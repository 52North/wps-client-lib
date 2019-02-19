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
package org.n52.geoprocessing.wps.client;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractClientGETRequest {

    protected static final String SERVICE_REQ_PARAM_NAME = "service";

    protected static final String REQUEST_REQ_PARAM_NAME = "request";

    protected static final String SERVICE_REQ_PARAM_VALUE = "WPS";

    protected static final String VERSION_REQ_PARAM_NAME = "version";

    protected static final String ACCEPT_VERSIONS_REQ_PARAM_NAME = "acceptVersions";

    protected static String VERSION_REQ_100_PARAM_VALUE = "1.0.0";

    protected static String VERSION_REQ_20_PARAM_VALUE = "2.0.0";

    protected Map<String, String> requestParams;

    public AbstractClientGETRequest() {
        requestParams = new HashMap<String, String>();
        requestParams.put(SERVICE_REQ_PARAM_NAME, SERVICE_REQ_PARAM_VALUE);
    }

    protected void setRequestParamValue(String s) {
        requestParams.put(REQUEST_REQ_PARAM_NAME, s);
    }

    /**
     * adds to the url the designated parameter names and values, as configured
     * before.
     *
     * @param url
     *            base URL
     * @return GetCapabilities URL
     */
    public String getRequest(String url) {

        StringBuffer urlCopy = new StringBuffer(url);

        if (!url.contains("?")) {
            urlCopy = urlCopy.append("?");
        }

        Iterator<Entry<String, String>> parameterIterator = requestParams.entrySet().iterator();

        while (parameterIterator.hasNext()) {
            Map.Entry<String, String> entry = parameterIterator.next();
            urlCopy.append(entry.getKey() + "=" + entry.getValue());
            if (parameterIterator.hasNext()) {
                urlCopy.append("&");
            }
        }

        return urlCopy.toString();
    }

    public abstract boolean valid();
}
