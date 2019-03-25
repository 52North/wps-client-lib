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

public class Range {

    private Object minimumValue;

    private Object maximumValue;

    private Object spacing;

    private Closure closure;

    public Object getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(Object minimumValue) {
        this.minimumValue = minimumValue;
    }

    public Object getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(Object maximumValue) {
        this.maximumValue = maximumValue;
    }

    public Object getSpacing() {
        return spacing;
    }

    public void setSpacing(Object spacing) {
        this.spacing = spacing;
    }

    public Closure getClosure() {
        return closure;
    }

    public void setClosure(String closure) {
        this.closure = Closure.forName(closure);
    }

    public static enum Closure {
        CLOSED {
            @Override
            public String getName() {
                return "closed";
            }

        },
        OPEN {
            @Override
            public String getName() {
                return "open";
            }

        },
        OPEN_CLOSED {
            @Override
            public String getName() {
                return "open-closed";
            }

        },
        CLOSED_OPEN {
            @Override
            public String getName() {
                return "closed-open";
            }

        };

        public static Closure forName(String name) {
            for (Closure c : Closure.values()) {
                if (c.getName().equals(name)) {
                    return c;
                }
            }

            throw new IllegalStateException("No value available for name: " + name);
        }

        public String getName() {
            return "";
        }
    }

}
