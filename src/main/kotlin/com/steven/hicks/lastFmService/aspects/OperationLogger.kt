package com.steven.hicks.lastFmService.aspects

import net.logstash.logback.argument.StructuredArguments.kv
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.Stack

@Component
@Aspect
class OperationLogger {

    companion object {
        const val MAX_LOG_LENGTH = 2500
    }

    val logger: Logger = LoggerFactory.getLogger(OperationLogger::class.java)
    val logContext = ThreadLocal<Stack<LoggedValueContainer>>()

    data class LoggedValueContainer(val start: Long, val args: Array<*>, val clazz: String, val operation: String)

    @Pointcut("@annotation(com.steven.hicks.lastFmService.aspects.Logged)")
    fun pointCut() {
        println("")
    }

    @Before("pointCut()")
    fun before(joinPoint: JoinPoint) {

        val startTime = System.currentTimeMillis()
        val args = joinPoint.args
        val clazz = joinPoint.signature.declaringType.name
        val operation = joinPoint.signature.name

        if (logContext.get() == null) {
            logContext.set(Stack())
        }

        logContext.get().push(
            LoggedValueContainer(
                start = startTime,
                args = args,
                clazz = clazz,
                operation = operation
            )
        )

        logger.info(
            "{} {} {} {}",
            kv("class", clazz),
            kv("operation", operation),
            kv("args", args),
            kv("stage", "start")
        )
    }

    @AfterReturning("pointCut()", returning = "returnedObject")
    fun around(returnedObject: Any?) {

        val loggedValues = logContext.get().pop()
        val latency = System.currentTimeMillis() - loggedValues.start

        val clazz = loggedValues.clazz
        val operation = loggedValues.operation

        var returnValue = returnedObject?.toString() ?: ""
        if (returnValue.length > MAX_LOG_LENGTH) {
            returnValue = returnValue.substring(0, MAX_LOG_LENGTH)
        }

        var statusCode: Int? = null
        var status = "success"
        if (returnedObject is ResponseEntity<*>) {
            statusCode = returnedObject.statusCode.value()
            if (returnedObject.statusCode.is4xxClientError || returnedObject.statusCode.is5xxServerError) {
                status = "failure"
            }
        }

        if (returnedObject is HttpStatus) {
            statusCode = returnedObject.value()
        }

        if (statusCode == null && clazz.contains("controller")) {
            statusCode = HttpStatus.OK.value()
        }

        logger.info(
            "{} {} {} {} {} {} {}",
            kv("class", clazz),
            kv("operation", operation),
            kv("stage", "end"),
            kv("latency", latency),
            kv("result", returnValue),
            kv("status", status),
            kv("statusCode", statusCode)
        )
    }
}
