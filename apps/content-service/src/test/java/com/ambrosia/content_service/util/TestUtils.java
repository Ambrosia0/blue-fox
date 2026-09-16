package com.ambrosia.content_service.util;

import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {
    public static Long randLong(){
        return ThreadLocalRandom.current().nextLong();
    }
}
