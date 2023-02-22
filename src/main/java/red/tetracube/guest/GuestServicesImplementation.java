package red.tetracube.guest;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.hublounge.guest.GuestServices;
import red.tetracube.hublounge.guest.HubLoungeGuestEnrollmentReply;
import red.tetracube.hublounge.guest.HubLoungeGuestEnrollmentRequest;

@GrpcService
public class GuestServicesImplementation implements GuestServices {

    private final static Logger LOGGER = LoggerFactory.getLogger(GuestServicesImplementation.class);

    @Override
    public Uni<HubLoungeGuestEnrollmentReply> enrollment(HubLoungeGuestEnrollmentRequest request) {
        return null;
    }
}
