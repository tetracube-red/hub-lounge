package red.tetracube.data.repositories;

import org.hibernate.reactive.mutiny.Mutiny;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuestRepository {

    private final Mutiny.SessionFactory sessionFactory;

    public GuestRepository(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
