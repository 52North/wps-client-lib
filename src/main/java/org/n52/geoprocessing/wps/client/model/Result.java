/*
 * ﻿Copyright (C) 2019 52°North Initiative for Geospatial Open Source
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

import java.net.URL;
import java.util.List;

import org.n52.geoprocessing.wps.client.model.execution.Data;

public class Result {

    private URL url;

    private List<Data> outputs;

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public List<Data> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<Data> data) {
        this.outputs = data;
    }

}
