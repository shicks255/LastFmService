package com.steven.hicks.lastFmService.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.DataByDay
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByAlbum
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByArtist
import com.steven.hicks.lastFmService.controllers.dtos.response.ResponseByAlbum
import com.steven.hicks.lastFmService.controllers.dtos.response.ResponseByArtist
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import com.steven.hicks.lastFmService.services.ScrobbleService
import com.steven.hicks.lastFmService.services.StatsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@WebMvcTest(controllers = [ScrobbleController::class])
@ExtendWith(MockitoExtension::class)
class ScrobbleControllerTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var scrobbleService: ScrobbleService

    // needed for filter
    @MockBean
    lateinit var scrobbleRepository: ScrobbleRepository

    @MockBean
    lateinit var statService: StatsService

    @BeforeEach
    fun setupUser() {
        `when`(scrobbleRepository.findDistinctByUserName())
            .thenReturn(listOf("shicks255"))
    }

    @Test
    fun `should get scrobbles`() {

        val request = ScrobbleRequest(
            userName = "shicks255",
            artistName = "Pink Floyd",
            albumName = "Animals",
            from = LocalDate.of(2020, 3, 16),
            to = LocalDate.of(2021, 3, 16),
            limit = null,
            sort = null,
            direction = null
        )

        `when`(scrobbleService.getTracks(request))
            .thenReturn(
                listOf(
                    Scrobble(
                        1,
                        "shicks255",
                        "Dogs",
                        "",
                        "Pink Floyd",
                        "",
                        "Animals",
                        111L
                    )
                )
            )

        val response = mockMvc.perform(
            get("/api/v1/scrobbles")
                .characterEncoding("utf-8")
                .param("userName", "shicks255")
                .param("artistName", "Pink Floyd")
                .param("albumName", "Animals")
                .param("from", "2020-03-16")
                .param("to", "2021-03-16")
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it.request.contentAsString)
                println(it.response.contentAsString)
            }
            .andReturn()

        verify(scrobbleService, times(1))
            .getTracks(request)

        assertThat(response.response.contentAsString).contains("shicks255", "Pink Floyd", "Dogs", "Animals")
    }

    @Test
    fun `should get scrobbles grouped`() {
        val request = GroupedScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2020, 3, 16),
            to = LocalDate.of(2021, 3, 16),
            timeGroup = TimeGroup.DAY
        )

        `when`(scrobbleService.getTracksGrouped(request))
            .thenReturn(
                listOf(
                    DataByDay(
                        plays = 1,
                        timeGroup = "DAY"
                    )
                )
            )

        val response = mockMvc.perform(
            get("/api/v1/scrobbles/grouped")
                .characterEncoding("utf-8")
                .param("userName", "shicks255")
                .param("from", "2020-03-16")
                .param("to", "2021-03-16")
                .param("timeGroup", TimeGroup.DAY.toString())
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it.request.contentAsString)
                println(it.response.contentAsString)
            }
            .andReturn()

        verify(scrobbleService, times(1))
            .getTracksGrouped(request)

        assertThat(response.response.contentAsString).contains("plays", "timeGroup")
    }

    @Test
    fun `should get artist scrobbles grouped`() {
        val request = GroupedArtistScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2020, 3, 16),
            to = LocalDate.of(2021, 3, 16),
            artistNames = listOf("Pink Floyd"),
            timeGroup = TimeGroup.DAY,
            limit = null,
            empties = null
        )

        `when`(scrobbleService.getArtistTracksGrouped(request))
            .thenReturn(
                GroupedResponseByArtist(
                    data = listOf(
                        ResponseByArtist(
                            artistName = "",
                            total = 1,
                            data = mutableListOf(
                                DataByDay(
                                    plays = 1,
                                    timeGroup = "DAY"
                                )
                            )
                        )
                    )
                )
            )

        val response = mockMvc.perform(
            get("/api/v1/scrobbles/artistsGrouped")
                .characterEncoding("utf-8")
                .param("userName", "shicks255")
                .param("from", "2020-03-16")
                .param("to", "2021-03-16")
                .param("artistNames", "Pink Floyd")
                .param("timeGroup", TimeGroup.DAY.toString())

        )
            .andExpect(status().isOk)
            .andExpect {
                println(it.request.contentAsString)
                println(it.response.contentAsString)
            }
            .andReturn()

        verify(scrobbleService, times(1))
            .getArtistTracksGrouped(request)

        assertThat(response.response.contentAsString).contains("artistName", "plays", "timeGroup")
    }

    @Test
    fun `should get album scrobbles grouped`() {
        val request = GroupedAlbumScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2020, 3, 16),
            to = LocalDate.of(2021, 3, 16),
            albumNames = listOf("Reign In Blood"),
            timeGroup = TimeGroup.DAY,
            limit = null,
            empties = null
        )

        `when`(scrobbleService.getAlbumTracksGrouped(request))
            .thenReturn(
                GroupedResponseByAlbum(
                    data = listOf(
                        ResponseByAlbum(
                            albumName = "",
                            artistName = "",
                            total = 1,
                            data = mutableListOf(
                                DataByDay(
                                    plays = 1,
                                    timeGroup = "DAY"
                                )
                            )
                        )
                    )
                )
            )

        val response = mockMvc.perform(
            get("/api/v1/scrobbles/albumsGrouped")
                .characterEncoding("utf-8")
                .param("userName", "shicks255")
                .param("from", "2020-03-16")
                .param("to", "2021-03-16")
                .param("albumNames", "Reign In Blood")
                .param("timeGroup", TimeGroup.DAY.toString())
        )
            .andExpect(status().isOk)
            .andExpect {
                println(it.request.contentAsString)
                println(it.response.contentAsString)
            }
            .andReturn()

        verify(scrobbleService, times(1))
            .getAlbumTracksGrouped(request)

        assertThat(response.response.contentAsString).contains("albumName", "plays", "timeGroup")
    }
}
