/*
 * ﻿Copyright (C) ${inceptionYear} - ${currentYear} 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
