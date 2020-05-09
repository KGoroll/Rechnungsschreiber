package Rechnungsschreiber;

import net.proteanit.sql.DbUtils;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.apache.log4j.BasicConfigurator;
import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JProgressBar;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;


public class GUI extends JFrame {
	private static final long serialVersionUID = -2107945385669135691L;
	private JTextField txtRechnungsnummer;
	private JTextField txtBauvorhaben;
	private JLabel lblRechnungsdatum;
	private JTextField txtRechnungsDatum;
	private JLabel lblKw;
	private JTextField txtKW;
	private JTextField txtBeschreibung;
	private JLabel lblBeschreibung;
	private JLabel lblBetrag;
	private JTextField txtBetrag;
	private JProgressBar progressBar;
    private String strDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    private DatenbankVerbindung dbVerbindung;
    private JPanel contentPane;
    private JPanel bearbeiten;
    private JLabel lblRechnungsnummer_b;
    private JTextField txtRechnungsnummer_b;
    private JTextField txtRechnungsdatum_b;
    private JTextField txtKW_b;
    private JTextField txtBauvorhaben_b;
    private JTextField txtBeschreibung_b;
    private JLabel lblBetrag_b;
    private JTextField txtBetrag_b;
    private JTable tableErgebnis;
    private JTextField umsatzErgebnis;
    private JTextField txtSearchQuery;
    private JTextField txtNeueKundennummer;
    private JTextField txtNeuerKundeName;
    private JTextField txtNeuerKundeStraße;
    private JTextField txtNeuerKundePlz;
    private JTextField txtNeuerKundeOrt;
    private JComboBox<Entry<String,String>> comboBox;
    private JRadioButton rdbtnNeuerKundeFirma;
    private JComboBox<Entry<String, String>> comboBox_b;
    private JPanel neueRechnung;
    private JPanel neuerKunde;
    private JPanel suchen;
    
    public GUI() {
    	initialize();
    }
 
