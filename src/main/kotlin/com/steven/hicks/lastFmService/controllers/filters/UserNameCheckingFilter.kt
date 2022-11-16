package com.steven.hicks.lastFmService.controllers.filters

import com.fasterxml.jackson.databind.ObjectMapper
import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

// @Component
class UserNameCheckingFilter(val scrobbleRepository: ScrobbleRepository, val objectMapper: ObjectMapper) : Filter {

    companion object {
        const val BAD_REQUEST_ERROR_CODE = 4000
    }

    val logger: Logger = LoggerFactory.getLogger(UserNameCheckingFilter::class.java)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {

        val userName = request?.getParameter("userName") ?: ""
        if (!scrobbleRepository.findDistinctByUserName().contains(userName.toLowerCase())) {
            val httpResponse = response as HttpServletResponse
            val typeAndResponseStatus = LastFmException.getTypeAndStatus(BAD_REQUEST_ERROR_CODE)

            httpResponse.status = typeAndResponseStatus.second.value()
            httpResponse.addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            httpResponse.outputStream.apply {
                write(objectMapper.writeValueAsBytes(typeAndResponseStatus.first))
                flush()
                close()
            }

            logger.info((request as HttpServletRequest).servletPath + " called with unknown user: $userName")
            return
        }

        chain!!.doFilter(request, response)
    }
}
