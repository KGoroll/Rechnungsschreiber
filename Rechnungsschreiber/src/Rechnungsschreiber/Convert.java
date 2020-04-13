package Rechnungsschreiber;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Convert{
	  
	  private  String pathToExe = "D:\\Program Files\\eclipse\\eclipse-workspace\\Rechnungsschreiber\\resources\\OfficeToPDF.exe";
	  private  String pathToTemplate;
	  private  String pathToOutput;
	  private  WordDocument dokument;
	  private  Process process;
	  private  byte[] fileContent;
	  
	  public Convert(WordDocument dokument) {
		  this.dokument = dokument;
		  runConvert();
	  }	  
	  
	  public void runConvert() {
		System.out.println("Started Converting");
		pathToTemplate = dokument.getFileDirectory() + "\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".docx";
		pathToOutput = dokument.getFileDirectory() + "\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".pdf";		
		
	    try {
			process = new ProcessBuilder(pathToExe, pathToTemplate, pathToOutput).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    try {
			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    System.out.println("Result of Office processing: " + process.exitValue());

	    File file = new File(pathToOutput);
		try {
			fileContent = Files.readAllBytes(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    System.out.println(fileContent.length);
	  }
	  
	  public String getPathToPdf() {
		  return pathToOutput;
	  }
	  
	  public String getPathToWord() {
		  return pathToTemplate;
	  }
}