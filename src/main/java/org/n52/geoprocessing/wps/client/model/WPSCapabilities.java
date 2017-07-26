package org.n52.geoprocessing.wps.client.model;

import java.util.List;

public class WPSCapabilities {

	private ServiceIdentification serviceIdentification;
	private ServiceProvider serviceProvider;
	private List<Process> processes;
	
	public ServiceIdentification getServiceIdentification() {
		return serviceIdentification;
	}
	
	public void setServiceIdentification(ServiceIdentification serviceIdentification) {
		this.serviceIdentification = serviceIdentification;
	}
	
	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}
	
	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	
	public List<Process> getProcesses() {
		return processes;
	}
	
	public void setProcesses(List<Process> processes) {
		this.processes = processes;
	}
	
	public Process getProcess(String id){
		for (Process process : processes) {
			if(process.getId().equals(id)){
				return process;
			}
		}		
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("ServiceIdentification:\t" + getServiceIdentification() + "\n");
		stringBuilder.append("ServiceProvider:\t" + getServiceProvider() + "\n");
		stringBuilder.append("Processes:\t" + getProcesses() + "\n");
		
		return stringBuilder.toString();
	}
	
}
