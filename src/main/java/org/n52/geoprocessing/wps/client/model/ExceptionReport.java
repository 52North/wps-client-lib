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

import java.util.ArrayList;
import java.util.List;

public class ExceptionReport extends WPSResponse {

    private List<OWSExceptionElement> exceptions;

    public ExceptionReport() {
        this.exceptions = new ArrayList<>();
    }

    public ExceptionReport(List<OWSExceptionElement> exceptions) {
        this.exceptions = exceptions;
    }

    public void setExceptions(List<OWSExceptionElement> exceptions) {
        this.exceptions = exceptions;
    }

    public List<OWSExceptionElement> getExceptions() {
        return exceptions;
    }

    public boolean addException(OWSExceptionElement exception) {
        return exceptions.add(exception);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("Exceptions: " + getExceptions());

        return builder.toString();
    }

}
