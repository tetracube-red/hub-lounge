package red.tetracube.extensions;

import io.vertx.pgclient.PgException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import red.tetracube.dto.DataValidationResult;
import red.tetracube.hublounge.errors.HubLoungeProcessErrorReply;
import red.tetracube.hublounge.errors.ServiceErrorReason;

import java.net.ConnectException;

public class ExceptionExtensions {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionExtensions.class);

    public static HubLoungeProcessErrorReply grpcErrorFromValidationResult(DataValidationResult result) {
        LOGGER.warn("Building exception for {}", result.getMessage());
        switch (result.getErrorReason()) {
            case CONFLICTS -> {
                return HubLoungeProcessErrorReply.newBuilder()
                        .setReason(ServiceErrorReason.CONFLICTS)
                        .setDescription(result.getMessage())
                        .build();
            }
            case NOT_FOUND -> {
                return HubLoungeProcessErrorReply.newBuilder()
                        .setReason(ServiceErrorReason.NOT_FOUND)
                        .setDescription(result.getMessage())
                        .build();
            }
            case UNAUTHORIZED -> {
                return HubLoungeProcessErrorReply.newBuilder()
                        .setReason(ServiceErrorReason.UNAUTHORIZED)
                        .setDescription(result.getMessage())
                        .build();
            }
        }
        return HubLoungeProcessErrorReply.newBuilder()
                .setReason(ServiceErrorReason.INTERNAL_ERROR)
                .build();
    }

    public static HubLoungeProcessErrorReply grpErrorFromException(Throwable exception) {
        if (exception instanceof PgException || exception instanceof ConnectException) {
            return HubLoungeProcessErrorReply.newBuilder()
                    .setReason(ServiceErrorReason.INTERNAL_ERROR)
                    .setDescription("DATABASE_CONNECTION_ERROR")
                    .build();
        }
        return HubLoungeProcessErrorReply.newBuilder()
                .setReason(ServiceErrorReason.INTERNAL_ERROR)
                .setDescription(exception.getMessage())
                .build();
    }
}
