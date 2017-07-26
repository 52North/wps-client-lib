package org.n52.geoprocessing.wps.client.model;

import java.util.List;

public class Process {

	private String id;
	private String title;
	private String abstrakt;
	private boolean statusSupported;
	private boolean referenceSupported;
	private List<Input> inputs;
	private List<Output> outputs;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
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
	
	public boolean isStatusSupported() {
		return statusSupported;
	}
	
	public void setStatusSupported(boolean statusSupported) {
		this.statusSupported = statusSupported;
	}
	
	public boolean isReferenceSupported() {
		return referenceSupported;
	}
	
	public void setReferenceSupported(boolean referenceSupported) {
		this.referenceSupported = referenceSupported;
	}
	
	public List<Input> getInputs() {
		return inputs;
	}
	
	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}
	
	public List<Output> getOutputs() {
		return outputs;
	}
	
	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
	}	
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("Id: " + getId() + "\n");
		stringBuilder.append("Title: " + getTitle() + "\n");
		stringBuilder.append("\t\t\tAbstract: " + getAbstract() + "\n");
		
		return stringBuilder.toString();
	}
	
}
