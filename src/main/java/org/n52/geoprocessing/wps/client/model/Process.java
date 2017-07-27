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

import java.util.List;

public class Process {

    private String id;

    private String title;

    private String abstrakt;

    private boolean statusSupported;

    private boolean referenceSupported;

    private List<InputDescription> inputs;

    private List<OutputDescription> outputs;

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

    public List<InputDescription> getInputs() {
        return inputs;
    }

    public void setInputs(List<InputDescription> inputs) {
        this.inputs = inputs;
    }

    public List<OutputDescription> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<OutputDescription> outputs) {
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
