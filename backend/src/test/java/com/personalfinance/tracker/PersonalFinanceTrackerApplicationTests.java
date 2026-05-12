package com.personalfinance.tracker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonalFinanceTrackerApplicationTests {

    @Test
    void apiResponseFactoryWorks() {
        assertEquals("SUCCESS", com.personalfinance.tracker.common.ApiResponse.success("ok").getCode());
    }
}
