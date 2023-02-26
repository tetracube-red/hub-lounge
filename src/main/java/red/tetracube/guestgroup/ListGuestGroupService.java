package red.tetracube.guestgroup;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.data.repositories.GuestGroupRepository;
import red.tetracube.hublounge.guestgroup.GetListGroupReply;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class ListGuestGroupService {

    @Inject
    GuestGroupRepository guestGroupRepository;

    private final static Logger LOGGER = LoggerFactory.getLogger(ListGuestGroupService.class);

    public Uni<List<GetListGroupReply>> getGuestsGroupsReply() {
        LOGGER.info("Getting list of groups");
        return this.guestGroupRepository.getAll()
                .invoke(items -> LOGGER.info("Found {} groups", items.size()))
                .onItem()
                .transformToMulti(groups -> Multi.createFrom().items(groups.stream()))
                .map(guestGroup ->
                        GetListGroupReply.newBuilder()
                                .setName(guestGroup.getName())
                                .setId(guestGroup.getId().toString())
                                .build()
                )
                .collect()
                .asList();
    }

}
