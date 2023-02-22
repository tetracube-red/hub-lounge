package red.tetracube.data.entities;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table( name = "hubs")
public class Hub {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "hub", targetEntity = GuestGroup.class)
    private List<GuestGroup> guestsGroups;

    public Hub() {
    }

    public Hub(String name, String slug) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.slug = slug;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<GuestGroup> getGuestsGroups() {
        return guestsGroups;
    }

    public void setGuestsGroups(List<GuestGroup> guestsGroups) {
        this.guestsGroups = guestsGroups;
    }
}
