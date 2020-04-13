package Rechnungsschreiber;
import java.awt.EventQueue;

import org.apache.log4j.BasicConfigurator;

public class Main {


	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		RechnungsInfo daten = new RechnungsInfo();
		WordDocument dokument = new WordDocument(daten);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI(dokument);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 
		
	}
}