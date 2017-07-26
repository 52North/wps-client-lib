package org.n52.geoprocessing.wps.client.model;

public class Phone {

	private String voice;
	private String facsimile;
	
	public String getVoice() {
		return voice;
	}
	
	public void setVoice(String voice) {
		this.voice = voice;
	}
	
	public String getFacsimile() {
		return facsimile;
	}
	
	public void setFacsimile(String facsimile) {
		this.facsimile = facsimile;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("\tVoice: " + getVoice() + "\n");
		stringBuilder.append("\t\t\t\t\t\t\t\tFacsimile: " + getFacsimile().trim() + "\n");
		
		return stringBuilder.toString();
	}
}
