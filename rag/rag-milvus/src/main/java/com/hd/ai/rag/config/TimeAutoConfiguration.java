package com.hd.ai.rag.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class TimeAutoConfiguration {

    @Bean
    @Description("获取某个城市的时间.")
    public GetCurrentTimeByTimeZoneIdService timeHelper() {
        return new GetCurrentTimeByTimeZoneIdService();
    }

}
