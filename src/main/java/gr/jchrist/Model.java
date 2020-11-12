package gr.jchrist;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "test")
public class Model extends PanacheEntityBase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @Column(name = "something_else") @JsonProperty("something_else")
    public String somethingElse;

    @Column(name = "time")
    public Instant time;

    public Model() {
        //for jpa
    }

    public Model(long id, String somethingElse, Instant time) {
        this.id = id;
        this.somethingElse = somethingElse;
        this.time = time;
    }
}
