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

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Health status information.
 */
public final class HealthStatus {

    /**
     * Convenient constant value representing unknown state.
     */
    public static final HealthStatus UNKNOWN = new HealthStatus(HealthStatusCode.UNKNOWN);

    /**
     * Convenient constant value representing up state.
     */
    public static final HealthStatus UP = new HealthStatus(HealthStatusCode.UP);

    /**
     * Convenient constant value representing down state.
     */
    public static final HealthStatus DOWN = new HealthStatus(HealthStatusCode.DOWN);

    /**
     * Convenient constant value representing out-of-service state.
     */
    public static final HealthStatus OUT_OF_SERVICE = new HealthStatus(HealthStatusCode.OUT_OF_SERVICE);

    /**
     * The status code of the server.
     */
    @JsonProperty("status")
    private final HealthStatusCode code;

    /**
     * Create a new instance of {@link HealthStatus}.
     *
     * @param code
     *         The status code of the server
     */
    public HealthStatus(final HealthStatusCode code) {
        this.code = code;
    }

    /**
     * Get the status code.
     *
     * @return the health status code
     */
    public HealthStatusCode getCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.code.toString();
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return code == ((HealthStatus) o).code;
    }

}