package gr.jchrist;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.List;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleResource {
    private static final Logger logger = LoggerFactory.getLogger(ExampleResource.class);

    @Inject EntityManager em;

    @GET
    @Transactional
    public List<Model> getAll() {
        return Model.listAll();
    }

    @POST
    @Transactional
    public Model create(Model m) {
        m.time = Instant.now();
        logger.info("persisting: {}", m);
        m.persist();
        logger.info("persisted and returning: {}", m);
        return m;
    }

    @GET
    @Path("/2")
    @Transactional
    public List<Model2> getAll2() {
        return em.unwrap(Session.class).createNativeQuery("SELECT * FROM test", Model2.class).getResultList();
    }

    @POST
    @Path("/2")
    @Transactional
    public Model2 create(Model2 m) {
        m.setTime(Instant.now());
        logger.info("persisting: {}", m);
        m = em.merge(m);
        logger.info("persisted and returning: {}", m);
        return m;
    }
}