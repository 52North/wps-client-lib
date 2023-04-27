/*
 * ﻿Copyright (C) 2023 52°North Initiative for Geospatial Open Source
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

import java.util.List;

public class WPSCapabilities extends WPSResponse {

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

    public Process getProcess(String id) {
        for (Process process : processes) {
            if (process.getId().equals(id)) {
                return process;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("ServiceIdentification:\t" + getServiceIdentification() + StringConstants.LINE_SEPARATOR);
        stringBuilder.append("ServiceProvider:\t" + getServiceProvider() + StringConstants.LINE_SEPARATOR);
        stringBuilder.append("Processes:\t" + getProcesses() + StringConstants.LINE_SEPARATOR);

        return stringBuilder.toString();
    }

}
