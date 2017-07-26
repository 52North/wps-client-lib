package org.n52.geoprocessing.wps.client.model;

public class ServiceContact {

	private String individualName;
	private String positionName;
	private ContactInfo contactInfo;
	
	public String getIndividualName() {
		return individualName;
	}
	
	public void setIndividualName(String individualName) {
		this.individualName = individualName;
	}
	
	public String getPositionName() {
		return positionName;
	}
	
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	
	public ContactInfo getContactInfo() {
		return contactInfo;
	}
	
	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("IndividualName: " + getIndividualName() + "\n");
		stringBuilder.append("\t\t\t\t\tPositionName: " + getPositionName() + "\n");
		stringBuilder.append("\t\t\t\t\tContactInfo:\t" + getContactInfo() + "\n");
		
		return stringBuilder.toString();
	}
}
