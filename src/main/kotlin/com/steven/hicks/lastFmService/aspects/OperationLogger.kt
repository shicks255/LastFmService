package com.steven.hicks.lastFmService.aspects

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import net.logstash.logback.argument.StructuredArguments.kv
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.CodeSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.Stack
import java.util.UUID

@Component
@Aspect
class OperationLogger(
    val objectMapper: ObjectMapper
) {

    companion object {
        const val MAX_LOG_LENGTH = 2500
    }

    val logger: Logger = LoggerFactory.getLogger("OperationLogger")
    val logContext = ThreadLocal<Stack<LoggedValueContainer>>()

    data class LoggedValueContainer(
        val start: Long,
        val args: Map<String, Any>,
        val clazz: String,
        val operation: String,
        val traceId: UUID
    )

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
        var traceId = UUID.randomUUID()

        if (logContext.get() == null) {
            logContext.set(Stack())
        }

        if (logContext.get().size > 0) {
            traceId = logContext.get().peek().traceId
        }

        val paramNames = (joinPoint.signature as CodeSignature).parameterNames
        val argList = paramNames.zip(args)
            .filter { x -> x.second != null }
            .toMap()

        val x = objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(argList)

        logContext.get().push(
            LoggedValueContainer(
                start = startTime,
                args = argList,
                clazz = clazz,
                operation = operation,
                traceId = traceId
            )
        )

        logger.info(
            "Start operationLog",
            kv("class", clazz),
            kv("operation", operation),
            kv("args", x),
            kv("stage", "start"),
            kv("traceId", traceId)
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

        var statusCode: Int = 0
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

        if (clazz.contains("controller")) {
            statusCode = HttpStatus.OK.value()
        }

        logger.info(
            "End operationLog",
            kv("class", clazz),
            kv("operation", operation),
            kv("stage", "end"),
            kv("latency", latency),
            kv("result", returnValue),
            kv("status", status),
            kv("statusCode", statusCode),
            kv("traceId", loggedValues.traceId)
        )
    }
}
