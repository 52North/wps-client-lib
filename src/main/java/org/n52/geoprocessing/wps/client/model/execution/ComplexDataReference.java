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

import java.net.URL;

import org.apache.xmlbeans.XmlObject;
import org.n52.geoprocessing.wps.client.model.StringConstants;

public class ComplexDataReference {

    private URL href;

    private URL bodyReference;

    private XmlObject body;

    public URL getHref() {
        return href;
    }

    public void setHref(URL href) {
        this.href = href;
    }

    public URL getBodyReference() {
        return bodyReference;
    }

    public void setBodyReference(URL bodyReference) {
        this.bodyReference = bodyReference;
    }

    public XmlObject getBody() {
        return body;
    }

    public void setBody(XmlObject body) {
        this.body = body;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Href: " + getHref() + StringConstants.LINE_SEPARATOR);

        return builder.toString();
    }

}
