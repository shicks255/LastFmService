package com.steven.hicks.lastFmService.controllers.filters

import java.util.UUID
import net.logstash.logback.argument.StructuredArguments.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AccessLoggingFilter : Filter {

    companion object {
        val IP_HEADERS = listOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        )
    }

    private val logger: Logger = LoggerFactory.getLogger("AccessLogger")

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        val servletRequest = (request as HttpServletRequest)
        val path = servletRequest.method + "_" + servletRequest.servletPath
        val referer = servletRequest.getHeader("referer")
        val userAgent = servletRequest.getHeader("user-agent")
        val clientIp = getClientIP(servletRequest)
        val traceId = UUID.randomUUID()

        logger.info(
            "Start accessLog",
            kv("path", path),
            kv("referer", referer),
            kv("user-agent", userAgent),
            kv("client-ip", clientIp),
            kv("stage", "start"),
            kv("traceId", traceId)
        )

        val start = System.currentTimeMillis()
        chain!!.doFilter(request, response)

        logger.info(
            "End accessLog",
            kv("path", path),
            kv("referer", referer),
            kv("user-agent", userAgent),
            kv("client-ip", clientIp),
            kv("stage", "end"),
            kv("latency", System.currentTimeMillis() - start),
            kv("responseStatus", (response as HttpServletResponse).status),
            kv("traceId", traceId)
        )
    }

    private fun getClientIP(request: HttpServletRequest): String {
        for (header in IP_HEADERS) {
            if (request.getHeader(header) != null) {
                return request.getHeader(header)
            }
        }

        return ""
    }
}
