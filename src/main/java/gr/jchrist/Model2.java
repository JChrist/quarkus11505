package gr.jchrist;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "test")
public class Model2 {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "something_else")
    @JsonProperty("something_else")
    private String somethingElse;

    @Column(name = "time")
    private Instant time;

    public Model2() {
        //for jpa
    }

    public Model2(long id, String somethingElse, Instant time) {
        this.id = id;
        this.somethingElse = somethingElse;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSomethingElse() {
        return somethingElse;
    }

    public void setSomethingElse(String somethingElse) {
        this.somethingElse = somethingElse;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"somethingElse\":" + (somethingElse == null ? null : "\"" + somethingElse + "\"") +
                ",\"time\":" + (time == null ? null : "\"" + time + "\"") +
                "}";
    }
}
