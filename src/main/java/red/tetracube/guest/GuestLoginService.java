package red.tetracube.guest;

import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import red.tetracube.data.entities.Guest;
import red.tetracube.data.entities.GuestGroup;
import red.tetracube.data.repositories.GuestGroupRepository;
import red.tetracube.data.repositories.GuestRepository;
import red.tetracube.dto.DataValidationResult;
import red.tetracube.hublounge.guest.HubLoungeGuestLoginRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class GuestLoginService {

    @Inject
    GuestRepository guestRepository;

    @Inject
    GuestGroupRepository guestGroupRepository;

    @Inject
    BCryptPasswordEncoder passwordEncoder;

    @ConfigProperty(name = "smallrye.jwt.new-token.issuer")
    String issuer;

    @ConfigProperty(name = "smallrye.jwt.new-token.audience")
    String audience;

    private final Logger LOGGER = LoggerFactory.getLogger(GuestLoginService.class);

    public Uni<DataValidationResult> validateLoginRequest(HubLoungeGuestLoginRequest request) {
        LOGGER.info("Checking if guest exists");
        return guestRepository.getByName(request.getName())
                .map(optionalGuest ->
                        optionalGuest
                                .map(guest -> {
                                    LOGGER.info("Guest exists, checking password");
                                    return passwordEncoder.matches(request.getPassword(), guest.getPassword())
                                            ? DataValidationResult.success()
                                            : DataValidationResult.failed(DataValidationResult.ErrorReason.UNAUTHORIZED, "INVALID_CREDENTIALS");
                                })
                                .orElseGet(() -> {
                                    LOGGER.info("Guest does not exists");
                                    return DataValidationResult.failed(DataValidationResult.ErrorReason.UNAUTHORIZED, "INVALID_CREDENTIALS");
                                })
                );
    }

    public Uni<String> doGuestLogin(HubLoungeGuestLoginRequest request) {
        return guestRepository.getByName(request.getName())
                .flatMap(optionalGuest -> {
                    LOGGER.info("Assuming guest exists because checked before");
                    if (optionalGuest.isEmpty()) {
                        return null;
                    }
                    var guest = optionalGuest.get();
                    LOGGER.info("Getting account group and authorizations");
                    var guestGroupUni = guestGroupRepository.getById(guest.getGroup().getId());
                    return guestGroupUni.map(guestGroup -> buildAccessToken(guest, guestGroup));
                });
    }

    private String buildAccessToken(Guest guest, GuestGroup guestGroup) {
        LOGGER.info("Building JWT");
        var createdAt = Calendar.getInstance();
        var expiresAt = Calendar.getInstance();
        expiresAt.add(Calendar.YEAR, 1);
        var subject = guest.getName();
        var groups = Stream.concat(
                        guestGroup.getDomainsAuthorities().stream()
                                .map(domainAuthority -> domainAuthority.getContext().name()),
                        guestGroup.getDomainsAuthorities().stream()
                                .map(domainAuthority -> domainAuthority.getContextAction().name())
                )
                .collect(Collectors.toSet());

        return Jwt.issuer(issuer)
                .subject(subject)
                .audience(audience)
                .issuedAt(createdAt.getTimeInMillis())
                .expiresAt(expiresAt.getTimeInMillis())
                .groups(groups)
                .preferredUserName(subject)
                .sign();
    }
}
