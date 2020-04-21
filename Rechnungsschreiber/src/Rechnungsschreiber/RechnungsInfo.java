package Rechnungsschreiber;
import java.util.Scanner;

public class RechnungsInfo{
	
	private String kundennummer;
    private String rechnungsNr;
    private String datum;
    private String woche;
    private String beschreibung;
    private String bauvorhaben;
    private String betrag;
    private String mwst;
    private String brutto;
    private boolean isFirma;
    
    public void setInfo() {
    	Scanner in = new Scanner (System.in);
    	
    	System.out.println("Rechnungsdatum: ");
    	datum = in.nextLine();
    	
    	System.out.println("Leistungszeitraum KW: ");
    	woche = in.nextLine();
    	
    	System.out.println("Rechnungsnummer: ");
    	rechnungsNr = in.nextLine();
    	
    	System.out.println("Bauvorhaben: ");
    	bauvorhaben = in.nextLine();

    	System.out.println("Beschreibung: ");
    	beschreibung = in.nextLine();
    	
    	System.out.println("Betrag: ");
    	betrag = in.nextLine();
    	
    	in.close();
    }
    
    public String getRechnungsNr() {
		return rechnungsNr;
	}

	public void setRechnungsNr(String rechnungsNr) {
		this.rechnungsNr = rechnungsNr;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public String getWoche() {
		return woche;
	}

	public void setWoche(String woche) {
		this.woche = woche;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBauvorhaben() {
		return bauvorhaben;
	}

	public void setBauvorhaben(String bauvorhaben) {
		this.bauvorhaben = bauvorhaben;
	}

	public String getBetrag() {
		return betrag;
	}

	public void setBetrag(String betrag) {
		this.betrag = betrag;
		betrag = betrag.replace(',', '.');
		mwst = String.format("%.2f", Float.parseFloat(betrag)*0.19);
		brutto = String.format("%.2f", Float.parseFloat(betrag)*0.19 + Float.parseFloat(betrag));
	}
	
	public String getKundennummer() {
		return kundennummer;
	}

	public void setKundennummer(String kundennummer) {
		this.kundennummer = kundennummer;
	}

	public String getMwst() {
		return mwst;
	}

	public void setMwst(String mwst) {
		this.mwst = mwst;
	}

	public String getBrutto() {
		return brutto;
	}

	public void setBrutto(String brutto) {
		this.brutto = brutto;
	}

	public void setRechnungsartIsFirma(boolean isFirma) {
		this.isFirma = isFirma;
	}

	public boolean getRechnungsartIsFirma() {
		return isFirma;
	}

	@Override
    public String toString() {
        return  "Rechnung Nr: " + rechnungsNr + "\n" +
                "Rechnungsdatum: " + datum + "\n" +
                "Leistungszeitraum: " + woche + "\n" +
                "Bauvorhaben: " + bauvorhaben + "\n" +
                "Beschreibung: " + beschreibung + "\n" +
                "Betrag: " + betrag;
    }
}
