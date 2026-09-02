package org.ambrosia;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.smallrye.reactive.messaging.memory.InMemoryConnector;

public class KafkaConnector implements QuarkusTestResourceLifecycleManager{
    @Override
    public Map<String, String> start() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> props = InMemoryConnector.switchIncomingChannelsToInMemory(
            "content-channel",
            "post-like-channel",
            "comment-like-channel",
            "post-view-channel"
        );
        env.putAll(props);
        return env;
    }

    @Override
    public void stop() {
        InMemoryConnector.clear();
    }
}
