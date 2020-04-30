package Rechnungsschreiber;
import java.io.IOException;

public class Convert{
	  
	  private  static final String pathToExe = "C:\\Users\\Krzysztof\\git\\Rechnungsschreiber\\Rechnungsschreiber\\resources\\OfficeToPDF.exe";
	  private  String pathToTemplate;
	  private  String pathToOutput;
	  private  WordDocument dokument;
	  private  Process process;
	  
	  public Convert(WordDocument dokument) throws IOException, InterruptedException {
		  this.dokument = dokument;
		  runConvert();
	  }	  
	  
	  public void runConvert() throws IOException, InterruptedException {
		System.out.println("Started Converting");
		pathToTemplate = dokument.getDocxAbsoulteFilePath();
		pathToOutput = dokument.getFileDirectory() + "\\Rechnung " + dokument.getRechnungsDaten().getRechnungsNr() + ".pdf";		

		process = new ProcessBuilder(pathToExe, pathToTemplate, pathToOutput).start();
		process.waitFor();
	    
	    System.out.println("Result of Office processing: " + process.exitValue());
	  }
	  
	  public String getPathToPdf() {
		  return pathToOutput;
	  }
	  
	  public String getPathToWord() {
		  return pathToTemplate;
	  }
}