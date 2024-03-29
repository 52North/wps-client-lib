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

public class LiteralInputDescription extends InputDescription {

    private Object defaultValue;

    private AllowedValues allowedValues;

    private boolean anyValue;

    private String dataType;

    private List<UOM> uoms;

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public AllowedValues getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(AllowedValues allowedValues) {
        this.allowedValues = allowedValues;
    }

    public boolean isAnyValue() {
        return anyValue;
    }

    public void setAnyValue(boolean anyValue) {
        this.anyValue = anyValue;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public List<UOM> getUoms() {
        return uoms;
    }

    public void setUoms(List<UOM> uoms) {
        this.uoms = uoms;
    }

}
