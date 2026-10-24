package org.ambrosia.notification_service.utils;

import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    public static Long randLong(){
        return ThreadLocalRandom.current().nextLong();
    }
}
