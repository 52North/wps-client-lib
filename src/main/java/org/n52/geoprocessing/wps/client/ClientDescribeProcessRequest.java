/*
 * ﻿Copyright (C) 2018 52°North Initiative for Geospatial Open Source
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

public class ClientDescribeProcessRequest extends AbstractClientGETRequest {

    private static String IDENTIFIER_REQ_PARAM_NAME = "identifier";

    private static String REQUEST_REQ_PARAM_VALUE = "DescribeProcess";

    ClientDescribeProcessRequest(String version) {
        super();
        setRequestParamValue(REQUEST_REQ_PARAM_VALUE);
        requestParams.put(VERSION_REQ_PARAM_NAME, version);
    }

    public void setIdentifier(String[] ids) {
        StringBuffer idsString = new StringBuffer();
        for (int i = 0; i < ids.length; i++) {
            idsString.append(ids[i]);
            if (i != ids.length - 1) {
                idsString.append(",");
            }
        }
        requestParams.put(IDENTIFIER_REQ_PARAM_NAME, idsString.toString());
    }

    public boolean valid() {
        return true;
    }

}
