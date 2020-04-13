package Rechnungsschreiber;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;

public class WordDocument {

	private static String TEMPLATE = "Rechnung_vorlage.docx";
	private static final String TEMPLATE_FIRMA = "Rechnung_Firma_vorlage.docx";
	private static final String TEMPLATE_PRIVAT = "Rechnung_Privat_vorlage.docx";
	private String absoluteFilePath;
	private String fileDirectory;
	private RechnungsInfo daten;
	
	WordDocument(RechnungsInfo daten){
		this.daten = daten;
	}
	
    public void generateDocxFileFromTemplate() throws Exception {
    	
    	if (daten.getRechnungsartIsFirma() == true) {
    		TEMPLATE = TEMPLATE_FIRMA;
    	}
    	else {
    		TEMPLATE = TEMPLATE_PRIVAT;
    	}
    	
        InputStream templateInputStream = this.getClass().getClassLoader().getResourceAsStream(TEMPLATE);

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(templateInputStream);

        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        VariablePrepare.prepare(wordMLPackage);

        HashMap<String, String> variables = new HashMap<>();
        variables.put("rechnungsNr", daten.getRechnungsNr());
        variables.put("datum", daten.getDatum());
        variables.put("woche", daten.getWoche());
        variables.put("beschreibung", daten.getBeschreibung());
        variables.put("bauvorhaben", daten.getBauvorhaben());
        variables.put("betrag", daten.getBetrag());
        variables.put("mwst", daten.getMwst());
        variables.put("brutto", daten.getBrutto());
        
        documentPart.variableReplace(variables);
        
        File finalFile = new File("D:\\Benutzer\\Desktop\\Rechnungen\\Rechnung " + daten.getRechnungsNr() + ".docx");
        Docx4J.save(wordMLPackage, finalFile,0);
        
        absoluteFilePath = finalFile.getAbsolutePath();
        fileDirectory = finalFile.getParent();
        
        System.out.println("finished creating Word document");
    }
    
    public String getDocxAbsoulteFilePath () {
    	return absoluteFilePath;
    }
    
    public String getFileDirectory () {
    	return fileDirectory;
    }
    
    public RechnungsInfo getRechnungsDaten() {
    	return daten;
    }
}