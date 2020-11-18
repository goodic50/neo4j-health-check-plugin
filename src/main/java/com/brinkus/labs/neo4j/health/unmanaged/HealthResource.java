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
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.ResultTransformer;

import java.util.Collections;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Un-managed REST endpoint to execute query and set health status of the instance.
 */
@Path("/health")
public class HealthResource {

    private static final long TIMEOUT = 1000L;

    private static final String PING_CYPHER = "MATCH (n) RETURN count(*) as count";

    private final ObjectMapper mapper;

    private final DatabaseManagementService dbms;

    public HealthResource(@Context DatabaseManagementService dbms) {
        assert(dbms != null);
        this.dbms = dbms;
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
    @Path("/{dbName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health(@PathParam("dbName") final String dbName) throws JsonProcessingException {
        Health health = healthCheck(dbms.database(dbName));
        byte[] entity = mapper.writeValueAsBytes(health);
        return Response.ok()
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(entity)
                .build();
    }

    private Health healthCheck(GraphDatabaseService service) {
        try {
            assert(service != null);

            if (!service.isAvailable(TIMEOUT)) {
                return new Health.Builder().down()
                        .withDetail("description", "Neo4j health check result was invalid!")
                        .build();
            }

            Long count = service.executeTransactionally(PING_CYPHER, Collections.emptyMap(), new CountResultTransformer());
            assert(count != null);

            if (count == 0) {
                return new Health.Builder().outOfService()
                        .withDetail("description", "Neo4j has no available nodes!")
                        .build();
            }

            return new Health.Builder().up()
                    .withDetail("description", "Neo4j health check was successful.")
                    .build();
        } catch (Exception e) {
            return new Health.Builder().outOfService()
                    .withDetail("description", "Neo4j health check failed!")
                    .withException(e)
                    .build();
        }
    }

    private static class CountResultTransformer implements ResultTransformer<Long> {
        @Override
        public Long apply(Result result) {
            return (Long) result.next().get("count");
        }
    }
}
