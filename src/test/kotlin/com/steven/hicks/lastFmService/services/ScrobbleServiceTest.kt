package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedAlbumResultMapper
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedArtistResultMapper
import com.steven.hicks.lastFmService.entities.resultMappers.GroupedResultMapper
import com.steven.hicks.lastFmService.mockedGroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.mockedGroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.mockedGroupedScrobbleRequest
import com.steven.hicks.lastFmService.mockedScrobbleRequest
import com.steven.hicks.lastFmService.repositories.ScrobbleRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ScrobbleServiceTest {

    @Mock
    lateinit var scrobbleRepository: ScrobbleRepository

    @InjectMocks
    lateinit var sut: ScrobbleService

    @Test
    fun `should get tracks`() {
        `when`(
            scrobbleRepository.getScrobbles(mockedScrobbleRequest)
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

        val results = sut.getTracks(mockedScrobbleRequest)

        verify(scrobbleRepository, times(1))
            .getScrobbles(mockedScrobbleRequest)

        assertThat(results).hasSize(1)
        assertThat(results.first().name).isEqualTo("Test")
    }

    @Test
    fun `should get tracks grouped`() {
        `when`(
            scrobbleRepository.getGroupedScrobbles(mockedGroupedScrobbleRequest)
        )
            .thenReturn(
                listOf(
                    GroupedResultMapper(1, "11")
                )
            )

        val results = sut.getTracksGrouped(
            mockedGroupedScrobbleRequest
        )

        verify(scrobbleRepository, times(1))
            .getGroupedScrobbles(
                mockedGroupedScrobbleRequest
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
                    GroupedArtistResultMapper(1, "2021-10-31", "Slayer")
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
        assertThat(results.data.first().data[1].plays).isEqualTo(0) // because 1 empty
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
                    GroupedArtistResultMapper(1, "2021-10-31", "Slayer"),
                    GroupedArtistResultMapper(10, "2021-11-01", "Slayer")
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
                    GroupedArtistResultMapper(1, "2021-11-01", "Slayer")
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
                    GroupedArtistResultMapper(1, "2021-11-01", "Slayer")
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
                    GroupedArtistResultMapper(1, "2021-11-01", "Slayer")
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
                    GroupedAlbumResultMapper(1, "2021-10-31", "Bleed American", "Jimmy Eat World")
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
                    GroupedAlbumResultMapper(1, "2021-10-31", "Bleed American", "Jimmy Eat World"),
                    GroupedAlbumResultMapper(10, "2021-11-01", "Bleed American", "Jimmy Eat World")
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
                    GroupedAlbumResultMapper(1, "2021-11-01", "Bleed American", "Jimmy Eat World")
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
                    GroupedAlbumResultMapper(1, "2021-11-01", "Bleed American", "Jimmy Eat World")
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
                    GroupedAlbumResultMapper(1, "2021-11-01", "Bleed American", "Jimmy Eat World")
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
                    GroupedAlbumResultMapper(1, "2021-11-01", "Bleed American", "Jimmy Eat World")
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
