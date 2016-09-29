/*
 * Health Check Plugin for Neo4j
 * Copyright (C) 2016  Balazs Brinkus
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.brinkus.labs.neo4j.health.type;

/**
 * The health status code.
 */
public enum HealthStatusCode {

    /**
     * The server is up and running.
     */
    UP("UP"),

    /**
     * The server is down.
     */
    DOWN("DOWN"),

    /**
     * The server is out of service.
     */
    OUT_OF_SERVICE("OUT_OF_SERVICE"),

    /**
     * The server is in an unknown state.
     */
    UNKNOWN("UNKNOWN");

    /**
     * The value of the enum.
     */
    private String value;

    /**
     * Create a new instance of {@link HealthStatusCode}
     *
     * @param value
     *         the text value of the enum
     */
    HealthStatusCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
