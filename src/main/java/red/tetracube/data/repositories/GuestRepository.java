package red.tetracube.data.repositories;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;
import red.tetracube.data.entities.Guest;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GuestRepository {

    private final Mutiny.SessionFactory sessionFactory;

    public GuestRepository(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Uni<Boolean> existsByName(String name) {
        var sessionUni = sessionFactory.openSession();
        return sessionUni.chain(session ->
                session.createQuery("""
                        select count(*) = 1
                        from Guest guest
                        where name = :name
                        """,
                        Boolean.class
                )
                        .setParameter("name", name)
                        .setMaxResults(1)
                        .getSingleResult()
                        .eventually(session::close)
        );
    }

    public Uni<Guest> save(Guest guest) {
        var sessionUni = sessionFactory.openSession();
        return sessionUni.chain(session ->
                session.merge(guest)
                        .eventually(session::flush)
                        .eventually(session::close)
        );
    }
}
