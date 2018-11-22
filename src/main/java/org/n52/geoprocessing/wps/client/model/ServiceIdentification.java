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
package org.n52.geoprocessing.wps.client.model;

import java.util.Iterator;
import java.util.List;

public class ServiceIdentification {

    private static final String SERVICE_TYPE = "WPS";

    private String title;

    private String abstrakt;

    private List<String> keyWords;

    private List<String> serviceTypeVersions;

    private String fees;

    private List<String> accessConstraints;

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

    public List<String> getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(List<String> keyWords) {
        this.keyWords = keyWords;
    }

    public List<String> getServiceTypeVersions() {
        return serviceTypeVersions;
    }

    public void setServiceTypeVersions(List<String> serviceTypeVersions) {
        this.serviceTypeVersions = serviceTypeVersions;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public List<String> getAccessConstraints() {
        return accessConstraints;
    }

    public void setAccessConstraints(List<String> accessConstraintList) {
        this.accessConstraints = accessConstraintList;
    }

    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Title: " + getTitle() + StringConstants.LINE_SEPARATOR);
        stringBuilder.append("\t\t\tAbstract: " + getAbstract() + StringConstants.LINE_SEPARATOR);
        stringBuilder.append("\t\t\tServiceType versions: "
                + createCommaSeparatedStringFromList(getServiceTypeVersions()) + StringConstants.LINE_SEPARATOR);
        stringBuilder.append("\t\t\tKeywords: " + createCommaSeparatedStringFromList(getKeyWords())
                + StringConstants.LINE_SEPARATOR);
        stringBuilder.append("\t\t\tFees: " + getFees() + StringConstants.LINE_SEPARATOR);
        stringBuilder.append("\t\t\tAccess constraints: " + createCommaSeparatedStringFromList(getAccessConstraints())
                + StringConstants.LINE_SEPARATOR);

        return stringBuilder.toString();
    }

    private String createCommaSeparatedStringFromList(List<String> inputList) {
        String commaSeparatedString = "";

        Iterator<String> stringIterator = inputList.iterator();

        while (stringIterator.hasNext()) {
            String string = stringIterator.next();
            commaSeparatedString = commaSeparatedString.concat(string);
            if (stringIterator.hasNext()) {
                commaSeparatedString = commaSeparatedString.concat(", ");
            }
        }

        return commaSeparatedString;
    }
}
