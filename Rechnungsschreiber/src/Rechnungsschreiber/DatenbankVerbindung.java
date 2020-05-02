package Rechnungsschreiber;

import java.awt.Desktop;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JPanel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class DatenbankVerbindung {
	
	private Connection conn = null;
	private String user = "KRZYSZTOF";
	private String pass = "Ichbinswieder1";
	private String url = "jdbc:oracle:thin:@localhost:1521:orcl";
	
	
	DatenbankVerbindung() throws SQLException {	
		 
			DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
			conn = DriverManager.getConnection(url, user, pass);
			System.out.println("Connected!");
	}
	
	public void insertNeueRechnung(WordDocument dokument, Convert convert) throws SQLException, NumberFormatException {
	Statement stmt = null;
	int RechNr = Integer.parseInt(dokument.getRechnungsDaten().getRechnungsNr());
	String LeistungsZeitr = dokument.getRechnungsDaten().getWoche();
	String RechDatum = dokument.getRechnungsDaten().getDatum();
	String Kundennummer = dokument.getRechnungsDaten().getKundennummer();
	String Bauvorhaben = dokument.getRechnungsDaten().getBauvorhaben();
	float Betrag = Float.parseFloat(dokument.getRechnungsDaten().getBetrag().replace(',', '.'));
	String PdfDatei = convert.getPathToPdf();
	String WordDatei = convert.getPathToWord();
	String Beschreibung = dokument.getRechnungsDaten().getBeschreibung();

	String insertQuery = "insert into Rechnung values("+RechNr+",'"+LeistungsZeitr+"','"+RechDatum+"','"+Kundennummer+"','"+Bauvorhaben+"',"+Betrag+",'"+PdfDatei+"','"+WordDatei+"','"+Beschreibung+"')";	
			stmt = conn.createStatement();
			int m = stmt.executeUpdate(insertQuery);	
			if(m == 1)
				System.out.println("inserted Row to Rechnung:" + insertQuery);
			else
				System.out.println("insertion failed");
	}
	
	public String[] getFilePaths(int nummer) throws SQLException {
		Statement stmt = null;
		String query = "select pdfdatei,worddatei from rechnung where rechnungsnr = " + nummer;
		String[] paths = new String[2];
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			paths[0] = rs.getString(1);
			paths[1] = rs.getString(2);
		return paths;
	}
	
	public WordDocument getRechnungsDaten(int nummer) throws SQLException {
		RechnungsInfo daten = new RechnungsInfo();
		Statement stmt = null;
		String query = "select * from rechnung where rechnungsnr = " + nummer;
		
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		daten.setRechnungsNr(String.valueOf(rs.getInt(1)));
		daten.setWoche(rs.getString(2));
		LocalDateTime sqldatum = LocalDateTime.parse(rs.getString(3), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String datum = sqldatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		daten.setDatum(datum);
		daten.setBauvorhaben(rs.getString(5));
		daten.setBetrag(String.valueOf(rs.getFloat(6)).replace(".", ","));
		daten.setBeschreibung(rs.getString(9));
				
		WordDocument doc = new WordDocument(daten);
		return doc;
	}
	
	public String retrieveNextRechnungsnummer() throws SQLException {
		Statement stmt = null;
		String query = "select max(rechnungsnr) from Rechnung";
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			int RechNummer = rs.getInt(1);
			return String.valueOf(RechNummer+1);
	}
	
	public void öffneRechnung(int nummer) throws SQLException, IOException {
		Statement stmt = null;
		String query ="select pdfdatei from rechnung where rechnungsnr = " + nummer;
		
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			String pathToPdf = rs.getString(1);
			
			File PdfFile = new File(pathToPdf);
			Desktop desktop = Desktop.getDesktop();
			desktop.open(PdfFile);
		
	}

	public void druckeRechnung(int nummer) throws IOException, PrinterException, SQLException {
		Statement stmt = null;
		String query ="select pdfdatei from rechnung where rechnungsnr = " + nummer;
		
		String defaultPrinter = PrintServiceLookup.lookupDefaultPrintService().getName();
		System.out.println("Default Printer: " + defaultPrinter);
		PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			rs.next();
			String pathToPdf = rs.getString(1);
			
			PDDocument document = PDDocument.load(new File(pathToPdf));
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPageable(new PDFPageable(document));
			job.setPrintService(service);
			job.print();
			document.close();
	}

	public void Rechnunglöschen(int nummer) throws SQLException {
		Statement stmt = null;
		String query = "delete from rechnung where rechnungsnr = " + nummer;
		
			stmt = conn.createStatement();
			int m = stmt.executeUpdate(query);
			if (m == 1)
				System.out.println("Rechnung nummer " + nummer + " gelöscht");
			else 
				System.out.println("Rechnung nummer " + nummer + " konnte nicht gelöscht werden");
	}

	public ResultSet search(String sucheNach) throws SQLException {
		PreparedStatement statement;
	        	if (sucheNach.isEmpty()) {
	            	String query = "select rechnungsnr,leistungszeitraum as KW,rechdatum,kundennr,bauvorhaben,betrag,beschreibung from rechnung order by rechnungsnr";
	            	Statement stmt = conn.createStatement();
	            	ResultSet rs = stmt.executeQuery(query);
	            	return rs;
	        	}
	        	
	            statement = conn.prepareStatement("select rechnungsnr,leistungszeitraum as KW,rechdatum,kundennr,bauvorhaben,betrag,beschreibung"
	            		+ " from rechnung where Bauvorhaben like ? or Kundennr like ? or Leistungszeitraum like ? or beschreibung like ? order by rechnungsnr");
	            statement.setString(1, "%" + sucheNach + "%");
	            statement.setString(2, "%" + sucheNach + "%");
	            statement.setString(3, "%" + sucheNach + "%");
	            statement.setString(4, "%" + sucheNach + "%");
	            
	            return statement.executeQuery();	
	}

	public HashMap<String,String> getMonatZuKalenderWocheVon(){
		HashMap<String,String> monatZuKalenderWocheVon = new HashMap<String,String>();
		monatZuKalenderWocheVon.put("Januar", "01");
		monatZuKalenderWocheVon.put("Februar", "05");
		monatZuKalenderWocheVon.put("März", "10");
		monatZuKalenderWocheVon.put("April", "14");
		monatZuKalenderWocheVon.put("Mai", "19");
		monatZuKalenderWocheVon.put("Juni", "23");
		monatZuKalenderWocheVon.put("Juli", "27");
		monatZuKalenderWocheVon.put("August", "32");
		monatZuKalenderWocheVon.put("September", "36");
		monatZuKalenderWocheVon.put("Oktober", "41");
		monatZuKalenderWocheVon.put("November", "45");
		monatZuKalenderWocheVon.put("Dezember", "49");
		return monatZuKalenderWocheVon;
	}
	
	public HashMap<String,String> getMonatZuKalenderWocheBis(){
		HashMap<String,String> monatZuKalenderWocheBis = new HashMap<String,String>();
		monatZuKalenderWocheBis.put("Januar", "04");
		monatZuKalenderWocheBis.put("Februar", "09");
		monatZuKalenderWocheBis.put("März", "13");
		monatZuKalenderWocheBis.put("April", "18");
		monatZuKalenderWocheBis.put("Mai", "22");
		monatZuKalenderWocheBis.put("Juni", "26");
		monatZuKalenderWocheBis.put("Juli", "31");
		monatZuKalenderWocheBis.put("August", "35");
		monatZuKalenderWocheBis.put("September", "40");
		monatZuKalenderWocheBis.put("Oktober", "44");
		monatZuKalenderWocheBis.put("November", "48");
		monatZuKalenderWocheBis.put("Dezember", "53");
		return monatZuKalenderWocheBis;
	}
	
	public int getJahrToRechnungsnr(int jahr) {
		int nummer = 0;
		nummer = (jahr - 2000) * 1000;
		return nummer;
	}
	
	public float getUmsatz(String monatV, int jahrV, String monatB, int jahrB) throws SQLException {
		HashMap<String,String> monatZuKalenderWocheVon = getMonatZuKalenderWocheVon();
		HashMap<String,String> monatZuKalenderWocheBis = getMonatZuKalenderWocheBis();
		
		String WocheVon = monatZuKalenderWocheVon.get(monatV);
		String WocheBis = monatZuKalenderWocheBis.get(monatB);		
		
		jahrV = getJahrToRechnungsnr(jahrV);
		jahrB = getJahrToRechnungsnr(jahrB) + 1000;
		
		StringBuilder query = new StringBuilder("select sum(betrag) from rechnung where (");
		
		for (int i = Integer.valueOf(WocheVon); i <= Integer.valueOf(WocheBis); i++) {
			if (i == Integer.valueOf(WocheVon)) {
				query.append(" leistungszeitraum like '%");
				if (i < 10)
					query.append("0");
				query.append(i).append("%'");
				continue;
			}
			query.append(" or leistungszeitraum like '%");
			if (i < 10)
				query.append("0");
			query.append(i).append("%'");
		}
		query.append(") and ( rechnungsnr >= ").append(jahrV).append(" and rechnungsnr < ").append(jahrB).append(")");
		
		Statement stmt = null;
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query.toString());
		rs.next();
		float umsatz = rs.getFloat(1);
		return umsatz;
	}

	public ResultSet searchByQuery(String query, JPanel suchen) throws SQLException {
		Statement stmt = null;
		stmt = conn.createStatement();
		return stmt.executeQuery(query);
	}
	
	public HashMap<String,String> retrieveKundennameUndNummer() throws SQLException{
		HashMap<String,String> Map = new HashMap<String,String>();
		
		Statement stmt = null;
		String query = "select kundenname,kundennummer from kunde";
		ResultSet rs;
		stmt = conn.createStatement();
		rs = stmt.executeQuery(query);
		while (rs.next()) {
			Map.put(rs.getString(1),rs.getString(2));
		}
		return Map;
	}

	public void insertNewKunde(TemplateInfo neueVorlage) throws SQLException {
		Statement stmt = null;
		String Query = "insert into Kunde values('"+neueVorlage.getKundennummer()+"','"+neueVorlage.getKundenname()+"','"+neueVorlage.getKundenStraße()+"',"
				+ "'"+neueVorlage.getKundenPlz()+"','"+neueVorlage.getKundenOrt()+"')";	
			stmt = conn.createStatement();
			int m = stmt.executeUpdate(Query);	
			if(m == 1)
				System.out.println("inserted Row to Rechnung:" + Query);
			else
				System.out.println("insertion failed");
	} 
		
}
