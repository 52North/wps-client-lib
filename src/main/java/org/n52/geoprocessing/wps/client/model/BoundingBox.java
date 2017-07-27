/*
 * ﻿Copyright (C) ${inceptionYear} - ${currentYear} 52°North Initiative for Geospatial Open Source
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

public class BoundingBox {

    private double[] lowerCorner;

    private double[] upperCorner;

    private int dimensions;

    private String crs;

    public double[] getLowerCorner() {
        return lowerCorner;
    }

    public void setLowerCorner(double[] lowerCorner) {
        this.lowerCorner = lowerCorner;
    }

    public double[] getUpperCorner() {
        return upperCorner;
    }

    public void setUpperCorner(double[] upperCorner) {
        this.upperCorner = upperCorner;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public String getCrs() {
        return crs;
    }

    public void setCrs(String crs) {
        this.crs = crs;
    }

}
