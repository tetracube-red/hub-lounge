package red.tetracube.data.entities;

import red.tetracube.data.enumerations.AuthorizationContext;
import red.tetracube.data.enumerations.AuthorizationContextAction;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "domains_authorities")
public class DomainAuthority {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "context", nullable = false)
    private AuthorizationContext context;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AuthorizationContextAction contextAction;

    @ManyToMany(targetEntity = GuestGroup.class, fetch = FetchType.LAZY)
    private List<GuestGroup> guestsGroups;
}
