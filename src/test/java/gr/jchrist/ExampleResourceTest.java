package gr.jchrist;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
@QuarkusTestResource(ExampleResourceTest.DbResource.class)
public class ExampleResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(ExampleResourceTest.class);
    @Inject ObjectMapper mapper;
    @Inject EntityManager em;

    @BeforeEach
    public void beforeEach() throws Exception {
        truncate();
    }

    @Test
    public void testEndpoint() throws Exception {
        final var m = new Model(0, "test text", null);
        //final var mjs = mapper.writeValueAsString(m);
        logger.info("sending: {} json:{}", m, mapper.writeValueAsString(m));
        var retJson = given().when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .body(m)
                .post("/api/test").then().extract().body().asString();
        logger.info("json: {}", retJson);
        assertThat(retJson).contains("something_else");
        var ret = mapper.readValue(retJson, Model.class);
        logger.info("parsed: {}", ret);
        assertTrue(ret.id > 0);
        assertEquals(ret.somethingElse, m.somethingElse);

        var getJson = given().when().get("/api/test").then()
                .statusCode(200).extract().body().asString();
        var gets = mapper.readValue(getJson, new TypeReference<List<Model>>() {});
        assertThat(gets).hasSize(1);
        assertEquals(gets.get(0).id, ret.id);
        assertEquals(gets.get(0).somethingElse, m.somethingElse);
    }

    @Test
    public void testEndpoint2() throws Exception {
        final var m = new Model2(0, "test text", null);
        logger.info("sending: {} json:{}", m, mapper.writeValueAsString(m));
        var retJson = given().when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .body(m)
                .post("/api/test/2").then().extract().body().asString();
        logger.info("json: {}", retJson);
        assertThat(retJson).contains("something_else");
        var ret = mapper.readValue(retJson, Model2.class);
        logger.info("parsed: {}", ret);
        assertTrue(ret.getId() > 0);
        assertEquals(ret.getSomethingElse(), m.getSomethingElse());

        var getJson = given().when().get("/api/test/2").then()
                .statusCode(200).extract().body().asString();
        var gets = mapper.readValue(getJson, new TypeReference<List<Model2>>() {});
        assertThat(gets).hasSize(1);
        assertEquals(gets.get(0).getId(), ret.getId());
        assertEquals(gets.get(0).getSomethingElse(), m.getSomethingElse());
    }

    @Transactional
    public void truncate() {
        em.createNativeQuery("TRUNCATE test").executeUpdate();
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