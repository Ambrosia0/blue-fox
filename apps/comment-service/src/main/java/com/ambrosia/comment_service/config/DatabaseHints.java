package com.ambrosia.comment_service.config;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

import com.google.protobuf.ExtensionRegistry;

import liquibase.change.core.SQLFileChange;

public class DatabaseHints implements RuntimeHintsRegistrar{
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        hints.resources().registerPattern("db/**");
        hints.resources().registerPattern("elastic-settings.json");
        hints.reflection().registerType(SQLFileChange.class, MemberCategory.INVOKE_PUBLIC_METHODS);
        hints.reflection().registerType(ExtensionRegistry.class, MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}
