package org.n52.geoprocessing.wps.client.model;
import java.util.Iterator;
import java.util.List;

public class ServiceIdentification {

	private String title;
	private String abstrakt;
	private List<String> keyWords;
	private final String serviceType = "WPS";
	private List<String> serviceTypeVersions;
	private String fees;
	private List<String> accessConstraints;
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAbstract() {
		return abstrakt;
	}
	
	public void setAbstract(String abstrakt) {
		this.abstrakt = abstrakt;
	}
	
	public List<String> getKeyWords() {
		return keyWords;
	}
	
	public void setKeyWords(List<String> keyWords) {
		this.keyWords = keyWords;
	}
	
	public List<String> getServiceTypeVersions() {
		return serviceTypeVersions;
	}
	
	public void setServiceTypeVersions(List<String> serviceTypeVersions) {
		this.serviceTypeVersions = serviceTypeVersions;
	}
	
	public String getFees() {
		return fees;
	}
	
	public void setFees(String fees) {
		this.fees = fees;
	}
	public List<String> getAccessConstraints() {
		return accessConstraints;
	}
	
	public void setAccessConstraints(List<String> accessConstraintList) {
		this.accessConstraints = accessConstraintList;
	}
	
	public String getServiceType() {
		return serviceType;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("Title: " + getTitle() + "\n");
		stringBuilder.append("\t\t\tAbstract: " + getAbstract() + "\n");
		stringBuilder.append("\t\t\tServiceType versions: " + createCommaSeparatedStringFromList(getServiceTypeVersions()) + "\n");
		stringBuilder.append("\t\t\tKeywords: " + createCommaSeparatedStringFromList(getKeyWords()) + "\n");
		stringBuilder.append("\t\t\tFees: " + getFees() + "\n");
		stringBuilder.append("\t\t\tAccess constraints: " + createCommaSeparatedStringFromList(getAccessConstraints()) + "\n");
		
		return stringBuilder.toString();
	}
	
	private String createCommaSeparatedStringFromList(List<String> inputList){
		String commaSeparatedString = "";
		
		Iterator<String> stringIterator = inputList.iterator();
		
		while (stringIterator.hasNext()) {
			String string = (String) stringIterator.next();
			commaSeparatedString = commaSeparatedString.concat(string);
			if(stringIterator.hasNext()){
				commaSeparatedString = commaSeparatedString.concat(", ");
			}			
		}
		
		return commaSeparatedString;
	}
}
