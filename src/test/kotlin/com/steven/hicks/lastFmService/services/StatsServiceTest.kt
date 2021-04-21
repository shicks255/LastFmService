package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Period

@ExtendWith(MockitoExtension::class)
class StatsServiceTest {

    @Mock
    lateinit var scrobbleRepository: ScrobbleRepository

    @InjectMocks
    lateinit var sut: StatsService

    @Test
    fun `should get longest dormancy album`() {
        `when`(scrobbleRepository.getLongestDormancy("shicks255", "album_name"))
            .thenReturn(
                listOf(
                    arrayOf(
                        "Pink Floyd",
                        1618888555.0,
                        1611118094.0,
                        "",
                        "Animals"
                    )
                )
            )

        val result = sut.getLongestDormancy("shicks255", "album_name")
        println(result)

        assertThat(result.name)
            .isEqualTo("Pink Floyd")
        assertThat(result.extra)
            .isEqualTo("Animals")
        assertThat(result.timeStat.newest)
            .isEqualTo("2021-04-20")
        assertThat(result.timeStat.oldest)
            .isEqualTo("2021-01-20")
        assertThat(result.timeStat.difference)
            .isEqualTo(Period.ofMonths(3))
    }

    @Test
    fun `should get longest dormancy artist`() {

        `when`(scrobbleRepository.getLongestDormancy("shicks255", "artist_name"))
            .thenReturn(
                listOf(
                    arrayOf(
                        "Pink Floyd",
                        1618888555.0,
                        1611118094.0,
                        "",
                    )
                )
            )

        val result = sut.getLongestDormancy("shicks255", "artist_name")
        assertThat(result.name)
            .isEqualTo("Pink Floyd")
        assertThat(result.timeStat.newest)
            .isEqualTo("2021-04-20")
        assertThat(result.timeStat.oldest)
            .isEqualTo("2021-01-20")
        assertThat(result.timeStat.difference)
            .isEqualTo(Period.ofMonths(3))
    }

    @Test
    fun `should get oldest and newest album`() {
        `when`(scrobbleRepository.getOldestAndNewestPlay("shicks255", "album_name"))
            .thenReturn(
                listOf(
                    arrayOf(
                        "Pink Floyd",
                        1618888555.0,
                        1611118094.0,
                        "",
                        "Animals"
                    )
                )
            )

        val result = sut.getOldestAndNewest("shicks255", "album_name")
        assertThat(result.name)
            .isEqualTo("Pink Floyd")
        assertThat(result.extra)
            .isEqualTo("Animals")
        assertThat(result.timeStat.newest)
            .isEqualTo("2021-04-20")
        assertThat(result.timeStat.oldest)
            .isEqualTo("2021-01-20")
        assertThat(result.timeStat.difference)
            .isEqualTo(Period.ofMonths(3))
    }

    @Test
    fun `should get oldest and newest artist`() {
        `when`(scrobbleRepository.getOldestAndNewestPlay("shicks255", "artist_name"))
            .thenReturn(
                listOf(
                    arrayOf(
                        "Pink Floyd",
                        1618888555.0,
                        1611118094.0,
                        "",
                    )
                )
            )

        val result = sut.getOldestAndNewest("shicks255", "artist_name")
        assertThat(result.name)
            .isEqualTo("Pink Floyd")
        assertThat(result.timeStat.newest)
            .isEqualTo("2021-04-20")
        assertThat(result.timeStat.oldest)
            .isEqualTo("2021-01-20")
        assertThat(result.timeStat.difference)
            .isEqualTo(Period.ofMonths(3))
    }
}
