package src.main.java.si.fri.rso.middoc.models.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "items")
@NamedQueries(value =
        {
                @NamedQuery(name = "ItemEntity.getAll", query = "SELECT it FROM ItemsEntity it")
        })
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "format")
    private String format;

    @Column(name = "created")
    private Instant created;

    @Column(name = "uri")
    private String uri;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}