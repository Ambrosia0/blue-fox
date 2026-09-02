package org.ambrosia.notification_service.util;

import java.time.Duration;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "app")
public interface AppConfiguration {
    @WithDefault(value = "PT15S")
    Duration heartbitInterval();

    @WithDefault(value = "PT2M")
    Duration leaseRefreshInterval();
}
