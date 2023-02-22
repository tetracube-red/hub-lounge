package red.tetracube.data.entities;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "guests")
public class Guest {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @JoinColumn(name = "guest_group_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = GuestGroup.class)
    private GuestGroup group;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public GuestGroup getGroup() {
        return group;
    }

    public void setGroup(GuestGroup group) {
        this.group = group;
    }
}
