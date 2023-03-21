package com.steven.hicks.lastFmService.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.steven.hicks.lastFmService.controllers.filters.AccessLoggingFilter
import com.steven.hicks.lastFmService.controllers.filters.UserNameCheckingFilter
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class FilterRegistrationConfig(val scrobbleRepository: ScrobbleRepository, val objectMapper: ObjectMapper) {

    @Bean
    fun userNameFilter(): FilterRegistrationBean<UserNameCheckingFilter> {
        val usernameCheckingFilter = FilterRegistrationBean<UserNameCheckingFilter>()
        usernameCheckingFilter.filter = UserNameCheckingFilter(scrobbleRepository, objectMapper)
        usernameCheckingFilter.addUrlPatterns("/api/v1/search/*")
        usernameCheckingFilter.addUrlPatterns("/api/v1/scrobbles/*")
        usernameCheckingFilter.addUrlPatterns("/api/v1/user/*")

        return usernameCheckingFilter
    }

    @Bean
    fun accessLoggingFilter(): FilterRegistrationBean<AccessLoggingFilter> {
        val accessLoggingFilter = FilterRegistrationBean<AccessLoggingFilter>()
        accessLoggingFilter.filter = AccessLoggingFilter()
        accessLoggingFilter.addUrlPatterns("/api/*")

        return accessLoggingFilter
    }
}
