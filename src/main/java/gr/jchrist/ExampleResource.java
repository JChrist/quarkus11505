package gr.jchrist;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.List;

@Path("/test")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExampleResource {

    @GET
    @Transactional
    public List<Model> getAll() {
        return Model.listAll();
    }

    @POST
    @Transactional
    public Model create(Model m) {
        m.time = Instant.now();
        m.persist();
        return m;
    }
}