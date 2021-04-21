package com.steven.hicks.lastFmService.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.steven.hicks.lastFmService.controllers.dtos.GroupBy
import com.steven.hicks.lastFmService.controllers.dtos.SortBy
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.*
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.services.ScrobbleService
import com.steven.hicks.lastFmService.services.StatsService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultMatcher
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

    @MockBean
    lateinit var statService: StatsService

    @Test
    fun `should get scrobbles`() {

        val request = ScrobbleRequest(
            artistName = "Pink Floyd",
            albumName = "Animals",
            from = LocalDate.of(2020, 3, 16),
            to = LocalDate.of(2021, 3, 16),
            limit = null,
            sort = null
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
                .param("artistName", "Pink Floyd")
                .param("albumName", "Animals")
                .param("from", "2020-03-16")
                .param("to", "2021-03-16")
        )
            .andExpect(status().isOk)
            .andExpect(ResultMatcher {
                println(it.request.contentAsString)
                println(it.response.contentAsString)
            })
            .andReturn()

        verify(scrobbleService, times(1))
            .getTracks(request)

        assertThat(response.response.contentAsString).contains("shicks255", "Pink Floyd", "Dogs", "Animals")
    }

    @Test
    fun `should get scrobbles grouped`() {
        val request = GroupedScrobbleRequest(
            from = LocalDate.now(),
            to = LocalDate.now(),
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


    }

    @Test
    fun `should get album scrobbles grouped`() {
        val request = GroupedAlbumScrobbleRequest(
            from = LocalDate.now(),
            to = LocalDate.now(),
            albumNames = listOf("Reign In Blood"),
            group = GroupBy.ARTIST,
            sort = SortBy.ALBUM,
            timeGroup = TimeGroup.DAY
        )

        `when`(scrobbleService.getAlbumTracksGrouped(request))
            .thenReturn(
                GroupedResponseByAlbum(
                    data = listOf(
                        ResponseByAlbum(
                            albumName = "",
                            total = 1,
                            data = listOf(
                                DataByDay(
                                    plays = 1,
                                    timeGroup = "DAY"
                                )
                            )
                        )
                    )
                )
            )
    }

    @Test
    fun `should get artist scrobbles grouped`() {
        val request = GroupedArtistScrobbleRequest(
            from = LocalDate.now(),
            to = LocalDate.now(),
            artistNames = listOf("Pink Floyd"),
            group = GroupBy.ARTIST,
            sort = SortBy.ALBUM,
            timeGroup = TimeGroup.DAY
        )

        `when`(scrobbleService.getArtistTracksGrouped(request))
            .thenReturn(
                GroupedResponseByArtist(
                    data = listOf(
                        ResponseByArtist(
                            artistName = "",
                            total = 1,
                            data = listOf(
                                DataByDay(
                                    plays = 1,
                                    timeGroup = "DAY"
                                )
                            )
                        )
                    )
                )
            )
    }
}
