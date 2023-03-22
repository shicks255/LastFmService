package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.ErrorResponse
import com.steven.hicks.lastFmService.entities.LastFmException
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
    fun handleAllExceptions(
        e: java.lang.Exception,
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unhandled exception caught: ${e.message}", e)
        val er = ErrorResponse(0, "Unknown Error")
        return ResponseEntity(er, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(LastFmException::class)
    @Logged
    fun handleServiceException(
        e: LastFmException
    ): ResponseEntity<ErrorResponse> {
        logger.error("Service exception caught: ${e.message}", e)
        return ResponseEntity(e.errorResponse, e.status)
    }
}
