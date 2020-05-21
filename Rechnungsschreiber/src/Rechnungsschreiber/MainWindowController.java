package Rechnungsschreiber;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Tab;
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
	@FXML private Tab neueRechnung;
	
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
	Alert alert = new Alert(AlertType.ERROR, );
	alert.show()
	
	public void öffneRechnung() {
		
	}
	
	public void bearbeiteRechnung() {
		
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
