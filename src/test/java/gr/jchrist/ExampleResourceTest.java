package gr.jchrist;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(ExampleResourceTest.DbResource.class)
public class ExampleResourceTest {
    @Inject ObjectMapper mapper;

    @Test
    public void testEndpoint() throws Exception {
        final var m = new Model(0, "test text", null);
        var retJson = given().when().body(m)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .post("/api/test").then().statusCode(200).extract().body().asString();
        assertThat(retJson).contains("something_else");
        var ret = mapper.readValue(retJson, Model.class);
        assertTrue(ret.id > 0);
        assertEquals(ret.somethingElse, m.somethingElse);

        var get = given().when().get("/api/test").then()
                .statusCode(200).extract().body().as(Model.class);
        assertEquals(get.id, ret.id);
        assertEquals(get.somethingElse, m.somethingElse);
    }

    public static class DbResource implements QuarkusTestResourceLifecycleManager {
        private static PostgreSQLContainer DB;
        private static final Map<String, String> params = new HashMap<>();

        @Override
        public Map<String, String> start() {
            if (DB == null) {
                DB = new PostgreSQLContainer("postgres:alpine")
                        .withDatabaseName("test")
                        .withUsername("test")
                        .withPassword("test");
                DB.start();
                String url = DB.getJdbcUrl();
                params.put("quarkus.datasource.jdbc.url", url);
                params.put("quarkus.datasource.username", "test");
                params.put("quarkus.datasource.password", "test");
            }
            return params;
        }

        @Override
        public void stop() {
            if (DB != null) {
                DB.stop();
            }
        }
    }
}