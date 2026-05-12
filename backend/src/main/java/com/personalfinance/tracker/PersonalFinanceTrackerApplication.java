package com.personalfinance.tracker;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan({
    "com.personalfinance.tracker.account.mapper",
    "com.personalfinance.tracker.category.mapper",
    "com.personalfinance.tracker.bill.mapper",
    "com.personalfinance.tracker.budget.mapper"
})
public class PersonalFinanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalFinanceTrackerApplication.class, args);
    }
}
