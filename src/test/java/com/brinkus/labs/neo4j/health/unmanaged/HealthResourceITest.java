package com.brinkus.labs.neo4j.health.unmanaged;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.Neo4j;
import org.neo4j.harness.Neo4jBuilders;
import org.neo4j.test.rule.SuppressOutput;
import org.neo4j.test.server.HTTP;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HealthResourceITest {

    @Rule
    public SuppressOutput suppressOutput = SuppressOutput.suppressAll();

    @Test
    public void healthUp() throws Exception {
        try (Neo4j server = Neo4jBuilders.newInProcessBuilder()
                .withUnmanagedExtension("/labs", HealthResource.class)
                .withFixture(graphDatabaseService ->
                {
                  graphDatabaseService.executeTransactionally("CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})");
                  return null;
                })
                .build()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("labs/health/neo4j").toString());

            assertThat(response.status(), is(200));
            assertThat(response.stringFromContent("status"), is("UP"));
            assertThat(response.stringFromContent("description"), is("Neo4j health check was successful."));
        }
    }

    @Test
    public void outOfService() throws Exception {
        try (Neo4j server = Neo4jBuilders.newInProcessBuilder()
                .withUnmanagedExtension("/labs", HealthResource.class)
                .build()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("labs/health/neo4j").toString());

            assertThat(response.status(), is(200));
            assertThat(response.stringFromContent("status"), is("OUT_OF_SERVICE"));
            assertThat(response.stringFromContent("description"), is("Neo4j has no available nodes!"));
        }
    }

    @Test
    public void resourceMissing() throws Exception {
        try (Neo4j server = Neo4jBuilders.newInProcessBuilder().build()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("labs/health/neo4j").toString());

            assertThat(response.status(), is(404));
        }
    }

}
