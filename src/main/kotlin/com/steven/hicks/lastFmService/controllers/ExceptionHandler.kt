package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.ErrorObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    val logger: Logger =
        LoggerFactory.getLogger(com.steven.hicks.lastFmService.controllers.ExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    @Logged
    fun handleAllExceptions(e: java.lang.Exception): ResponseEntity<ErrorObject> {
        logger.error("Unhandled exception caught: ${e.message}")
        e.printStackTrace()

        return ResponseEntity(ErrorObject("An exception has occurred"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
