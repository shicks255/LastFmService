package com.steven.hicks.lastFmService.aspects

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

        logger.info("class={} operation={} arguments={} stage={}", clazz, operation, args, "start")
    }

    @AfterReturning("pointCut()", returning = "returnedObject")
    fun around(returnedObject: Any) {

        val loggedValues = logContext.get().pop()
        val latency = System.currentTimeMillis() - loggedValues.start

        val clazz = loggedValues.clazz
        val operation = loggedValues.operation

        var returnValue = returnedObject.toString()
        if (returnValue.length > MAX_LOG_LENGTH) {
            returnValue = returnValue.substring(0, MAX_LOG_LENGTH)
        }

        logger.info(
            "class={} operation={} stage={} latency={} result={}",
            clazz,
            operation,
            "end",
            latency,
            returnValue
        )
    }
}
