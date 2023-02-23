package red.tetracube.data.repositories;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;
import red.tetracube.data.entities.GuestGroup;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class GuestGroupRepository {

    private final Mutiny.SessionFactory sessionFactory;

    public GuestGroupRepository(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Uni<Boolean> existsByName(String name) {
        var sessionUni = sessionFactory.openSession();
        return sessionUni.chain(session ->
                session.createQuery("""
                                            select count(*) = 1
                                            from GuestGroup guestGroup
                                            where guestGroup.name = :name
                                        """,
                                Boolean.class
                        )
                        .setParameter("name", name)
                        .setMaxResults(1)
                        .getSingleResult()
                        .eventually(session::close)
        );
    }

    public Uni<GuestGroup> getByName(String name) {
        var sessionUni = sessionFactory.openSession();
        return sessionUni.chain(session ->
                session.createQuery("""
                                        from GuestGroup guestGroup
                                        where guestGroup.name = :name
                                        """,
                                GuestGroup.class
                        )
                        .setParameter("name", name)
                        .setMaxResults(1)
                        .getSingleResult()
                        .eventually(session::close)
        );
    }

    public Uni<GuestGroup> getById(UUID id) {
        var sessionUni = sessionFactory.openSession();
        return sessionUni.chain(session ->
                session.createQuery("""
                                        select guestGroup
                                        from GuestGroup guestGroup
                                        join fetch guestGroup.domainsAuthorities domainsAuthorities
                                        where guestGroup.id = :id
                                        """,
                                GuestGroup.class
                        )
                        .setParameter("id", id)
                        .getResultList()
                        .map(results -> results.isEmpty() ? null : results.get(0))
                        .eventually(session::close)
        );
    }
}
