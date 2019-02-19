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
import java.util.ArrayList;
import java.util.List;

public class Output {

    private String id;

    private String title;

    private String abstrakt;

    private String value;

    private URL reference;

    private boolean isReference;

    private Format format;

    private DataType type;

    private List<String> metadata = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String string) {
        this.value = string;
    }

    public URL getReference() {
        return reference;
    }

    public void setReference(URL reference) {
        this.reference = reference;
    }

    public boolean isReference() {
        return isReference;
    }

    public void setIsReference(boolean isReference) {
        this.isReference = isReference;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
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

    public List<String> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<String> metadataList) {
        this.metadata = metadataList;
    }

    public void addMetadata(String metadata) {
        this.metadata.add(metadata);
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }
}
