package red.tetracube.hub;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.extensions.ExceptionExtensions;
import red.tetracube.hublounge.hub.HubLoungeHubCreateReply;
import red.tetracube.hublounge.hub.HubLoungeHubCreateRequest;
import red.tetracube.hublounge.hub.HubServices;

import javax.inject.Inject;

@GrpcService
public class HubServicesImplementation implements HubServices {

    @Inject
    HubCreateService hubCreateService;

    private final static Logger LOGGER = LoggerFactory.getLogger(HubServicesImplementation.class);

    @Override
    public Uni<HubLoungeHubCreateReply> create(HubLoungeHubCreateRequest request) {
        return this.hubCreateService.validateCreateData()
                .flatMap(dataValidationResult -> {
                    if (!dataValidationResult.getValid()) {
                        var replyError = ExceptionExtensions.grpcErrorFromValidationResult(dataValidationResult);
                        var invalidResponse = HubLoungeHubCreateReply.newBuilder()
                                .setProcessingError(replyError)
                                .build();
                        return Uni.createFrom().item(invalidResponse);
                    }
                    return this.hubCreateService.createHub(request)
                            .map(hubEntity ->
                                    HubLoungeHubCreateReply.newBuilder()
                                            .setId(hubEntity.getId().toString())
                                            .setName(hubEntity.getName())
                                            .setSlug(hubEntity.getSlug())
                                            .build()
                            );
                })
                .onFailure()
                .recoverWithItem(throwable -> {
                    LOGGER.warn(throwable.getMessage());
                    var replyError = ExceptionExtensions.grpErrorFromException(throwable);
                    return HubLoungeHubCreateReply.newBuilder()
                            .setProcessingError(replyError)
                            .build();
                });
    }
}
