package com.steven.hicks.lastFmService.services

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.steven.hicks.lastFmService.longestDormancyAlbum
import com.steven.hicks.lastFmService.longestDormancyArtist
import com.steven.hicks.lastFmService.oldestNewestAlbum
import com.steven.hicks.lastFmService.oldestNewestArtist
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import kotlinx.coroutines.runBlocking
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
    fun `should get stats`() {
        `when`(scrobbleRepository.getLongestDormancy("shicks255", "album_name"))
            .thenReturn(longestDormancyAlbum)
        `when`(scrobbleRepository.getLongestDormancy("shicks255", "artist_name"))
            .thenReturn(longestDormancyArtist)
        `when`(scrobbleRepository.getOldestAndNewestPlay("shicks255", "album_name"))
            .thenReturn(oldestNewestAlbum)
        `when`(scrobbleRepository.getOldestAndNewestPlay("shicks255", "artist_name"))
            .thenReturn(oldestNewestArtist)

        val result = sut.getStats("shicks255")


        verify(scrobbleRepository, times(2)).getLongestDormancy(eq("shicks255"), any())
        verify(scrobbleRepository, times(2)).getOldestAndNewestPlay(eq("shicks255"), any())
    }

    @Test
    fun `should get longest dormancy album`() {
        `when`(scrobbleRepository.getLongestDormancy("shicks255", "album_name"))
            .thenReturn(longestDormancyAlbum)

        runBlocking {
            val result = sut.getLongestDormancy("shicks255", "album_name")

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
    }

    @Test
    fun `should get longest dormancy artist`() {

        `when`(scrobbleRepository.getLongestDormancy("shicks255", "artist_name"))
            .thenReturn(longestDormancyArtist)

        runBlocking {

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
    }

    @Test
    fun `should get oldest and newest album`() {
        `when`(scrobbleRepository.getOldestAndNewestPlay("shicks255", "album_name"))
            .thenReturn(oldestNewestAlbum)

        runBlocking {
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
    }

    @Test
    fun `should get oldest and newest artist`() {
        `when`(scrobbleRepository.getOldestAndNewestPlay("shicks255", "artist_name"))
            .thenReturn(oldestNewestArtist)

        runBlocking {
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
}
