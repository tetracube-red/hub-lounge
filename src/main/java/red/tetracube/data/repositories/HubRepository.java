package red.tetracube.data.repositories;

import io.smallrye.mutiny.Uni;
import org.hibernate.reactive.mutiny.Mutiny;
import red.tetracube.data.entities.Hub;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HubRepository {

    private final Mutiny.SessionFactory sessionFactory;

    public HubRepository(Mutiny.SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Uni<Boolean> existsAnyHub() {
        var sessionUni = sessionFactory.openSession();
        return sessionUni.chain(session ->
                session.createQuery("""
                                        select count(hub.id) = 1
                                        from Hub hub
                                        """,
                                Boolean.class
                        )
                        .setMaxResults(1)
                        .getSingleResultOrNull()
                        .call(session::close)
        );
    }

    public Uni<Hub> createHub(Hub hub) {
        var sessionUni = sessionFactory.openSession();
        return sessionUni.chain(session ->
                session.merge(hub)
                        .call(session::flush)
                        .call(session::close)
        );
    }
}
