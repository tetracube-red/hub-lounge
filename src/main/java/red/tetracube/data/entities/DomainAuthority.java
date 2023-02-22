package red.tetracube.data.entities;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "domains_authorities")
public class DomainAuthority {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToMany(targetEntity = GuestGroup.class, fetch = FetchType.LAZY)
    private List<GuestGroup> guestsGroups;
}
