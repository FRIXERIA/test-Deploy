package sit.int221.sastt3.exception;
//import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementValidator {
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public static void validatePublishAndCloseDateTime(String publishDateTimeStr, String closeDateTimeStr) throws InvalidAnnouncementAttributeException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

        ZonedDateTime publishDateTime = null;
        ZonedDateTime closeDateTime = null;

        List<ValidationError> validationErrors = new ArrayList<>();

        try {
            publishDateTime = ZonedDateTime.parse(publishDateTimeStr, formatter);
        } catch (Exception e) {
            validationErrors.add(new ValidationError("publishDateTime", "Invalid publish date/time format: " + publishDateTimeStr));
        }

        try {
            closeDateTime = ZonedDateTime.parse(closeDateTimeStr, formatter);
        } catch (Exception e) {
            validationErrors.add(new ValidationError("closeDateTime", "Invalid close date/time format: " + closeDateTimeStr));
        }

        if (publishDateTime != null && closeDateTime != null && publishDateTime.isAfter(closeDateTime)) {
            validationErrors.add(new ValidationError("publishDateTime", "Publish date/time must be before close date/time."));
        }

        if (!validationErrors.isEmpty()) {
            throw new InvalidAnnouncementAttributeException("Announcement attributes validation failed!", "/api/announcements", validationErrors);
        }
    }

    public static class InvalidAnnouncementAttributeException extends Exception {
        private final String instance;
        private final List<ValidationError> errors;

        public InvalidAnnouncementAttributeException(String message, String instance, List<ValidationError> errors) {
            super(message);
            this.instance = instance;
            this.errors = errors;
        }

        public String getInstance() {
            return instance;
        }

        public List<ValidationError> getErrors() {
            return errors;
        }
    }

    public static class ValidationError {
        private final String field;
        private final String errorMessage;

        public ValidationError(String field, String errorMessage) {
            this.field = field;
            this.errorMessage = errorMessage;
        }

        public String getField() {
            return field;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
