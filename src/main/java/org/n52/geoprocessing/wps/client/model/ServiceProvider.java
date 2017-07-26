package org.n52.geoprocessing.wps.client.model;

public class ServiceProvider {

	private String providerName;
	private String providerSite;
	private ServiceContact serviceContact;
	
	public String getProviderName() {
		return providerName;
	}
	
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	
	public String getProviderSite() {
		return providerSite;
	}
	
	public void setProviderSite(String providerSite) {
		this.providerSite = providerSite;
	}
	
	public ServiceContact getServiceContact() {
		return serviceContact;
	}
	
	public void setServiceContact(ServiceContact serviceContact) {
		this.serviceContact = serviceContact;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("ProviderName: " + getProviderName() + "\n");
		stringBuilder.append("\t\t\tProviderSite: " + getProviderSite() + "\n");
		stringBuilder.append("\t\t\tServiceContact: " + getServiceContact() + "\n");
		
		return stringBuilder.toString();
	}
}
