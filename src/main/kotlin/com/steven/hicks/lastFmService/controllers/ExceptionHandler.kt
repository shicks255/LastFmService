package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.controllers.dtos.ErrorObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler {

    val logger = LoggerFactory.getLogger(com.steven.hicks.lastFmService.controllers.ExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(e: java.lang.Exception): ResponseEntity<ErrorObject> {
        logger.error("Unhandled exception caught: ${e.message}")

        return ResponseEntity(ErrorObject("An exception has occured"), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
