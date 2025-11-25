package validate;

/**
 * EmailValidator class provides email validation functionality
 * that can be used across multiple controllers in the application.
 */
public class EmailValidator {

    /**
     * Validates email address according to standard email format rules:
     * - Must have a local part (before @)
     * - Must have exactly one @ symbol
     * - Must have a domain name (after @)
     * - Domain must have at least one dot
     * - Must not start or end with special characters
     * 
     * @param email The email address to validate
     * @return ValidationResult object containing success status and error message
     */
    public static ValidationResult validateEmail(String email) {
        // Check if email is null or empty
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "Email cannot be empty.");
        }

        // Basic email regex pattern
        // Format: localpart@domain.extension
        String emailRegex = "^[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$";
        
        if (!email.matches(emailRegex)) {
            return new ValidationResult(false, "Please enter a valid email address (e.g., user@example.com).");
        }

        // Additional validation: check for consecutive dots
        if (email.contains("..")) {
            return new ValidationResult(false, "Email cannot contain consecutive dots.");
        }

        // Check that @ is not at the beginning or end
        if (email.startsWith("@") || email.endsWith("@")) {
            return new ValidationResult(false, "Email format is invalid.");
        }

        // Split email to validate parts
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return new ValidationResult(false, "Email must contain exactly one @ symbol.");
        }

        String localPart = parts[0];
        String domainPart = parts[1];

        // Validate local part (before @)
        if (localPart.length() < 1 || localPart.length() > 64) {
            return new ValidationResult(false, "Email username part is too long.");
        }

        // Validate domain part (after @)
        if (domainPart.length() < 3 || domainPart.length() > 255) {
            return new ValidationResult(false, "Email domain is invalid.");
        }

        // Check that domain has at least one dot
        if (!domainPart.contains(".")) {
            return new ValidationResult(false, "Email domain must contain a dot (e.g., example.com).");
        }

        // Check that domain doesn't start or end with dot or hyphen
        if (domainPart.startsWith(".") || domainPart.endsWith(".") || 
            domainPart.startsWith("-") || domainPart.endsWith("-")) {
            return new ValidationResult(false, "Email domain format is invalid.");
        }

        return new ValidationResult(true, "");
    }


    /**
     * Inner class to hold validation results
     */
    public static class ValidationResult {
        private boolean isValid;
        private String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}