package com.example.demo.validation.impl;

import com.example.demo.exception.PasswordValidationException;
import com.example.demo.exception.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.passay.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component
public class PasswordValidation {

    public final static String SPECIAL_CHARACTER = "-_=$#";

    public void validation(String password) {
        List<Rule> rules = List.of(
                new LengthRule(4, 16),
                new WhitespaceRule(),
                new EmailRule(),
                new AllowedRegexRule("[a-zA-Z0-9#\\$=_\\-]*")
        );

        PasswordValidator validator = new PasswordValidator(rules);
        RuleResult result = validator.validate(new PasswordData(password));

        if (result.isValid()) return;

        List<String> messages = validator.getMessages(result);
        String messageTemplate = String.join(", ", messages);

        throw new PasswordValidationException(messageTemplate);
    }

    private CharacterData specialCharacters() {
        return new CharacterData() {
            public String getErrorCode() {
                return "INSUFFICIENT_SPECIAL";
            }
            public String getCharacters() {
                return SPECIAL_CHARACTER;
            }
        };
    }

}
