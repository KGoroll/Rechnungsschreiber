package Rechnungsschreiber;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;

public class Convert {
	  
	  //private  static final String pathToExe = "C:\\Users\\Krzysztof\\git\\Rechnungsschreiber\\Rechnungsschreiber\\resources\\OfficeToPDF.exe";
	  private  String pathToTemplate;
	  private  String pathToOutput;
	  private  WordDocument dokument;
	  
	  public Convert(WordDocument dokument) throws IOException {
		  this.dokument = dokument;
		  runConvert();
	  }	  
	  
	  
	 /* public void runConvert() throws IOException, InterruptedException {
		System.out.println("Started Converting");
		pathToTemplate = dokument.getDocxAbsoulteFilePath();
		pathToOutput = dokument.getFileDirectory() + "\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".pdf";		

		process = new ProcessBuilder(pathToExe, pathToTemplate, pathToOutput).start();
		process.waitFor();
	    
	    System.out.println("Result of Office processing: " + process.exitValue());
	  } */
	  
	  public void runConvert() throws IOException{
		  pathToTemplate = dokument.getDocxAbsoulteFilePath();
		  pathToOutput = dokument.getFileDirectory() + "\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".pdf";
		  
		  	File inputWord = new File(pathToTemplate);
	        File outputFile = new File(pathToOutput);
	            InputStream docxInputStream = new FileInputStream(inputWord);
	            OutputStream outputStream = new FileOutputStream(outputFile);
	            IConverter converter = LocalConverter.builder().build();
	            converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
	            outputStream.close();
	            System.out.println("success");
	  }  
	  
	  public String getPathToPdf() {
		  return pathToOutput;
	  }
	  
	  public String getPathToWord() {
		  return pathToTemplate;
	  }

}