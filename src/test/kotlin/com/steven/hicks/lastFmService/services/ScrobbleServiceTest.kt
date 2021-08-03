package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.mockGroupedScrobbleRequest
import com.steven.hicks.lastFmService.mockScrobbleRequest
import com.steven.hicks.lastFmService.mockedGroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.mockedGroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigInteger
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class ScrobbleServiceTest {

    @Mock
    lateinit var scrobbleRepository: ScrobbleRepository

    @Captor
    lateinit var captor: ArgumentCaptor<Scrobble>

    @InjectMocks
    lateinit var sut: ScrobbleService

    @Test
    fun `should get tracks`() {
        `when`(
            scrobbleRepository.getScrobbles(mockScrobbleRequest)
        )
            .thenReturn(
                listOf(
                    Scrobble(
                        id = 1,
                        userName = "shicks255",
                        name = "Test",
                        artistMbid = "",
                        artistName = "",
                        albumName = "",
                        albumMbid = "",
                        time = 12345678
                    )
                )
            )

        val results = sut.getTracks(mockScrobbleRequest)

        verify(scrobbleRepository, times(1))
            .getScrobbles(mockScrobbleRequest)

        assertThat(results).hasSize(1)
        assertThat(results.first().name).isEqualTo("Test")
    }

    @Test
    fun `should get tracks grouped`() {
        `when`(
            scrobbleRepository.getGroupedScrobbles(mockGroupedScrobbleRequest)
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "11")
                )
            )

        val results = sut.getTracksGrouped(
            mockGroupedScrobbleRequest
        )

        verify(scrobbleRepository, times(1))
            .getGroupedScrobbles(
                mockGroupedScrobbleRequest
            )

        assertThat(results).hasSize(1)
        assertThat(results.first().plays).isEqualTo(1)
        assertThat(results.first().timeGroup).isEqualTo("11")
    }

    @Test
    fun `should get artist tracks grouped with empties`() {
        `when`(
            scrobbleRepository.getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(empties = true)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-10-31", "Slayer")
                )
            )

        val results = sut.getArtistTracksGrouped(
            mockedGroupedArtistScrobbleRequest.copy(empties = true)
        )

        verify(scrobbleRepository, times(1))
            .getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(empties = true)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().data[1].plays).isEqualTo(0) //because 1 empty
        assertThat(results.data.first().artistName).isEqualTo("Slayer")
    }

    @Test
    fun `should get artist tracks grouped`() {
        `when`(
            scrobbleRepository.getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-10-31", "Slayer"),
                    arrayOf(BigInteger.TEN, "2021-11-01", "Slayer")
                )
            )

        val results = sut.getArtistTracksGrouped(
            mockedGroupedArtistScrobbleRequest
        )

        verify(scrobbleRepository, times(1))
            .getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().data[1].plays).isEqualTo(10)
        assertThat(results.data.first().artistName).isEqualTo("Slayer")
    }

    @Test
    fun `should get artist tracks grouped by week`() {
        `when`(
            scrobbleRepository.getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(timeGroup = TimeGroup.WEEK)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-11-01", "Slayer")
                )
            )

        val results = sut.getArtistTracksGrouped(
            mockedGroupedArtistScrobbleRequest.copy(timeGroup = TimeGroup.WEEK)
        )

        verify(scrobbleRepository, times(1))
            .getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(timeGroup = TimeGroup.WEEK)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().artistName).isEqualTo("Slayer")
    }

    @Test
    fun `should get artist tracks grouped by month`() {
        `when`(
            scrobbleRepository.getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(timeGroup = TimeGroup.MONTH)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-11-01", "Slayer")
                )
            )

        val results = sut.getArtistTracksGrouped(
            mockedGroupedArtistScrobbleRequest.copy(timeGroup = TimeGroup.MONTH)
        )

        verify(scrobbleRepository, times(1))
            .getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(timeGroup = TimeGroup.MONTH)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().artistName).isEqualTo("Slayer")
    }

    @Test
    fun `should get artist tracks grouped by year`() {
        `when`(
            scrobbleRepository.getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(timeGroup = TimeGroup.YEAR)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-11-01", "Slayer")
                )
            )

        val results = sut.getArtistTracksGrouped(
            mockedGroupedArtistScrobbleRequest.copy(empties = false, timeGroup = TimeGroup.YEAR)
        )

        verify(scrobbleRepository, times(1))
            .getArtistGroupedScrobbles(
                mockedGroupedArtistScrobbleRequest.copy(empties = false, timeGroup = TimeGroup.YEAR)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().artistName).isEqualTo("Slayer")
    }

    @Test
    fun `should get album tracks grouped with empties`() {
        `when`(
            scrobbleRepository.getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(empties = true)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-10-31", "Bleed American", "Jimmy Eat World")
                )
            )

        val results = sut.getAlbumTracksGrouped(
            mockedGroupedAlbumScrobbleRequest.copy(empties = true)
        )

        verify(scrobbleRepository, times(1))
            .getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(empties = true)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().data[1].plays).isEqualTo(0) // because 1 empty
        assertThat(results.data.first().albumName).isEqualTo("Bleed American - Jimmy Eat World")
    }

    @Test
    fun `should get album tracks grouped by day`() {
        `when`(
            scrobbleRepository.getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-10-31", "Bleed American", "Jimmy Eat World"),
                    arrayOf(BigInteger.TEN, "2021-11-01", "Bleed American", "Jimmy Eat World")
                )
            )

        val results = sut.getAlbumTracksGrouped(
            mockedGroupedAlbumScrobbleRequest
        )

        verify(scrobbleRepository, times(1))
            .getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().data[1].plays).isEqualTo(10)
        assertThat(results.data.first().albumName).isEqualTo("Bleed American - Jimmy Eat World")
    }

    @Test
    fun `should get album tracks grouped by week`() {
        `when`(
            scrobbleRepository.getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.WEEK)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-11-01", "Bleed American", "Jimmy Eat World")
                )
            )

        val results = sut.getAlbumTracksGrouped(
            mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.WEEK)
        )

        verify(scrobbleRepository, times(1))
            .getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.WEEK)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().albumName).isEqualTo("Bleed American - Jimmy Eat World")
    }

    @Test
    fun `should get album tracks grouped by month`() {
        `when`(
            scrobbleRepository.getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.MONTH)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-11-01", "Bleed American", "Jimmy Eat World")
                )
            )

        val results = sut.getAlbumTracksGrouped(
            mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.MONTH)
        )

        verify(scrobbleRepository, times(1))
            .getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.MONTH)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().albumName).isEqualTo("Bleed American - Jimmy Eat World")
    }

    @Test
    fun `should get album tracks grouped by year`() {
        `when`(
            scrobbleRepository.getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.YEAR)
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-11-01", "Bleed American", "Jimmy Eat World")
                )
            )

        val results = sut.getAlbumTracksGrouped(
            mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.YEAR)
        )

        verify(scrobbleRepository, times(1))
            .getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest.copy(timeGroup = TimeGroup.YEAR)
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().albumName).isEqualTo("Bleed American - Jimmy Eat World")
    }

    @Test
    fun `should get album tracks grouped`() {
        `when`(
            scrobbleRepository.getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest
            )
        )
            .thenReturn(
                listOf(
                    arrayOf(BigInteger.ONE, "2021-11-01", "Bleed American", "Jimmy Eat World")
                )
            )

        val results = sut.getAlbumTracksGrouped(
            mockedGroupedAlbumScrobbleRequest
        )

        verify(scrobbleRepository, times(1))
            .getAlbumGroupedScrobbles(
                mockedGroupedAlbumScrobbleRequest
            )

        assertThat(results.data).hasSize(1)
        assertThat(results.data.first().data.first().plays).isEqualTo(1)
        assertThat(results.data.first().albumName).isEqualTo("Bleed American - Jimmy Eat World")
    }

    @Test
    fun `should get artists`() {
        `when`(scrobbleRepository.suggestArtists("shicks255", "Pink Floyd"))
            .thenReturn(listOf("Pink Floyd"))

        val results = sut.getArtists("shicks255", "Pink Floyd")

        verify(scrobbleRepository, times(1)).suggestArtists("shicks255", "Pink Floyd")
        assertThat(results.size).isEqualTo(1)
        assertThat(results.first()).isEqualTo("Pink Floyd")
    }

    @Test
    fun `should get albums`() {
        `when`(scrobbleRepository.suggestAlbums("shicks255", "Dark Side"))
            .thenReturn(listOf("The Dark Side of the Moon", "Dark Side of the Moon"))

        val results = sut.getAlbums("shicks255", "Dark Side")

        verify(scrobbleRepository, times(1)).suggestAlbums("shicks255", "Dark Side")
        assertThat(results.size).isEqualTo(2)
        assertThat(results.first()).isEqualTo("The Dark Side of the Moon")
    }
}
