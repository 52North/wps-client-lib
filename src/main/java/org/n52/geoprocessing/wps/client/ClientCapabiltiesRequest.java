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
package org.n52.geoprocessing.wps.client;

public class ClientCapabiltiesRequest extends AbstractClientGETRequest {

    private String REQUEST_REQ_PARAM_VALUE = "GetCapabilities";

    public ClientCapabiltiesRequest(String version) {
        super();
        setRequestParamValue(REQUEST_REQ_PARAM_VALUE);
        if (version != null && !version.isEmpty()) {
            requestParams.put(ACCEPT_VERSIONS_REQ_PARAM_NAME, version);
        }
    }

    public boolean valid() {
        return true;
    }

}
