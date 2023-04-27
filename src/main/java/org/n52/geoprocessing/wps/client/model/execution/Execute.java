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
package org.n52.geoprocessing.wps.client.model.execution;

import java.util.ArrayList;
import java.util.List;

import org.n52.geoprocessing.wps.client.model.ResponseMode;

public class Execute {

    private List<Data> inputs;

    private List<ExecuteOutput> outputs;

    private ResponseMode responseMode;

    private ExecutionMode executionMode;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Data> getInputs() {
        return inputs;
    }

    public void setInputs(List<Data> inputs) {
        this.inputs = inputs;
    }

    public List<ExecuteOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ExecuteOutput> outputs) {
        this.outputs = outputs;
    }

    public ResponseMode getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(ResponseMode responseMode) {
        this.responseMode = responseMode;
    }

    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    public boolean addOutput(ExecuteOutput output) {

        if (getOutputs() == null) {
            setOutputs(new ArrayList<ExecuteOutput>());
        }
        return getOutputs().add(output);
    }

    public boolean addInput(Data input) {
        if (getInputs() == null) {
            setInputs(new ArrayList<Data>());
        }
        return getInputs().add(input);

    }

}
