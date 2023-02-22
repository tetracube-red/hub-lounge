package red.tetracube.data.entities;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "guests_groups")
public class GuestGroup {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToMany(targetEntity = Guest.class, fetch = FetchType.LAZY, mappedBy = "group")
    private List<Guest> guests;

    @ManyToMany(targetEntity = DomainAuthority.class, fetch = FetchType.LAZY, mappedBy = "guestsGroups")
    private List<DomainAuthority> domainsAuthorities;

    @JoinColumn(name = "hub_id", nullable = false)
    @ManyToOne(targetEntity = Hub.class, fetch = FetchType.LAZY)
    private Hub hub;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Guest> getGuests() {
        return guests;
    }

    public void setGuests(List<Guest> guests) {
        this.guests = guests;
    }

    public List<DomainAuthority> getDomainsAuthorities() {
        return domainsAuthorities;
    }

    public void setDomainsAuthorities(List<DomainAuthority> domainsAuthorities) {
        this.domainsAuthorities = domainsAuthorities;
    }

    public Hub getHub() {
        return hub;
    }

    public void setHub(Hub hub) {
        this.hub = hub;
    }
}
