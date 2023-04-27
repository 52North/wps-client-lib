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

import java.util.ArrayList;
import java.util.List;

public class AllowedValues {

    private List<String> allowedValues;

    private List<Range> ranges;

    public AllowedValues() {
        this.allowedValues = new ArrayList<String>();
        this.ranges = new ArrayList<Range>();
    }

    public AllowedValues(int size) {
        allowedValues = new ArrayList<>(size);
    }

    public List<String> getAllowedValues() {
        return allowedValues;
    }

    public void setAllowedValues(List<String> allowedValues) {
        this.allowedValues.addAll(allowedValues);
    }

    public void addAllowedValue(String allowedValue) {
        this.allowedValues.add(allowedValue);
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }

    public void addRange(Range range) {
        this.ranges.add(range);
    }

}
