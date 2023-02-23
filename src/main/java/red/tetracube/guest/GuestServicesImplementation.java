package red.tetracube.guest;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.extensions.ExceptionExtensions;
import red.tetracube.hublounge.guest.*;

import javax.inject.Inject;

@GrpcService
public class GuestServicesImplementation implements GuestServices {

    @Inject
    GuestEnrollmentService guestEnrollmentService;

    @Inject
    GuestLoginService guestLoginService;

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

    @Override
    public Uni<HubLoungeGuestLoginReply> login(HubLoungeGuestLoginRequest request) {
        return this.guestLoginService.validateLoginRequest(request)
                .flatMap(dataValidationResult -> {
                    if (!dataValidationResult.getValid()) {
                        LOGGER.warn("Invalid request, building invalid response");
                        var replyError = ExceptionExtensions.grpcErrorFromValidationResult(dataValidationResult);
                        var invalidResponse = HubLoungeGuestLoginReply.newBuilder()
                                .setProcessingError(replyError)
                                .build();
                        return Uni.createFrom().item(invalidResponse);
                    }
                    return guestLoginService.doGuestLogin(request)
                            .map(accessToken -> {
                                LOGGER.info("Building access token response");
                                return HubLoungeGuestLoginReply.newBuilder()
                                        .setAccessToken(accessToken)
                                        .build();
                            });
                });
    }
}
