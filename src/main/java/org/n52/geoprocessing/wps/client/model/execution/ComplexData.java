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

import org.n52.geoprocessing.wps.client.model.StringConstants;

public class ComplexData extends Data {

    private boolean isReference;

    private ComplexDataReference reference;

    public boolean isReference() {
        return isReference;
    }

    public void setIsReference(boolean isReference) {
        this.isReference = isReference;
    }

    public ComplexDataReference getReference() {
        return reference;
    }

    public void setReference(ComplexDataReference reference) {
        this.reference = reference;
        setIsReference(true);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(super.toString());

        builder.append("Format: " + getFormat() + StringConstants.LINE_SEPARATOR);
        if (isReference) {
            builder.append("Reference: " + getReference() + StringConstants.LINE_SEPARATOR);
        } else {
            builder.append("Value: " + getValue() + StringConstants.LINE_SEPARATOR);
        }

        return builder.toString();
    }

}
