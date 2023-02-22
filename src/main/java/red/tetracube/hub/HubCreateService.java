package red.tetracube.hub;

import io.smallrye.mutiny.Uni;
import red.tetracube.data.entities.Hub;
import red.tetracube.data.repositories.HubRepository;
import red.tetracube.dto.DataValidationResult;
import red.tetracube.extensions.StringExtensions;
import red.tetracube.hublounge.hub.HubLoungeHubCreateRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class HubCreateService {

    @Inject
    HubRepository hubRepository;

    public Uni<DataValidationResult> validateCreateData() {
        return this.hubRepository.existsAnyHub()
                .map(exists ->
                        exists
                                ? DataValidationResult.failed(DataValidationResult.ErrorReason.CONFLICTS, "HUB_ALREADY_EXISTS")
                                : DataValidationResult.success()
                );
    }

    public Uni<Hub> createHub(HubLoungeHubCreateRequest request) {
        var slugHubName = StringExtensions.toSlug(request.getName());
        return Uni.createFrom().item(request)
                .map(insertRequest ->
                        new Hub(
                                insertRequest.getName(),
                                slugHubName
                        )
                )
                .flatMap(hub -> this.hubRepository.createHub(hub));
    }
}
