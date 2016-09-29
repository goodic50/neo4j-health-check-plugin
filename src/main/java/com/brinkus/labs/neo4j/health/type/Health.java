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

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Health information with the status code and details.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class Health {

    /**
     * Builder for creating immutable {@link Health} instances.
     */
    public static class Builder {

        private HealthStatus status;

        private Map<String, Object> details;

        /**
         * Create new Builder instance.
         */
        public Builder() {
            this.status = HealthStatus.UNKNOWN;
            this.details = new LinkedHashMap<>();
        }

        /**
         * Record detail for given {@link Exception}.
         *
         * @param ex
         *         the exception
         *
         * @return this {@link Builder} instance
         */
        public Builder withException(Exception ex) {
            return withDetail("error", ex.getClass().getName() + ": " + ex.getMessage());
        }

        /**
         * Record detail using {@code key} and {@code value}.
         *
         * @param key
         *         the detail key
         * @param data
         *         the detail data
         *
         * @return this {@link Builder} instance
         */
        public Builder withDetail(String key, Object data) {
            this.details.put(key, data);
            return this;
        }

        /**
         * Set status to {@link HealthStatus#UNKNOWN} status.
         *
         * @return this {@link Builder} instance
         */
        public Builder unknown() {
            return status(HealthStatus.UNKNOWN);
        }

        /**
         * Set status to {@link HealthStatus#UP} status.
         *
         * @return this {@link Builder} instance
         */
        public Builder up() {
            return status(HealthStatus.UP);
        }

        /**
         * Set status to {@link HealthStatus#DOWN}.
         *
         * @return this {@link Builder} instance
         */
        public Builder down() {
            return status(HealthStatus.DOWN);
        }

        /**
         * Set status to {@link HealthStatus#OUT_OF_SERVICE}.
         *
         * @return this {@link Builder} instance
         */
        public Builder outOfService() {
            return status(HealthStatus.OUT_OF_SERVICE);
        }

        /**
         * Set status to given {@code statusCode}.
         *
         * @param statusCode
         *         the status code
         *
         * @return this {@link Builder} instance
         */
        public Builder status(HealthStatusCode statusCode) {
            return status(new HealthStatus(statusCode));
        }

        /**
         * Set status to given {@link HealthStatus} instance.
         *
         * @param status
         *         the status
         *
         * @return this {@link Builder} instance
         */
        public Builder status(HealthStatus status) {
            this.status = status;
            return this;
        }

        /**
         * Create a new {@link Health} instance with the previously specified code and
         * details.
         *
         * @return a new {@link Health} instance
         */
        public Health build() {
            return new Health(this);
        }
    }

    private final HealthStatus status;

    private final Map<String, Object> details;

    /**
     * Create a new {@link Health} instance with the specified status and details.
     *
     * @param builder
     *         the Builder to use
     */
    private Health(Builder builder) {
        this.status = builder.status;
        this.details = Collections.unmodifiableMap(builder.details);
    }

    /**
     * Return the status of the health.
     *
     * @return the status (never {@code null})
     */
    @JsonUnwrapped
    public HealthStatus getStatus() {
        return this.status;
    }

    /**
     * Return the details of the health.
     *
     * @return the details (or an empty map)
     */
    @JsonAnyGetter
    public Map<String, Object> getDetails() {
        return this.details;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj instanceof Health) {
            Health other = (Health) obj;
            return this.status.equals(other.status) && this.details.equals(other.details);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = this.status.hashCode();
        return 19 * hashCode + this.details.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s %s", getStatus(), getDetails());
    }

}
