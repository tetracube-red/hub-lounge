package red.tetracube.dto;

public class DataValidationResult {

    private final Boolean isValid;
    private final ErrorReason errorReason;
    private final String message;

    private DataValidationResult(Boolean isValid, ErrorReason errorReason, String message) {
        this.isValid = isValid;
        this.errorReason = errorReason;
        this.message = message;
    }

    public static DataValidationResult success() {
        return new DataValidationResult(true, null, null);
    }

    public static DataValidationResult failed(ErrorReason errorReason, String message) {
        return new DataValidationResult(
                false,
                errorReason,
                message
        );
    }

    public Boolean getValid() {
        return isValid;
    }

    public ErrorReason getErrorReason() {
        return errorReason;
    }

    public String getMessage() {
        return message;
    }

    public enum ErrorReason {
        CONFLICTS
    }
}
