package red.tetracube.guest;

import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import red.tetracube.data.entities.Guest;
import red.tetracube.data.repositories.GuestGroupRepository;
import red.tetracube.data.repositories.GuestRepository;
import red.tetracube.dto.DataValidationResult;
import red.tetracube.hublounge.guest.HubLoungeGuestEnrollmentRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class GuestEnrollmentService {

    @Inject
    GuestRepository guestRepository;

    @Inject
    GuestGroupRepository guestGroupRepository;

    @Inject
    BCryptPasswordEncoder passwordEncoder;

    private final static Logger LOGGER = LoggerFactory.getLogger(GuestEnrollmentService.class);

    public Uni<DataValidationResult> validateEnrollment(HubLoungeGuestEnrollmentRequest request) {
        LOGGER.info("Checking if there is another guest with the same name");
        var guestExistsUni = guestRepository.existsByName(request.getName());
        LOGGER.info("Checking if guest group exists");
        var guestGroupExistsUni = guestGroupRepository.existsByName(request.getGuestGroupName());
        return Uni.combine().all().unis(guestExistsUni, guestGroupExistsUni)
                .asTuple()
                .map(queryResults -> {
                    var guestExists = queryResults.getItem1();
                    var guestGroupExists = queryResults.getItem2();
                    if (guestExists) {
                        LOGGER.warn("Guest {} already exists", request.getName());
                        return DataValidationResult.failed(DataValidationResult.ErrorReason.CONFLICTS, "GUEST_ALREADY_EXISTS");
                    }
                    if (!guestGroupExists) {
                        LOGGER.warn("Guest group {} does not exist", request.getGuestGroupName());
                        return DataValidationResult.failed(DataValidationResult.ErrorReason.NOT_FOUND, "ACCOUNT_GROUP_NOT_FOUND");
                    }
                    LOGGER.info("Enrollment data is ok");
                    return DataValidationResult.success();
                });
    }

    public Uni<Void> saveGuest(HubLoungeGuestEnrollmentRequest request) {
        LOGGER.info("Encoding password");
        var encodedPassword = passwordEncoder.encode(request.getPassword());
        LOGGER.debug("Building guest entity");
        var guestEntity = new Guest();
        guestEntity.setId(UUID.randomUUID());
        guestEntity.setName(request.getName());
        guestEntity.setPassword(encodedPassword);
        return guestGroupRepository.getByName(request.getGuestGroupName())
                .invoke(guestEntity::setGroup)
                .call(ignored -> guestRepository.save(guestEntity))
                .replaceWithVoid();
    }
}
