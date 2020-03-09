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

public class BoundingBox {

    private double minX;

    private double minY;

    private double maxX;

    private double maxY;

    private String crs;

    private int dimensions;

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public double getMinX() {
        return minX;
    }

    public void setMinX(double minX) {
        this.minX = minX;
    }

    public double getMinY() {
        return minY;
    }

    public void setMinY(double minY) {
        this.minY = minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Min x: " + getMinX() + StringConstants.LINE_SEPARATOR);
        builder.append("Min y: " + getMinY() + StringConstants.LINE_SEPARATOR);
        builder.append("Max x : " + getMaxX() + StringConstants.LINE_SEPARATOR);
        builder.append("May y: " + getMaxY() + StringConstants.LINE_SEPARATOR);
        builder.append("Crs: " + getCrs() + StringConstants.LINE_SEPARATOR);
        builder.append("Dimensions: " + getDimensions() + StringConstants.LINE_SEPARATOR);

        return builder.toString();
    }
}
