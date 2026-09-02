package com.ambrosia.report_service.report.utils;

import java.util.Locale;
import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class Iso6391Validator implements ConstraintValidator<Iso6391, String>{
    private static final Set<String> LANGS = Set.of(Locale.getISOLanguages());
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && LANGS.contains(value);
    }
}