    public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 
		
	}
    
    private void addNewTemplate() {
    	WordDocument dokument = new WordDocument(new RechnungsInfo());
    	TemplateInfo neueVorlage = new TemplateInfo();
		
		neueVorlage.setKundennummer(txtNeueKundennummer.getText());
		neueVorlage.setKundenname(txtNeuerKundeName.getText());
		neueVorlage.setKundenStraße(txtNeuerKundeStraße.getText());
		neueVorlage.setKundenPlz(txtNeuerKundePlz.getText());
		neueVorlage.setKundenOrt(txtNeuerKundeOrt.getText());
		neueVorlage.setIsFirma(rdbtnNeuerKundeFirma.isSelected());
		
		try {
			dokument.generateNewTemplate(neueVorlage);
			dbVerbindung.insertNewKunde(neueVorlage);
		} catch (Docx4JException e) {JOptionPane.showMessageDialog(neuerKunde, "Fehler beim Laden des Templates oder beim speichern der Word Datei \n" + e.toString());
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(neuerKunde, "Fehler beim einfügen des neuen Templates in die Datenbank \n Vorlage wurde wieder gelöscht \n" + e.toString());
			new File(dokument.getDocxAbsoulteFilePath()).delete();
		} catch (Exception e) {JOptionPane.showMessageDialog(neuerKunde, "Fehler beim erstellen der Vorlage in dem Fall Eingabe überprüfen \n oder in den code schauen \n" + e.toString());
		}
    }
    
    public void populateBearbeitenFeld(){
    	  WordDocument doc = null;
		try {
			doc = dbVerbindung.getRechnungsDaten(Integer.parseInt(txtRechnungsnummer_b.getText()));
		} catch (NumberFormatException e) {JOptionPane.showMessageDialog(bearbeiten, "Eingabe überprüfen \n" + e.toString());
		} catch (SQLException e) {JOptionPane.showMessageDialog(bearbeiten, "Fehler beim erfragen der Daten aus der Datenbank \n Datenbank überprüfen \n" + e.toString());
		}
    	  txtRechnungsnummer_b.setText(doc.getRechnungsDaten().getRechnungsNr());
    	  txtRechnungsdatum_b.setText(doc.getRechnungsDaten().getDatum());
    	  txtKW_b.setText(doc.getRechnungsDaten().getWoche());
    	  txtBauvorhaben_b.setText(doc.getRechnungsDaten().getBauvorhaben());
    	  txtBeschreibung_b.setText(doc.getRechnungsDaten().getBeschreibung());
    	  txtBetrag_b.setText(doc.getRechnungsDaten().getBetrag());
    	  
    	  comboBox_b.removeAllItems();
    	  addItems(comboBox_b);
    }
    
    @SuppressWarnings("unchecked")
	public void bearbeiten() {
    	WordDocument dokument = new WordDocument(new RechnungsInfo());
    	
    	dokument.getRechnungsDaten().setRechnungsNr(txtRechnungsnummer_b.getText());
    	dokument.getRechnungsDaten().setDatum(txtRechnungsdatum_b.getText());
		dokument.getRechnungsDaten().setWoche(txtKW_b.getText());
		dokument.getRechnungsDaten().setBauvorhaben(txtBauvorhaben_b.getText());
		dokument.getRechnungsDaten().setBetrag(txtBetrag_b.getText());
		dokument.getRechnungsDaten().setBeschreibung(txtBeschreibung_b.getText());
		dokument.getRechnungsDaten().setKundennummer(((Entry<String,String>) comboBox_b.getSelectedItem()).getValue());
    	
		String[] paths = null;
		try {
			paths = dbVerbindung.getFilePaths(Integer.parseInt(dokument.getRechnungsDaten().getRechnungsNr()));
		} catch (NumberFormatException | SQLException e) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim erhalten der Pfade zum bearbeiten. Rechnungsnummer überprüfen oder Datenbank \n" + e.toString());}
    	
    	File pdfFile = new File(paths[0]);
    	File wordFile = new File(paths[1]);
    	
    	if(pdfFile.delete())
    		System.out.println("Pdf File wurde gelöscht");
    	else 
    		System.out.println("Pdf File konnte nicht gelöscht werden");
    	
    	if(wordFile.delete())
    		System.out.println("Word File wurde gelöscht");
    	else 
    		System.out.println("Word File konnte nicht gelöscht werden");
    	
    	try {
			dbVerbindung.Rechnunglöschen(Integer.parseInt(dokument.getRechnungsDaten().getRechnungsNr()));
			erstelleNeueRechnung(dokument);
    	} catch (SQLException e) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim löschen der alten Rechnung \n Rechnung konnte nicht aus der Datenbank entfernt werden \n" + e.toString());
		}
    	
    	
    }
    
    @SuppressWarnings("unchecked")
	public void fertigstellen() {
    	WordDocument dokument = new WordDocument(new RechnungsInfo());
    	
    	
		dokument.getRechnungsDaten().setRechnungsNr(txtRechnungsnummer.getText());
		dokument.getRechnungsDaten().setDatum(txtRechnungsDatum.getText());
		dokument.getRechnungsDaten().setWoche(txtKW.getText());
		dokument.getRechnungsDaten().setBauvorhaben(txtBauvorhaben.getText());
		dokument.getRechnungsDaten().setBetrag(txtBetrag.getText());
		dokument.getRechnungsDaten().setBeschreibung(txtBeschreibung.getText());
		dokument.getRechnungsDaten().setKundennummer(((Entry<String,String>) comboBox.getSelectedItem()).getValue());
		
		erstelleNeueRechnung(dokument);
   }
    
    public void erstelleNeueRechnung(WordDocument dokument) {
    	Convert convert = null;
    	try {
			dokument.generateDocxFileFromTemplate();
			convert = new Convert(dokument);
			dbVerbindung.insertNeueRechnung(dokument,convert);	
		} 
		catch (Docx4JException e) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim Laden des Templates oder beim speichern der Word Datei \n" + e.toString());} 
		catch (FileNotFoundException e) {JOptionPane.showMessageDialog(neueRechnung, "Fehler. Template nicht vorhanden \n" + e.toString());}
		catch (IOException e) {
			 //if(new File("D:\\Benutzer\\Desktop\\Rechnungen\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".docx").delete())
			if(new File(dokument.getDocxAbsoulteFilePath()).delete())
				 JOptionPane.showMessageDialog(neueRechnung, "Fehler beim konvertieren. Word Datei wurde wieder gelöscht \n" + e.toString());
			 else
				 JOptionPane.showMessageDialog(neueRechnung, "Fehler beim konvertieren. Word Datei konnte nicht gelöscht werden \n" + e.toString());
		}
		catch (NumberFormatException | SQLException e) {
			new File("D:\\Benutzer\\Desktop\\Rechnungen\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".docx").delete();
			new File("D:\\Benutzer\\Desktop\\Rechnungen\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".pdf").delete();
			JOptionPane.showMessageDialog(neueRechnung, "Fehler beim einsetzen in die Datenbank. Dateien wurden wieder gelöscht. Eingabe überprüfen \n" + e.toString());
		}
		catch (Exception e) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim fertigstellen. In diesem Fall in den Code schauen \n" + e.toString());}
    }
    
    public void addItems(JComboBox<Entry<String,String>> comboBox) {
    	HashMap<String, String> KundenNameNummer = null;
		try {
			KundenNameNummer = dbVerbindung.retrieveKundennameUndNummer();
		} catch (SQLException e) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim erstellen der ComboBox. Datenbanküberprüfen \n" + e.toString());
		}
    	for(Entry<String,String> m : KundenNameNummer.entrySet()) {
    		comboBox.addItem(m);
    	}
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initialize() {
		try {
			dbVerbindung = new DatenbankVerbindung();
		} catch (SQLException e3) {
			JOptionPane.showMessageDialog(neueRechnung, "Eine Verbindung mit der Datenbank konnte nicht hergestellt werden \n Datenbank überprüfen \n" + e3.toString());
			throw new Error("Datenbankverbindung fehlgeschlagen");
		}
		
		setForeground(new Color(0, 0, 0));
		setBackground(new Color(51, 51, 51));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 818, 673);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(204, 204, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 792, 623);
		contentPane.add(tabbedPane);
		
		neueRechnung = new JPanel();
		neueRechnung.setBorder(null);
		neueRechnung.setBackground(new Color(204,204,204));
		tabbedPane.addTab("Neue Rechnung", null, neueRechnung, null);
		neueRechnung.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(113, 363, 146, 17);
		neueRechnung.add(progressBar);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		JButton btnffnen = new JButton("Öffnen");
		btnffnen.setBounds(137, 404, 96, 23);
		neueRechnung.add(btnffnen);
		
		txtBetrag = new JTextField();
		txtBetrag.setBounds(10, 323, 86, 20);
		neueRechnung.add(txtBetrag);
		txtBetrag.setColumns(10);
		
		JButton btnDrucken = new JButton("Drucken");
		btnDrucken.setBounds(10, 404, 89, 23);
		neueRechnung.add(btnDrucken);
		
		JButton btnFertig = new JButton("Fertig");
		btnFertig.setBounds(10, 360, 89, 23);
		neueRechnung.add(btnFertig);
		
		
		lblBetrag = new JLabel("Betrag Netto");
		lblBetrag.setBounds(10, 305, 110, 14);
		neueRechnung.add(lblBetrag);
		
		lblBeschreibung = new JLabel("Beschreibung");
		lblBeschreibung.setBounds(10, 210, 110, 14);
		neueRechnung.add(lblBeschreibung);
		
		txtBeschreibung = new JTextField();
		txtBeschreibung.setBounds(10, 224, 388, 54);
		neueRechnung.add(txtBeschreibung);
		txtBeschreibung.setColumns(10);
		
		txtKW = new JTextField();
		txtKW.setBounds(10, 121, 110, 20);
		neueRechnung.add(txtKW);
		txtKW.setColumns(10);
		
		lblKw = new JLabel("KW");
		lblKw.setBounds(10, 104, 110, 14);
		neueRechnung.add(lblKw);
		
		txtRechnungsDatum = new JTextField();
		txtRechnungsDatum.setBounds(10, 76, 110, 20);
		neueRechnung.add(txtRechnungsDatum);
		txtRechnungsDatum.setText(strDate);
		txtRechnungsDatum.setColumns(10);
		
		lblRechnungsdatum = new JLabel("Rechnungsdatum");
		lblRechnungsdatum.setBounds(10, 60, 135, 14);
		neueRechnung.add(lblRechnungsdatum);
		
		txtBauvorhaben = new JTextField();
		txtBauvorhaben.setBounds(10, 175, 388, 20);
		neueRechnung.add(txtBauvorhaben);
		txtBauvorhaben.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Bauvorhaben");
		lblNewLabel.setBounds(10, 157, 110, 14);
		neueRechnung.add(lblNewLabel);
		
		JLabel lblRechnungsnummer = new JLabel("Rechnungsnummer");
		lblRechnungsnummer.setBounds(10, 11, 158, 14);
		neueRechnung.add(lblRechnungsnummer);
		
		txtRechnungsnummer = new JTextField();
		txtRechnungsnummer.setBounds(10, 30, 110, 20);
		neueRechnung.add(txtRechnungsnummer);
		try {
			txtRechnungsnummer.setText(dbVerbindung.retrieveNextRechnungsnummer());
		} catch (SQLException e2) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim erfragen der nächsten Rechnungsnummer \n" + e2.toString());
		}
		txtRechnungsnummer.setColumns(10);
		
		JLabel lblKunde = new JLabel("Kunde");
		lblKunde.setBounds(178, 11, 110, 14);
		neueRechnung.add(lblKunde);
		
		
		comboBox = new JComboBox();
		comboBox.setBounds(178, 29, 319, 21);
		neueRechnung.add(comboBox);
		
		addItems(comboBox);
		
		btnFertig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progressBar.setValue(0);
				fertigstellen();
				progressBar.setValue(100);
			}
		});
		btnDrucken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dbVerbindung.druckeRechnung(Integer.parseInt(txtRechnungsnummer.getText()));
				} catch (NumberFormatException e1) {JOptionPane.showMessageDialog(neueRechnung, "Rechnungsnummer überprüfen \n");
				} catch (IOException e1) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim Laden der PDF Datei. Pfad überprüfen \n" + e1.toString());
				} catch (PrinterException e1) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim Drucken. Drucker oder so überprüfen \n" + e1.toString());
				} catch (SQLException e1) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim erfragen der Daten aus der Datenbank \n" + e1.toString());
				}
			}
		});
		btnffnen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dbVerbindung.öffneRechnung(Integer.parseInt(txtRechnungsnummer.getText()));
				} catch (NumberFormatException e1) {JOptionPane.showMessageDialog(neueRechnung, "Rechnungsnummer überprüfen \n");
				} catch (SQLException e1) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim erfragen des Pfades der PDF Datei \n" + e1.toString());
				} catch (IOException e1) {JOptionPane.showMessageDialog(neueRechnung, "Fehler beim öffnen. Datei nicht vorhanden \n" + e1.toString());
				}
			}
		});
		
		suchen = new JPanel();
		tabbedPane.addTab("Suchen", null, suchen, null);
		suchen.setLayout(null);
		
		JButton btnSuchen = new JButton("Suchen");
		JTextField txtSuchen = new JTextField();
		txtSuchen.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					btnSuchen.doClick();
			}
		});
		txtSuchen.setBounds(10, 11, 523, 20);
		suchen.add(txtSuchen);
		txtSuchen.setColumns(10);
		
		
		btnSuchen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					tableErgebnis.setModel(DbUtils.resultSetToTableModel(dbVerbindung.search(txtSuchen.getText())));
				} catch (SQLException e1) {JOptionPane.showMessageDialog(suchen, "Kein Ergebnis \n" + e1.toString());
				}
			}
		});
		btnSuchen.setBounds(543, 10, 89, 23);
		suchen.add(btnSuchen);
		
		tableErgebnis = new JTable();
		JScrollPane scrollPane = new JScrollPane(tableErgebnis);
		scrollPane.setBounds(10, 76, 749, 450);
		suchen.add(scrollPane);
		
		String[] monate = {"Januar", "Februar", "März", "April", "Mai", "Juni","Juli", "Augusut","September","Oktober","November","Dezember"};
		Integer[] jahre = {2019,2020};
		
		JLabel lblUmsatzVon = new JLabel("Umsatz von ");
		lblUmsatzVon.setBounds(10, 537, 104, 29);
		suchen.add(lblUmsatzVon);
		
		JComboBox<String> monatVon = new JComboBox(monate);
		monatVon.setBounds(91, 540, 111, 22);
		suchen.add(monatVon);
		
		JComboBox<Integer> jahrVon = new JComboBox(jahre);
		jahrVon.setBounds(212, 540, 95, 22);
		suchen.add(jahrVon);
		
		JLabel lblBis = new JLabel("bis");
		lblBis.setBounds(317, 544, 46, 14);
		suchen.add(lblBis);
		
		JComboBox<String >monatBis = new JComboBox(monate);
		monatBis.setBounds(347, 540, 111, 22);
		suchen.add(monatBis);
		
		JComboBox<Integer> jahrBis = new JComboBox(jahre);
		jahrBis.setBounds(468, 540, 95, 22);
		suchen.add(jahrBis);
		
		umsatzErgebnis = new JTextField();
		umsatzErgebnis.setBounds(635, 541, 124, 20);
		suchen.add(umsatzErgebnis);
		umsatzErgebnis.setColumns(10);
		
		JButton btnPfeil = new JButton("->");
		btnPfeil.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String monatV, monatB;
				Integer jahrV,jahrB;
				monatV = (String)monatVon.getSelectedItem();
				jahrV =  (Integer)jahrVon.getSelectedItem();
				monatB =  (String)monatBis.getSelectedItem();
				jahrB =  (Integer)jahrBis.getSelectedItem();
				float umsatz = 0;
				try {
					umsatz = dbVerbindung.getUmsatz(monatV,jahrV,monatB,jahrB);
				} catch (SQLException e1) {JOptionPane.showMessageDialog(suchen, "Fehler beim erfragen des Umsatzes \n In dem Fall besteht ein Problem mit der Datenbank \n" + e1.toString());}
				
				umsatzErgebnis.setText(String.valueOf(umsatz));
			}
		});
		btnPfeil.setBounds(573, 540, 52, 23);
		suchen.add(btnPfeil);
		
		JButton btnQuery = new JButton("Query");
		btnQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					tableErgebnis.setModel(DbUtils.resultSetToTableModel(dbVerbindung.searchByQuery(txtSearchQuery.getText(), suchen)));
				} catch (SQLException e1) {JOptionPane.showMessageDialog(suchen, "Query fehlerhaft \n" + e1.toString());
				}
			}
		});
		btnQuery.setBounds(543, 42, 89, 23);
		suchen.add(btnQuery);
		
		txtSearchQuery = new JTextField();
		txtSearchQuery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					btnQuery.doClick();
			}
		});
		txtSearchQuery.setBounds(10, 42, 523, 20);
		suchen.add(txtSearchQuery);
		txtSearchQuery.setColumns(10);
		
		bearbeiten = new JPanel();
		tabbedPane.addTab("Bearbeiten", null, bearbeiten, null);
		bearbeiten.setBackground(new Color(204,204,204));
		bearbeiten.setLayout(null);
		
		lblRechnungsnummer_b = new JLabel("Rechnungsnummer");
		lblRechnungsnummer_b.setBounds(10, 11, 91, 14);
		bearbeiten.add(lblRechnungsnummer_b);
		
		txtRechnungsnummer_b = new JTextField();
		txtRechnungsnummer_b.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					populateBearbeitenFeld();
				}
			}
		});
		txtRechnungsnummer_b.setColumns(10);
		txtRechnungsnummer_b.setBounds(10, 28, 110, 20);
		bearbeiten.add(txtRechnungsnummer_b);
		
		JLabel lblRechnungsdatum_b = new JLabel("Rechnungsdatum");
		lblRechnungsdatum_b.setBounds(10, 64, 135, 14);
		bearbeiten.add(lblRechnungsdatum_b);
		
		txtRechnungsdatum_b = new JTextField();
		txtRechnungsdatum_b.setColumns(10);
		txtRechnungsdatum_b.setBounds(10, 78, 110, 20);
		bearbeiten.add(txtRechnungsdatum_b);
		
		JLabel lblKw_b = new JLabel("KW");
		lblKw_b.setBounds(10, 109, 110, 14);
		bearbeiten.add(lblKw_b);
		
		txtKW_b = new JTextField();
		txtKW_b.setColumns(10);
		txtKW_b.setBounds(10, 121, 110, 20);
		bearbeiten.add(txtKW_b);
		
		JLabel lblNewLabel_b = new JLabel("Bauvorhaben");
		lblNewLabel_b.setBounds(10, 152, 110, 14);
		bearbeiten.add(lblNewLabel_b);
		
		txtBauvorhaben_b = new JTextField();
		txtBauvorhaben_b.setColumns(10);
		txtBauvorhaben_b.setBounds(10, 165, 388, 20);
		bearbeiten.add(txtBauvorhaben_b);
		
		JLabel lblBeschreibung_b = new JLabel("Beschreibung");
		lblBeschreibung_b.setBounds(10, 196, 110, 14);
		bearbeiten.add(lblBeschreibung_b);
		
		txtBeschreibung_b = new JTextField();
		txtBeschreibung_b.setColumns(10);
		txtBeschreibung_b.setBounds(10, 210, 388, 54);
		bearbeiten.add(txtBeschreibung_b);
		
		lblBetrag_b = new JLabel("Betrag Netto");
		lblBetrag_b.setBounds(10, 287, 110, 14);
		bearbeiten.add(lblBetrag_b);
		
		txtBetrag_b = new JTextField();
		txtBetrag_b.setColumns(10);
		txtBetrag_b.setBounds(10, 301, 86, 20);
		bearbeiten.add(txtBetrag_b);
		
		JButton btnBearbeiten = new JButton("Bearbeiten");
		btnBearbeiten.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bearbeiten();
			}
		});
		btnBearbeiten.setBounds(10, 358, 89, 23);
		bearbeiten.add(btnBearbeiten);
		
		comboBox_b = new JComboBox();
		comboBox_b.setBounds(130, 28, 310, 21);
		bearbeiten.add(comboBox_b);
		
		JLabel lblKunde_b = new JLabel("Kunde");
		lblKunde_b.setBounds(130, 11, 74, 14);
		bearbeiten.add(lblKunde_b);
		
		neuerKunde = new JPanel();
		tabbedPane.addTab("Neuer Kunde", null, neuerKunde, null);
		neuerKunde.setLayout(null);
		
		JLabel lblNeueKundennummer = new JLabel("Kundennummer");
		lblNeueKundennummer.setBounds(32, 11, 106, 14);
		neuerKunde.add(lblNeueKundennummer);
		
		txtNeueKundennummer = new JTextField();
		txtNeueKundennummer.setBounds(32, 24, 312, 20);
		neuerKunde.add(txtNeueKundennummer);
		txtNeueKundennummer.setColumns(10);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(32, 115, 106, 14);
		neuerKunde.add(lblName);
		
		txtNeuerKundeName = new JTextField();
		txtNeuerKundeName.setBounds(32, 140, 312, 20);
		neuerKunde.add(txtNeuerKundeName);
		txtNeuerKundeName.setColumns(10);
		
		JLabel lblKundenStraße = new JLabel("Straße");
		lblKundenStraße.setBounds(32, 171, 46, 14);
		neuerKunde.add(lblKundenStraße);
		
		txtNeuerKundeStraße = new JTextField();
		txtNeuerKundeStraße.setBounds(32, 196, 312, 20);
		neuerKunde.add(txtNeuerKundeStraße);
		txtNeuerKundeStraße.setColumns(10);
		
		JLabel lblPlz = new JLabel("PLZ");
		lblPlz.setBounds(32, 227, 46, 14);
		neuerKunde.add(lblPlz);
		
		JLabel lblOrt = new JLabel("Ort");
		lblOrt.setBounds(32, 283, 46, 14);
		neuerKunde.add(lblOrt);
		
		txtNeuerKundePlz = new JTextField();
		txtNeuerKundePlz.setBounds(32, 252, 312, 20);
		neuerKunde.add(txtNeuerKundePlz);
		txtNeuerKundePlz.setColumns(10);
		
		txtNeuerKundeOrt = new JTextField();
		txtNeuerKundeOrt.setBounds(32, 308, 312, 20);
		neuerKunde.add(txtNeuerKundeOrt);
		txtNeuerKundeOrt.setColumns(10);
		
		rdbtnNeuerKundeFirma = new JRadioButton("Firma");
		rdbtnNeuerKundeFirma.setBounds(29, 62, 109, 23);
		neuerKunde.add(rdbtnNeuerKundeFirma);
		
		JButton btnNeu = new JButton("Neu");
		btnNeu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addNewTemplate();
			}
		});
		btnNeu.setBounds(36, 373, 89, 23);
		neuerKunde.add(btnNeu);
		
		JButton btnÖffneVorlage = new JButton("Öffne Vorlage");
		btnÖffneVorlage.setBounds(237, 373, 106, 23);
		neuerKunde.add(btnÖffneVorlage);
		
		JRadioButton rdbtnNeuerKundePrivat = new JRadioButton("Privat");
		rdbtnNeuerKundePrivat.setBounds(154, 62, 109, 23);
		neuerKunde.add(rdbtnNeuerKundePrivat);
		
		ButtonGroup NeuerKundeButtons = new ButtonGroup();
		NeuerKundeButtons.add(rdbtnNeuerKundeFirma);
		NeuerKundeButtons.add(rdbtnNeuerKundePrivat);
	}
}