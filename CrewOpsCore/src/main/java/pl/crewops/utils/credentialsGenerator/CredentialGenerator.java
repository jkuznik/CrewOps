package pl.crewops.utils.credentialsGenerator;

import java.security.SecureRandom;
import java.util.List;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class CredentialGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private CredentialGenerator() {}

    public static String generateUsername(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            throw new IllegalArgumentException("First name and last name must not be null");
        }

        String fn = sanitize(firstName);
        String ln = sanitize(lastName);

        String firstPart = fn.length() >= 3 ? fn.substring(0, 3) : padRight(fn, 3);
        String lastPart = ln.length() >= 3 ? ln.substring(0, 3) : padRight(ln, 3);
        int randomSuffix = 1000 + RANDOM.nextInt(9000);

        return (firstPart + lastPart + randomSuffix).toLowerCase();
    }

    private static String sanitize(String input) {
        return input.trim().replaceAll("[^a-zA-Z]", "").toLowerCase();
    }

    private static String padRight(String s, int n) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) {
            sb.append("x");
        }
        return sb.toString();
    }

    public static String generatePassword() {
        PasswordGenerator generator = new PasswordGenerator();

        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
        lowerCaseRule.setNumberOfCharacters(2);

        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
        upperCaseRule.setNumberOfCharacters(2);

        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit);
        digitRule.setNumberOfCharacters(2);

        CharacterRule specialCharRule = new CharacterRule(SPECIAL_CHARACTERS);
        specialCharRule.setNumberOfCharacters(2);

        return generator.generatePassword(10, List.of(lowerCaseRule, upperCaseRule, digitRule, specialCharRule));
    }

    private static final CharacterData SPECIAL_CHARACTERS = new CharacterData() {
        private static final String ERROR_CODE = "INSUFFICIENT_SPECIAL";

        @Override
        public String getErrorCode() {
            return ERROR_CODE;
        }

        @Override
        public String getCharacters() {
            return "!@#$%^&*()_+";
        }
    };
}
