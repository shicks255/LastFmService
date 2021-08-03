package com.steven.hicks.lastFmService.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.steven.hicks.lastFmService.controllers.dtos.response.*
import com.steven.hicks.lastFmService.services.DataLoadService
import com.steven.hicks.lastFmService.services.LastFmLoadingService
import com.steven.hicks.lastFmService.services.StatsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.Period

@WebMvcTest(controllers = [UserController::class])
@ExtendWith(MockitoExtension::class)
class UserControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var statsService: StatsService

    @MockBean
    lateinit var dataLoadService: DataLoadService

    @MockBean
    lateinit var lastFmLoadingService: LastFmLoadingService

    @Test
    fun `should get stats`() {

        val oldestAndNewestStat = OldestAndNewestStat(
            name = "Pink Floyd",
            extra = null,
            timeStat = TimeStat(
                oldest = LocalDate.now(),
                newest = LocalDate.now(),
                difference = Period.ofMonths(1)
            )
        )

        val longestDormancyStat = LongestDormancyStat(
            name = "Pink Floyd",
            extra = null,
            timeStat = TimeStat(
                oldest = LocalDate.now(),
                newest = LocalDate.now(),
                difference = Period.ofMonths(1)
            )
        )

        `when`(statsService.getStats("shicks255"))
            .thenReturn(
                UserStats(
                    oldestAndNewestAlbum = oldestAndNewestStat,
                    oldestAndNewestArtist = oldestAndNewestStat,
                    longestDormancyAlbum = longestDormancyStat,
                    longestDormancyArtist = longestDormancyStat
                )
            )

        val response = mockMvc.perform(
            get("/api/v1/user/stats")
                .characterEncoding("utf-8")
                .param("userName", "shicks255")
        )
            .andExpect(status().isOk)
            .andReturn()

        assertThat(response.response.contentAsString.contains("Pink Floyd"))
        verify(statsService, times(1)).getStats("shicks255")
    }

    @Test
    fun `should load scrobbles`() {
        val response = mockMvc.perform(
            post("/api/v1/user/load")
                .characterEncoding("utf-8")
                .param("userName", "shicks255")
        )
            .andExpect(status().isAccepted)
            .andReturn()
    }

    @Test
    fun `should return loadStatus`() {
        `when`(dataLoadService.getDataLoadStatus("shicks255"))
            .thenReturn(LoadStatusResponse(
                currentPage = 1,
                totalPages = 1,
                message = "almost done"
            ))

        val response = mockMvc.perform(
            get("/api/v1/user/loadStatus")
                .characterEncoding("utf-8")
                .param("userName", "shicks255")
        )
            .andExpect(status().isOk)
            .andReturn()

        assertThat(response.response.contentAsString.contains("almost done"))
        verify(dataLoadService, times(1)).getDataLoadStatus("shicks255")
    }

}