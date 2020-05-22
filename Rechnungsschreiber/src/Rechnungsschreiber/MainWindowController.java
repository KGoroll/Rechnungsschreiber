package Rechnungsschreiber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import net.proteanit.sql.DbUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MainWindowController {
	
	
	// Views
	@FXML private TextField rechnungsnummer;
	@FXML private TextField leistungszeitraum;
	@FXML private TextField bauvorhaben;
	@FXML private TextField betrag;
	@FXML private TextField beschreibung;
	@FXML private ComboBox<Entry<String,String>> kunde;
	@FXML private DatePicker rechnungsdatum;
	@FXML private TextField kundennummer;
	@FXML private TextField kundenName;
	@FXML private TextField kundenStraße;
	@FXML private TextField kundenPlz;
	@FXML private TextField kundenOrt;
	@FXML private TextField sucheInTabelle;
	@FXML private TableView übersichtTabelle;
	@FXML private RadioButton radioKundeIsFirma;
	@FXML private RadioButton radioKundeIsPrivat;
	
	public Main main;
	private DatenbankVerbindung dbVerbindung;
	
	public void initialize() {
		try {
			dbVerbindung = new DatenbankVerbindung();
		} catch (SQLException e3) {
			Alert alert = new Alert(AlertType.ERROR, "Eine Verbindung mit der Datenbank konnte nicht hergestellt werden \n Datenbank überprüfen \n" + e3.toString());
			alert.show();
			throw new Error("Datenbankverbindung fehlgeschlagen");
		}
		übersichtTabelle.setModel(DbUtils.resultSetToTableModel(dbVerbindung.search(sucheInTabelle.getText())));
		addItems(kunde);
	}
	
	public void fertigstellen() {
    	WordDocument dokument = new WordDocument(new RechnungsInfo());
    	
		dokument.getRechnungsDaten().setRechnungsNr(rechnungsnummer.getText());
		String datum = rechnungsdatum.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		dokument.getRechnungsDaten().setDatum(datum);
		dokument.getRechnungsDaten().setWoche(leistungszeitraum.getText());
		dokument.getRechnungsDaten().setBauvorhaben(bauvorhaben.getText());
		dokument.getRechnungsDaten().setBetrag(betrag.getText());
		dokument.getRechnungsDaten().setBeschreibung(beschreibung.getText());
		dokument.getRechnungsDaten().setKundennummer(kunde.getValue().getValue());
		
		erstelleNeueRechnung(dokument); 
   }
	
	public void erstelleNeueRechnung(WordDocument dokument) {
	    Convert convert = null;
	    try {
	    	
			dokument.generateDocxFileFromTemplate();
			convert = new Convert(dokument);
			//dbVerbindung.insertNeueRechnung(dokument,convert);	
			
		} 
		catch (Docx4JException e) {
			Alert alert = new Alert(AlertType.ERROR, "Fehler beim Laden des Templates oder beim speichern der Word Datei \n" + e.toString());
			alert.show();} 
		catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR,"Fehler. Template nicht vorhanden \n" + e.toString());
			alert.show();}
		catch (IOException e) {
			if(new File(dokument.getDocxAbsoulteFilePath()).delete()) {
				Alert alert = new Alert(AlertType.ERROR,"Fehler beim konvertieren. Word Datei wurde wieder gelöscht \n" + e.toString());
				alert.show();
			}
			else {
				Alert alert = new Alert(AlertType.ERROR, "Fehler beim konvertieren. Word Datei konnte nicht gelöscht werden \n" + e.toString());
				alert.show();
			}
		}
		catch (NumberFormatException | SQLException e) {
			new File("D:\\Benutzer\\Desktop\\Rechnungen\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".docx").delete();
			new File("D:\\Benutzer\\Desktop\\Rechnungen\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".pdf").delete();
			Alert alert = new Alert(AlertType.ERROR, "Fehler beim einsetzen in die Datenbank. Dateien wurden wieder gelöscht. Eingabe überprüfen \n" + e.toString());
			alert.show();
		}
		catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR, "Fehler beim fertigstellen. In diesem Fall in den Code schauen \n" + e.toString());
			alert.show();
		}
	}
	
	public void druckeRechnung() {
		
	}
	
	//Zum kopieren
	//Alert alert = new Alert(AlertType.ERROR, );
	//alert.show()
	
	public void öffneRechnung() {
		
	}
	
	public void bearbeiteRechnung() {
		
	}
	
	public void addNewTemplate() {
	    	WordDocument dokument = new WordDocument(new RechnungsInfo());
	    	TemplateInfo neueVorlage = new TemplateInfo();
			
			neueVorlage.setKundennummer(kundennummer.getText());
			neueVorlage.setKundenname(kundenName.getText());
			neueVorlage.setKundenStraße(kundenStraße.getText());
			neueVorlage.setKundenPlz(kundenPlz.getText());
			neueVorlage.setKundenOrt(kundenOrt.getText());
			neueVorlage.setIsFirma(radioKundeIsFirma.isSelected());
			
			try {
				dokument.generateNewTemplate(neueVorlage);
				dbVerbindung.insertNewKunde(neueVorlage);
			} catch (Docx4JException e) {
				Alert alert = new Alert(AlertType.ERROR, "Fehler beim Laden des Templates oder beim speichern der Word Datei \n" + e.toString());
				alert.show();
			} catch (SQLException e) {
				Alert alert = new Alert(AlertType.ERROR,"Fehler beim einfügen des neuen Templates in die Datenbank \n Vorlage wurde wieder gelöscht \n" + e.toString() );
				alert.show();
				new File(dokument.getDocxAbsoulteFilePath()).delete();
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR, "Fehler beim erstellen der Vorlage in dem Fall Eingabe überprüfen \n oder in den code schauen \n" + e.toString());
				alert.show();
			}
	}
	
	public void addItems(ComboBox<Entry<String,String>> comboBox) {
	    	HashMap<String, String> KundenNameNummer = null;
			try {
				KundenNameNummer = dbVerbindung.retrieveKundennameUndNummer();
			} catch (SQLException e) {/*JOptionPane.showMessageDialog(neueRechnung, "Fehler beim erstellen der ComboBox. Datenbanküberprüfen \n" + e.toString())*/;
			}
			
	    	for(Entry<String,String> m : KundenNameNummer.entrySet()) {
	    		comboBox.getItems().add(m);
	    	}
	    }
	
	public void setMain(Main main) {
		this.main = main;
	}
	
}
