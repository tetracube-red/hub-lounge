package red.tetracube.guest;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.extensions.ExceptionExtensions;
import red.tetracube.hublounge.guest.GuestServices;
import red.tetracube.hublounge.guest.HubLoungeGuestEnrollmentReply;
import red.tetracube.hublounge.guest.HubLoungeGuestEnrollmentRequest;
import red.tetracube.hublounge.hub.HubLoungeHubCreateReply;

import javax.inject.Inject;

@GrpcService
public class GuestServicesImplementation implements GuestServices {

    @Inject
    GuestEnrollmentService guestEnrollmentService;

    private final static Logger LOGGER = LoggerFactory.getLogger(GuestServicesImplementation.class);

    @Override
    public Uni<HubLoungeGuestEnrollmentReply> enrollment(HubLoungeGuestEnrollmentRequest request) {
        return this.guestEnrollmentService.validateEnrollment(request)
                .flatMap(dataValidationResult -> {
                    if (!dataValidationResult.getValid()) {
                        var replyError = ExceptionExtensions.grpcErrorFromValidationResult(dataValidationResult);
                        var invalidResponse = HubLoungeGuestEnrollmentReply.newBuilder()
                                .setProcessingError(replyError)
                                .build();
                        return Uni.createFrom().item(invalidResponse);
                    }
                    return this.guestEnrollmentService.saveGuest(request)
                            .map(hubEntity ->
                                    HubLoungeGuestEnrollmentReply.newBuilder()
                                            .build()
                            );
                })
                .onFailure()
                .recoverWithItem(throwable -> {
                    LOGGER.warn(throwable.getMessage());
                    var replyError = ExceptionExtensions.grpErrorFromException(throwable);
                    return HubLoungeGuestEnrollmentReply.newBuilder()
                            .setProcessingError(replyError)
                            .build();
                });
    }
}
