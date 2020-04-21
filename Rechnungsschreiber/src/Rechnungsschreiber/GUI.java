package Rechnungsschreiber;

import net.proteanit.sql.DbUtils;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JProgressBar;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;


@SuppressWarnings("serial")
public class GUI extends JFrame {
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
    private Convert convert;
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
    
    public GUI(WordDocument dokument) {
    	initialize(dokument);
    }
    
    public void populateBearbeitenFeld(){
    	WordDocument doc = dbVerbindung.getRechnungsDaten(Integer.parseInt(txtRechnungsnummer_b.getText()));
    	  txtRechnungsnummer_b.setText(doc.getRechnungsDaten().getRechnungsNr());
    	  txtRechnungsdatum_b.setText(doc.getRechnungsDaten().getDatum());
    	  txtKW_b.setText(doc.getRechnungsDaten().getWoche());
    	  txtBauvorhaben_b.setText(doc.getRechnungsDaten().getBauvorhaben());
    	  txtBeschreibung_b.setText(doc.getRechnungsDaten().getBeschreibung());
    	  txtBetrag_b.setText(doc.getRechnungsDaten().getBetrag());
    }
    
    public void bearbeiten(WordDocument dokument) {
    	dokument.getRechnungsDaten().setRechnungsNr(txtRechnungsnummer_b.getText());
    	dokument.getRechnungsDaten().setDatum(txtRechnungsdatum_b.getText());
		dokument.getRechnungsDaten().setWoche(txtKW_b.getText());
		dokument.getRechnungsDaten().setBauvorhaben(txtBauvorhaben_b.getText());
		dokument.getRechnungsDaten().setBetrag(txtBetrag_b.getText());
		dokument.getRechnungsDaten().setBeschreibung(txtBeschreibung_b.getText());
		
    	String[] paths = dbVerbindung.getFilePaths(Integer.parseInt(dokument.getRechnungsDaten().getRechnungsNr()));
    	
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
    	
    	dbVerbindung.Rechnunglöschen(Integer.parseInt(dokument.getRechnungsDaten().getRechnungsNr()));
    	
    	try {
			dokument.generateDocxFileFromTemplate();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	convert = new Convert(dokument);
    	dbVerbindung.insertNeueRechnung(dokument, convert);
    }
    
    public void fertigstellen(WordDocument dokument) {
    	
		dokument.getRechnungsDaten().setRechnungsNr(txtRechnungsnummer.getText());
		dokument.getRechnungsDaten().setDatum(txtRechnungsDatum.getText());
		dokument.getRechnungsDaten().setWoche(txtKW.getText());
		dokument.getRechnungsDaten().setBauvorhaben(txtBauvorhaben.getText());
		dokument.getRechnungsDaten().setBetrag(txtBetrag.getText());
		dokument.getRechnungsDaten().setBeschreibung(txtBeschreibung.getText());
		
		try {
			dokument.generateDocxFileFromTemplate();
			convert = new Convert(dokument);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
			dbVerbindung.insertNeueRechnung(dokument,convert);	
	}
    
    public void addItems(JComboBox comboBox) {
    	HashMap<String,String> KundenNameNummer = dbVerbindung.retrieveKundennameUndNummer();
    	for(Map.Entry m : KundenNameNummer.entrySet()) {
    		comboBox.addItem(m.getKey());
    	}
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void initialize(WordDocument dokument) {
		dbVerbindung = new DatenbankVerbindung();
		
		setForeground(new Color(0, 0, 0));
		setBackground(new Color(51, 51, 51));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 818, 673);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(204, 204, 204));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		ButtonGroup RechnungsArt = new ButtonGroup();
		ButtonGroup RechnungsArt_b = new ButtonGroup();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 792, 623);
		contentPane.add(tabbedPane);
		
		JPanel NeueRechnung = new JPanel();
		NeueRechnung.setBorder(null);
		NeueRechnung.setBackground(new Color(204,204,204));
		tabbedPane.addTab("Neue Rechnung", null, NeueRechnung, null);
		NeueRechnung.setLayout(null);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(113, 363, 146, 17);
		NeueRechnung.add(progressBar);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		
		JButton btnffnen = new JButton("Öffnen");
		btnffnen.setBounds(137, 404, 96, 23);
		NeueRechnung.add(btnffnen);
		
		JRadioButton rdbtnPrivat = new JRadioButton("Privat");
		rdbtnPrivat.setBounds(319, 100, 79, 23);
		NeueRechnung.add(rdbtnPrivat);
		rdbtnPrivat.setSelected(true);
		rdbtnPrivat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dokument.getRechnungsDaten().setRechnungsartIsFirma(false);
			}
		});
		RechnungsArt.add(rdbtnPrivat);
		
		
		JRadioButton rdbtnFirma = new JRadioButton("Firma");
		rdbtnFirma.setBounds(238, 100, 79, 23);
		NeueRechnung.add(rdbtnFirma);
		rdbtnFirma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dokument.getRechnungsDaten().setRechnungsartIsFirma(true);
			}
		});
		RechnungsArt.add(rdbtnFirma);
		
		txtBetrag = new JTextField();
		txtBetrag.setBounds(10, 323, 86, 20);
		NeueRechnung.add(txtBetrag);
		txtBetrag.setColumns(10);
		
		JButton btnDrucken = new JButton("Drucken");
		btnDrucken.setBounds(10, 404, 89, 23);
		NeueRechnung.add(btnDrucken);
		
		JButton btnFertig = new JButton("Fertig");
		btnFertig.setBounds(10, 360, 89, 23);
		NeueRechnung.add(btnFertig);
		
		lblBetrag = new JLabel("Betrag Netto");
		lblBetrag.setBounds(10, 305, 110, 14);
		NeueRechnung.add(lblBetrag);
		
		lblBeschreibung = new JLabel("Beschreibung");
		lblBeschreibung.setBounds(10, 210, 110, 14);
		NeueRechnung.add(lblBeschreibung);
		
		txtBeschreibung = new JTextField();
		txtBeschreibung.setBounds(10, 224, 388, 54);
		NeueRechnung.add(txtBeschreibung);
		txtBeschreibung.setColumns(10);
		
		txtKW = new JTextField();
		txtKW.setBounds(10, 121, 110, 20);
		NeueRechnung.add(txtKW);
		txtKW.setColumns(10);
		
		lblKw = new JLabel("KW");
		lblKw.setBounds(10, 104, 110, 14);
		NeueRechnung.add(lblKw);
		
		txtRechnungsDatum = new JTextField();
		txtRechnungsDatum.setBounds(10, 76, 110, 20);
		NeueRechnung.add(txtRechnungsDatum);
		txtRechnungsDatum.setText(strDate);
		txtRechnungsDatum.setColumns(10);
		
		lblRechnungsdatum = new JLabel("Rechnungsdatum");
		lblRechnungsdatum.setBounds(10, 60, 135, 14);
		NeueRechnung.add(lblRechnungsdatum);
		
		txtBauvorhaben = new JTextField();
		txtBauvorhaben.setBounds(10, 175, 388, 20);
		NeueRechnung.add(txtBauvorhaben);
		txtBauvorhaben.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Bauvorhaben");
		lblNewLabel.setBounds(10, 157, 110, 14);
		NeueRechnung.add(lblNewLabel);
		
		JLabel lblRechnungsnummer = new JLabel("Rechnungsnummer");
		lblRechnungsnummer.setBounds(10, 11, 158, 14);
		NeueRechnung.add(lblRechnungsnummer);
		
		txtRechnungsnummer = new JTextField();
		txtRechnungsnummer.setBounds(10, 30, 110, 20);
		NeueRechnung.add(txtRechnungsnummer);
		txtRechnungsnummer.setText(dbVerbindung.retrieveMaxRechnungsnummer());
		txtRechnungsnummer.setColumns(10);
		
		JLabel lblKunde = new JLabel("Kunde");
		lblKunde.setBounds(178, 11, 110, 14);
		NeueRechnung.add(lblKunde);
		
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(178, 29, 220, 21);
		NeueRechnung.add(comboBox);
		
		addItems(comboBox);
		
		btnFertig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				progressBar.setValue(0);
				fertigstellen(dokument);
				progressBar.setValue(100);
			}
		});
		btnDrucken.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					dbVerbindung.druckeRechnung(Integer.parseInt(txtRechnungsnummer.getText()));
				} catch (IOException | PrinterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnffnen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dbVerbindung.öffneRechnung(Integer.parseInt(txtRechnungsnummer.getText()));
			}
		});
		
		JPanel Suchen = new JPanel();
		tabbedPane.addTab("Suchen", null, Suchen, null);
		Suchen.setLayout(null);
		
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
		Suchen.add(txtSuchen);
		txtSuchen.setColumns(10);
		
		
		btnSuchen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableErgebnis.setModel(DbUtils.resultSetToTableModel(dbVerbindung.search(txtSuchen.getText(), Suchen)));
			}
		});
		btnSuchen.setBounds(543, 10, 89, 23);
		Suchen.add(btnSuchen);
		
		tableErgebnis = new JTable();
		JScrollPane scrollPane = new JScrollPane(tableErgebnis);
		scrollPane.setBounds(10, 76, 749, 450);
		Suchen.add(scrollPane);
		
		String[] monate = {"Januar", "Februar", "März", "April", "Mai", "Juni","Juli", "Augusut","September","Oktober","November","Dezember"};
		Integer[] jahre = {2019,2020};
		
		JLabel lblUmsatzVon = new JLabel("Umsatz von ");
		lblUmsatzVon.setBounds(10, 537, 104, 29);
		Suchen.add(lblUmsatzVon);
		
		JComboBox<String> monatVon = new JComboBox(monate);
		monatVon.setBounds(91, 540, 111, 22);
		Suchen.add(monatVon);
		
		JComboBox<Integer> jahrVon = new JComboBox(jahre);
		jahrVon.setBounds(212, 540, 95, 22);
		Suchen.add(jahrVon);
		
		JLabel lblBis = new JLabel("bis");
		lblBis.setBounds(317, 544, 46, 14);
		Suchen.add(lblBis);
		
		JComboBox<String >monatBis = new JComboBox(monate);
		monatBis.setBounds(347, 540, 111, 22);
		Suchen.add(monatBis);
		
		JComboBox<Integer> jahrBis = new JComboBox(jahre);
		jahrBis.setBounds(468, 540, 95, 22);
		Suchen.add(jahrBis);
		
		umsatzErgebnis = new JTextField();
		umsatzErgebnis.setBounds(635, 541, 124, 20);
		Suchen.add(umsatzErgebnis);
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
				float umsatz = dbVerbindung.getUmsatz(monatV,jahrV,monatB,jahrB);
				umsatzErgebnis.setText(String.valueOf(umsatz));
			}
		});
		btnPfeil.setBounds(573, 540, 52, 23);
		Suchen.add(btnPfeil);
		
		JButton btnQuery = new JButton("Query");
		btnQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableErgebnis.setModel(DbUtils.resultSetToTableModel(dbVerbindung.searchByQuery(txtSearchQuery.getText(), Suchen)));
			}
		});
		btnQuery.setBounds(543, 42, 89, 23);
		Suchen.add(btnQuery);
		
		txtSearchQuery = new JTextField();
		txtSearchQuery.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					btnQuery.doClick();
			}
		});
		txtSearchQuery.setBounds(10, 42, 523, 20);
		Suchen.add(txtSearchQuery);
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
		
		JRadioButton rdbtnFirma_b = new JRadioButton("Firma");
		rdbtnFirma_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dokument.getRechnungsDaten().setRechnungsartIsFirma(true);
			}
		});
		rdbtnFirma_b.setBounds(139, 27, 79, 23);
		bearbeiten.add(rdbtnFirma_b);
		RechnungsArt_b.add(rdbtnFirma_b);
		
		JRadioButton rdbtnPrivat_b = new JRadioButton("Privat");
		rdbtnPrivat_b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dokument.getRechnungsDaten().setRechnungsartIsFirma(false);
			}
		});
		rdbtnPrivat_b.setSelected(true);
		rdbtnPrivat_b.setBounds(220, 27, 79, 23);
		bearbeiten.add(rdbtnPrivat_b);
		RechnungsArt_b.add(rdbtnPrivat_b);
		
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
				bearbeiten(dokument);
			}
		});
		btnBearbeiten.setBounds(10, 358, 89, 23);
		bearbeiten.add(btnBearbeiten);
		
		JPanel neuerKunde = new JPanel();
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
		
		JRadioButton rdbtnNeuerKundeFirma = new JRadioButton("Firma");
		rdbtnNeuerKundeFirma.setBounds(29, 62, 109, 23);
		neuerKunde.add(rdbtnNeuerKundeFirma);
		
		JButton btnNeu = new JButton("Neu");
		btnNeu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TemplateInfo neueVorlage = new TemplateInfo();
				
				neueVorlage.setKundennummer(txtNeueKundennummer.getText());
				neueVorlage.setKundenname(txtNeuerKundeName.getText());
				neueVorlage.setKundenStraße(txtNeuerKundeStraße.getText());
				neueVorlage.setKundenPlz(txtNeuerKundePlz.getText());
				neueVorlage.setKundenOrt(txtNeuerKundeOrt.getText());
				neueVorlage.setIsFirma(rdbtnNeuerKundeFirma.isSelected());
				
				try {
					dokument.generateNewTemplate(neueVorlage);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//dbVerbindung.insertNewKunde(neueVorlage,TemplatePath);
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