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

public class OWSException {

    private String exceptionCode;

    private String locator;

    private String exceptionText;

    public OWSException(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public OWSException(String exceptionCode, String locator, String exceptionText) {
        this.exceptionCode = exceptionCode;
        this.locator = locator;
        this.exceptionText = exceptionText;
    }

    public OWSException() {}

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public String getExceptionText() {
        return exceptionText;
    }

    public void setExceptionText(String exceptionText) {
        this.exceptionText = exceptionText;
    }



    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("\tCode: " + getExceptionCode());
        builder.append("\tText: " + getExceptionText());
        builder.append("\tLocator: " + getLocator());

        return builder.toString();
    }
}
