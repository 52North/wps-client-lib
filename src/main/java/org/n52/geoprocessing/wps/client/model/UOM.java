/*
 * ﻿Copyright (C) 2020 52°North Initiative for Geospatial Open Source
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

import java.util.Objects;

public class UOM extends DefaultableElement {

    private String uomString;

    public UOM(String uomString) {
        this.uomString = uomString;
    }

    public String getUomString() {
        return uomString;
    }

    public void setUomString(String uomString) {
        this.uomString = uomString;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof UOM) {

            UOM other = (UOM) obj;

            return uomString.equals(other.uomString);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }
}
