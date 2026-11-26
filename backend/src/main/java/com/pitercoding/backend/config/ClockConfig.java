package com.pitercoding.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.ZoneId;

/**
 * Single source of truth for "today": the workday date is always computed in the
 * configured application time zone (APP_TIMEZONE), not in the client's or JVM's.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock(@Value("${app.timezone:}") String timezone) {
        ZoneId zone = StringUtils.hasText(timezone) ? ZoneId.of(timezone) : ZoneId.systemDefault();
        return Clock.system(zone);
    }
}
