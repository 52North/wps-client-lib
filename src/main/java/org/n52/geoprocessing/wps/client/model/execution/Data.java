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
package org.n52.geoprocessing.wps.client.model.execution;

public class Data extends WPSExecuteParameter{

    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public ComplexData asComplexData(){

        ComplexData complexData = new ComplexData();

        copyMembers(complexData);

        return complexData;
    }

    public LiteralData asLiteralData(){

        LiteralData literalData = new LiteralData();

        copyMembers(literalData);

        return literalData;
    }

    public BoundingBoxData asBoundingBoxData(){

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

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Id: " + getId() + "\n");
        builder.append("Title: " + getTitle() + "\n");
        builder.append("Abstract: " + getAbstract() + "\n");
        builder.append("Metadata: " + getMetadata() + "\n");
        builder.append("Format: " + getFormat() + "\n");

        return builder.toString();
    }

}
