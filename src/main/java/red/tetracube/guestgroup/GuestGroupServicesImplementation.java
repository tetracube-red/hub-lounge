package red.tetracube.guestgroup;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import red.tetracube.hublounge.guestgroup.GetListGroupsReply;
import red.tetracube.hublounge.guestgroup.GetListGroupsRequest;
import red.tetracube.hublounge.guestgroup.GuestGroupServices;

import javax.inject.Inject;

@GrpcService
public class GuestGroupServicesImplementation implements GuestGroupServices {

    @Inject
    ListGuestGroupService listGuestGroupService;

    @Override
    public Uni<GetListGroupsReply> listGroups(GetListGroupsRequest request) {
        return listGuestGroupService.getGuestsGroupsReply()
                .map(guests ->
                        GetListGroupsReply.newBuilder()
                                .addAllGroup(guests)
                                .build()
                );
    }
}
