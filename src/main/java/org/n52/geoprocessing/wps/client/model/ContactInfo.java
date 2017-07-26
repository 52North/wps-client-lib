package org.n52.geoprocessing.wps.client.model;

public class ContactInfo {

	private Phone phone;
	private Address address;
	
	public Phone getPhone() {
		return phone;
	}
	
	public void setPhone(Phone phone) {
		this.phone = phone;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("Phone: " + getPhone() + "\n");
		stringBuilder.append("\t\t\t\t\t\t\tAddress: " + getAddress() + "\n");
		
		return stringBuilder.toString();
	}
	
}
