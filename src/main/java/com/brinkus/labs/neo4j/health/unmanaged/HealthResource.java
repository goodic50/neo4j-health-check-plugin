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

package com.brinkus.labs.neo4j.health.unmanaged;

import com.brinkus.labs.neo4j.health.type.Health;
import com.brinkus.labs.neo4j.health.type.HealthStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Un-managed REST endpoint to execute query and set health status of the instance.
 */
@Path("/health")
public class HealthResource {

    private static final int TIMEOUT = 1000;

    private static final String PING_CYPHER = "MATCH (n) RETURN count(*) as count";

    private final GraphDatabaseService service;

    private final ObjectMapper mapper;

    public HealthResource(@Context GraphDatabaseService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
    }

    /**
     * REST endpoint to get the health status.
     *
     * @return the REST endpoint's response containing the {@link HealthStatus} entity.
     *
     * @throws JsonProcessingException
     *         if an error occurred during the entity serialization.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response health() throws JsonProcessingException {
        Health health = healthCheck();
        byte[] entity = mapper.writeValueAsBytes(health);
        return Response.ok()
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(entity)
                .build();
    }

    private Health healthCheck() {
        try {
            if (!service.isAvailable(TIMEOUT)) {
                return new Health.Builder().down()
                        .withDetail("description", "Neo4j health check result was invalid!")
                        .build();
            }

            Result result = service.execute(PING_CYPHER);
            Long count = (Long) result.next().get("count");

            if (count == 0) {
                return new Health.Builder().outOfService()
                        .withDetail("description", "Neo4j has no available node!")
                        .build();
            }

            return new Health.Builder().up()
                    .withDetail("description", "Neo4j health check was success.")
                    .build();
        } catch (Exception e) {
            return new Health.Builder().outOfService()
                    .withDetail("description", "Neo4j health check failed!")
                    .withException(e)
                    .build();

        }
    }

}
