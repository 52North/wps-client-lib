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
package org.n52.geoprocessing.wps.client.model.execution;

import org.n52.geoprocessing.wps.client.model.StringConstants;

public class Data extends WPSExecuteParameter {

    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ComplexData asComplexData() {

        ComplexData complexData = new ComplexData();

        copyMembers(complexData);

        return complexData;
    }

    public ComplexData asComplexReferenceData() {

        ComplexData complexData = new ComplexData();

        copyMembersForReference(complexData);

        return complexData;
    }

    public LiteralData asLiteralData() {

        LiteralData literalData = new LiteralData();

        copyMembers(literalData);

        return literalData;
    }

    public BoundingBoxData asBoundingBoxData() {

        BoundingBoxData boundingBoxData = new BoundingBoxData();

        copyMembers(boundingBoxData);

        return boundingBoxData;
    }

    private void copyMembers(Data data) {

        data.setId(getId());
        data.setAbstract(getAbstract());
        data.setFormat(getFormat());
        data.setMetadata(getMetadata());
        data.setTitle(getTitle());
        data.setValue(getValue());
    }

    private void copyMembersForReference(ComplexData data) {

        data.setId(getId());
        data.setAbstract(getAbstract());
        data.setFormat(getFormat());
        data.setMetadata(getMetadata());
        data.setTitle(getTitle());
        data.setReference(((ComplexData)this).getReference());
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Id: " + getId() + StringConstants.LINE_SEPARATOR);
        builder.append("Title: " + getTitle() + StringConstants.LINE_SEPARATOR);
        builder.append("Abstract: " + getAbstract() + StringConstants.LINE_SEPARATOR);
        builder.append("Metadata: " + getMetadata() + StringConstants.LINE_SEPARATOR);
        builder.append("Format: " + getFormat() + StringConstants.LINE_SEPARATOR);

        return builder.toString();
    }

}
