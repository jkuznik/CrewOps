package pl.crewops.utils.credentialsGenerator;

import java.util.List;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class PasswordService {

    private PasswordService() {}

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
