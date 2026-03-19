package com.xiaoluo.syservice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class SyServiceApplicationTests {

    @Test
    void applicationClassIsLoadable() {
        assertDoesNotThrow(() -> Class.forName("com.xiaoluo.syservice.SyServiceApplication"));
    }
}
