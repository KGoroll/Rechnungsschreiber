package Rechnungsschreiber;

public class TemplateInfo {
	private String kundennummer;
	private Boolean isFirma;
	private String kundenname;
	private String kundenStraße;
	private String kundenPlz;
	private String kundenOrt;
	
	TemplateInfo(){
	}

	public String getKundennummer() {
		return kundennummer;
	}

	public void setKundennummer(String kundennummer) {
		this.kundennummer = kundennummer;
	}

	public Boolean getIsFirma() {
		return isFirma;
	}

	public void setIsFirma(Boolean isFirma) {
		this.isFirma = isFirma;
	}

	public String getKundenname() {
		return kundenname;
	}

	public void setKundenname(String kundenname) {
		this.kundenname = kundenname;
	}

	public String getKundenStraße() {
		return kundenStraße;
	}

	public void setKundenStraße(String kundenStraße) {
		this.kundenStraße = kundenStraße;
	}

	public String getKundenPlz() {
		return kundenPlz;
	}

	public void setKundenPlz(String kundenPlz) {
		this.kundenPlz = kundenPlz;
	}

	public String getKundenOrt() {
		return kundenOrt;
	}

	public void setKundenOrt(String kundenOrt) {
		this.kundenOrt = kundenOrt;
	}
	
	
}
