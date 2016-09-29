package com.brinkus.labs.neo4j.health.unmanaged;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.test.SuppressOutput;
import org.neo4j.test.server.HTTP;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HealthResourceIT {

    @Rule
    public SuppressOutput suppressOutput = SuppressOutput.suppressAll();

    @Test
    public void healthUp() throws Exception {
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension("/labs", HealthResource.class)
                .withFixture("CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})")
                .newServer()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("labs/health").toString());

            assertThat(response.status(), is(200));
            assertThat(response.stringFromContent("status"), is("UP"));
            assertThat(response.stringFromContent("description"), is("Neo4j health check was success."));
        }
    }

    @Test
    public void outOfService() throws Exception {
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension("/labs", HealthResource.class)
                .newServer()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("labs/health").toString());

            assertThat(response.status(), is(200));
            assertThat(response.stringFromContent("status"), is("OUT_OF_SERVICE"));
            assertThat(response.stringFromContent("description"), is("Neo4j has no available node!"));
        }
    }

    @Test
    public void resourceMissing() throws Exception {
        try (ServerControls server = TestServerBuilders.newInProcessBuilder().newServer()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("labs/health").toString());

            assertThat(response.status(), is(404));
        }
    }

}
