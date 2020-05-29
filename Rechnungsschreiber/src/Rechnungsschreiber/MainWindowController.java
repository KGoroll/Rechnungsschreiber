package Rechnungsschreiber;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map.Entry;

import org.docx4j.openpackaging.exceptions.Docx4JException;

import com.sun.javafx.charts.Legend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

@SuppressWarnings({ "unchecked", "rawtypes" })
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
	@FXML private ProgressBar progressBarFertig;
	@FXML private ProgressIndicator progressIndicator;
	@FXML private LineChart<String,Number> umsatzChart;
	@FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private CheckBox checkUmsatz2019;
    @FXML private CheckBox checkUmsatz2020;
    @FXML private TabPane tabPane;
	private ObservableList<ObservableList> data;
	
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
		searchTable();
		
		sucheInTabelle.setOnKeyPressed(e -> {
		    if (e.getCode() == KeyCode.ENTER) {
		        searchTable();
		    }
		});
		rechnungsnummer.setOnKeyPressed(e -> {
		    if (e.getCode() == KeyCode.ENTER) {
		        fillRechnungsFeld();
		    }
		});
		
		rechnungsdatum.setValue(LocalDate.now());
		
		try {
			rechnungsnummer.setText(dbVerbindung.retrieveNextRechnungsnummer());
		} catch (SQLException e2) {
			Alert alert = new Alert(AlertType.ERROR, "Fehler beim erfragen der nächsten Rechnungsnummer \n" + e2.toString());
			alert.show();
		}
		
		progressBarFertig.setProgress(0.0);
		
		try {
			fillChart(2019);
			fillChart(2020);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (Node n : umsatzChart.getChildrenUnmodifiable()) {
		    if (n instanceof Legend) {
		        Legend l = (Legend) n;
		        for (Legend.LegendItem li : l.getItems()) {
		            for (XYChart.Series<String, Number> s : umsatzChart.getData()) {
		                if (s.getName().equals(li.getText())) {
		                    li.getSymbol().setCursor(Cursor.HAND); // Hint user that legend symbol is clickable
		                    li.getSymbol().setOnMouseClicked(me -> {
		                        if (me.getButton() == MouseButton.PRIMARY) {
		                            s.getNode().setVisible(!s.getNode().isVisible()); // Toggle visibility of line
		                            for (XYChart.Data<String, Number> d : s.getData()) {
		                                if (d.getNode() != null) {
		                                    d.getNode().setVisible(s.getNode().isVisible()); // Toggle visibility of every node in the series
		                                }
		                            }
		                        }
		                    });
		                    break;
		                }
		            }
		        }
		    }
		}
		
		getSelectedCell();
	}
	
	//Zum kopieren
	//Alert alert = new Alert(AlertType.ERROR, );
	//alert.show();
	
	public void fillChart(int year) throws SQLException {
		HashMap<String,Integer> monatZuUmsatz = new HashMap<String,Integer>();
		float sum = 0;
		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		series.setName("Umsatz " + year);
	    umsatzChart.setTitle("Umsatz"); 
	    
		String[] monate = {"Januar", "Februar", "März", "April", "Mai", "Juni","Juli", "August","September","Oktober","November","Dezember"};
		for(String monat : monate) {
			int umsatz = dbVerbindung.getUmsatz(monat, year, monat, year);
			monatZuUmsatz.put(monat,umsatz);
			series.getData().add(new XYChart.Data<String,Number>(monat,umsatz));
			sum += umsatz;
		}
	    umsatzChart.getData().add(series);
	    
	    for (Data<String, Number> entry : series.getData()) {                
            Tooltip t = new Tooltip(entry.getYValue().toString());
            Tooltip.install(entry.getNode(), t);
        }
	}
	
	public void searchTable() {
		ResultSet rs = null;
		data = FXCollections.observableArrayList();
		try {
		rs = dbVerbindung.search(sucheInTabelle.getText());
		for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            übersichtTabelle.getColumns().addAll(col);
        }
		
		while (rs.next()) {
            //Iterate Row
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(rs.getString(i));
            }
            data.add(row);
        }
		
		übersichtTabelle.setItems(data);
		
		} catch (Exception e) {
        Alert alert = new Alert(AlertType.ERROR, "Fehler beim erstellen der Tabelle \n" + e.toString());
		alert.show();
		}	
	}

	public void getSelectedCell() {
		
		ObservableList selectedCells = übersichtTabelle.getSelectionModel().getSelectedCells();

		selectedCells.addListener(new ListChangeListener() {
		    @Override
		    public void onChanged(Change c) {
		    	TablePosition tablePosition = (TablePosition) selectedCells.get(0);
		        Object val = tablePosition.getTableColumn().getCellData(tablePosition.getRow());
		        if(tablePosition.getTableColumn().getText().equals("RECHNUNGSNR")) {
		        	rechnungsnummer.setText((String) val);
		        	fillRechnungsFeld();
		        	tabPane.getSelectionModel().selectNext();
		        }
		    }    
		});
	}
	
	public void fertigstellen() {
    	WordDocument dokument = new WordDocument(new RechnungsInfo());
    	
    	progressBarFertig.setProgress(0.1);
    	
		dokument.getRechnungsDaten().setRechnungsNr(rechnungsnummer.getText());
		String datum = rechnungsdatum.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		dokument.getRechnungsDaten().setDatum(datum);
		dokument.getRechnungsDaten().setWoche(leistungszeitraum.getText());
		dokument.getRechnungsDaten().setBauvorhaben(bauvorhaben.getText());
		dokument.getRechnungsDaten().setBetrag(betrag.getText());
		dokument.getRechnungsDaten().setBeschreibung(beschreibung.getText());
		dokument.getRechnungsDaten().setKundennummer(kunde.getValue().getValue());
		progressBarFertig.setProgress(0.25);
		erstelleNeueRechnung(dokument); 
		
		try {
			rechnungsnummer.setText(dbVerbindung.retrieveNextRechnungsnummer());
		} catch (SQLException e2) {
			Alert alert = new Alert(AlertType.ERROR, "Fehler beim erfragen der nächsten Rechnungsnummer \n" + e2.toString());
			alert.show();
		}
   }
	
	public void erstelleNeueRechnung(WordDocument dokument) {
	    Convert convert = null;
	    try {
	    	
			dokument.generateDocxFileFromTemplate();
			progressBarFertig.setProgress(0.5);
			convert = new Convert(dokument);
			progressBarFertig.setProgress(0.85);
			//dbVerbindung.insertNeueRechnung(dokument,convert);
			progressBarFertig.setProgress(1.0);
			
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
		try {
			
			dbVerbindung.druckeRechnung(Integer.parseInt(rechnungsnummer.getText()));
			
		} catch (NumberFormatException e1) {
			Alert alert = new Alert(AlertType.ERROR, "Rechnungsnummer überprüfen");
			alert.show();
		} catch (IOException e1) {
			Alert alert = new Alert(AlertType.ERROR, "Fehler beim Laden der PDF Datei. Pfad überprüfen \n" + e1.toString());
			alert.show();
		} catch (PrinterException e1) {
			Alert alert = new Alert(AlertType.ERROR,"Fehler beim Drucken. Drucker oder so überprüfen \n" + e1.toString());
			alert.show();
		} catch (SQLException e1) {
			Alert alert = new Alert(AlertType.ERROR,"Fehler beim erfragen der Daten aus der Datenbank \n" + e1.toString());
			alert.show();
		}
	}
	
	public void öffneRechnung() {
		try {
			
			dbVerbindung.öffneRechnung(Integer.parseInt(rechnungsnummer.getText()));
			
		} catch (NumberFormatException e1) {
			Alert alert = new Alert(AlertType.ERROR, "Rechnungsnummer überprüfen");
			alert.show();
		} catch (SQLException e1) {
			Alert alert = new Alert(AlertType.ERROR,"Fehler beim erfragen des Pfades der PDF Datei \n" + e1.toString());
			alert.show();
		} catch (IOException e1) {
			Alert alert = new Alert(AlertType.ERROR, "Fehler beim öffnen. Datei nicht vorhanden \n" + e1.toString());
			alert.show();
		}
	}
	
	public void fillRechnungsFeld() {
		 WordDocument doc = null;
			try {
				doc = dbVerbindung.getRechnungsDaten(Integer.parseInt(rechnungsnummer.getText()));
			} catch (NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR,"Eingabe überprüfen \n" + e.toString());
				alert.show();
			} catch (SQLException e) {
				Alert alert = new Alert(AlertType.ERROR, "Fehler beim erfragen der Daten aus der Datenbank \n Datenbank überprüfen \n" + e.toString());
				alert.show();
			}
	    	  rechnungsnummer.setText(doc.getRechnungsDaten().getRechnungsNr());
	    	  LocalDate date = LocalDate.parse(doc.getRechnungsDaten().getDatum(),DateTimeFormatter.ofPattern("dd.MM.yyyy"));
	    	  rechnungsdatum.setValue(date);
	    	  leistungszeitraum.setText(doc.getRechnungsDaten().getWoche());
	    	  bauvorhaben.setText(doc.getRechnungsDaten().getBauvorhaben());
	    	  beschreibung.setText(doc.getRechnungsDaten().getBeschreibung());
	    	  betrag.setText(doc.getRechnungsDaten().getBetrag()); 
	}
	
	public void bearbeiteRechnung() {
    	
		String[] paths = null;
		try {
			paths = dbVerbindung.getFilePaths(Integer.parseInt(rechnungsnummer.getText()));
		} catch (NumberFormatException | SQLException e) {
			Alert alert = new Alert(AlertType.ERROR,"Fehler beim erhalten der Pfade zum bearbeiten. Rechnungsnummer überprüfen oder Datenbank \n" + e.toString());
			alert.show();
		}
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
			dbVerbindung.Rechnunglöschen(Integer.parseInt(rechnungsnummer.getText()));
			fertigstellen();
    	} catch (SQLException e) {
    		Alert alert = new Alert(AlertType.ERROR, "Fehler beim löschen der alten Rechnung \n Rechnung konnte nicht aus der Datenbank entfernt werden \n" + e.toString());
    		alert.show();
		}
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
			} catch (SQLException e) {
				Alert alert = new Alert(AlertType.ERROR, "Fehler beim erstellen der ComboBox. Datenbanküberprüfen \n" + e.toString());
				alert.show();
			}
			
	    	for(Entry<String,String> m : KundenNameNummer.entrySet()) {
	    		comboBox.getItems().add(m);
	    	}
	    }
	
	public void setMain(Main main) {
		this.main = main;
	}
	
}
