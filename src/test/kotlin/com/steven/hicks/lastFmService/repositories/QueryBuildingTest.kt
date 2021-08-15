package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.queryBuilding.Direction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
class QueryBuildingTest {

    @Test
    fun `should build query from ScrobbleRequest`() {
        val scrobbleRequest = ScrobbleRequest(
            userName = "shicks255",
            artistName = "",
            albumName = "",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 10, 31),
            limit = 100,
            sort = ScrobbleField.NAME,
            direction = Direction.DESCENDING
        )
        val query = scrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select scrobble.id, scrobble.album_name, scrobble.album_mbid, scrobble.artist_name, scrobble.artist_mbid, scrobble.name, scrobble.time, scrobble.user_name from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.time >= 1635660000 and scrobble.time < 1635660000 order by NAME desc limit 100")
    }

    @Test
    fun `should build query from ScrobbleRequest with albumNames`() {
        val scrobbleRequest = ScrobbleRequest(
            userName = "shicks255",
            artistName = "",
            albumName = "Animals",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 10, 31),
            limit = 100,
            sort = ScrobbleField.NAME,
            direction = Direction.DESCENDING
        )
        val query = scrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select scrobble.id, scrobble.album_name, scrobble.album_mbid, scrobble.artist_name, scrobble.artist_mbid, scrobble.name, scrobble.time, scrobble.user_name from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.album_name = 'Animals' and scrobble.time >= 1635660000 and scrobble.time < 1635660000 order by NAME desc limit 100")
    }

    @Test
    fun `should build query from ScrobbleRequest with null albumName and artistName`() {
        val scrobbleRequest = ScrobbleRequest(
            userName = "shicks255",
            artistName = null,
            albumName = null,
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 10, 31),
            limit = 100,
            sort = ScrobbleField.NAME,
            direction = Direction.DESCENDING
        )
        val query = scrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select scrobble.id, scrobble.album_name, scrobble.album_mbid, scrobble.artist_name, scrobble.artist_mbid, scrobble.name, scrobble.time, scrobble.user_name from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.time >= 1635660000 and scrobble.time < 1635660000 order by NAME desc limit 100")
    }

    @Test
    fun `should build query from ScrobbleRequest with artistNames`() {
        val scrobbleRequest = ScrobbleRequest(
            userName = "shicks255",
            artistName = "Pink Floyd",
            albumName = "",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 10, 31),
            limit = 100,
            sort = ScrobbleField.NAME,
            direction = Direction.DESCENDING
        )
        val query = scrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select scrobble.id, scrobble.album_name, scrobble.album_mbid, scrobble.artist_name, scrobble.artist_mbid, scrobble.name, scrobble.time, scrobble.user_name from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.artist_name = 'Pink Floyd' and scrobble.time >= 1635660000 and scrobble.time < 1635660000 order by NAME desc limit 100")
    }

    @Test
    fun `should build query from GroupedAlbumScrobbleRequest`() {
        val groupedAlbumScrobbleRequest = GroupedAlbumScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 11, 1),
            timeGroup = TimeGroup.DAY,
            albumNames = listOf("Bleed American"),
            limit = 100,
            empties = false
        )
        val query = groupedAlbumScrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select count(*), to_char(to_timestamp(time), 'YYYY-MM-DD'), scrobble.album_name, scrobble.artist_name from SCROBBLE where scrobble.album_name <> '' and scrobble.user_name = 'shicks255' and scrobble.album_name in ('Bleed American') and scrobble.time >= 1635660000 and scrobble.time < 1635746400 group by to_char(to_timestamp(time), 'YYYY-MM-DD'),scrobble.album_name,scrobble.artist_name limit 100")
    }

    @Test
    fun `should build query from GroupedAlbumScrobbleRequest with null album names`() {
        val groupedAlbumScrobbleRequest = GroupedAlbumScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 11, 1),
            timeGroup = TimeGroup.DAY,
            albumNames = null,
            limit = 100,
            empties = true
        )
        val query = groupedAlbumScrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select count(*), to_char(to_timestamp(time), 'YYYY-MM-DD'), scrobble.album_name, scrobble.artist_name from SCROBBLE where scrobble.album_name <> '' and scrobble.user_name = 'shicks255' and scrobble.time >= 1635660000 and scrobble.time < 1635746400 group by to_char(to_timestamp(time), 'YYYY-MM-DD'),scrobble.album_name,scrobble.artist_name limit 100")
    }

    @Test
    fun `should build query from GroupedAlbumScrobbleRequest with empty album names`() {
        val groupedAlbumScrobbleRequest = GroupedAlbumScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 11, 1),
            timeGroup = TimeGroup.DAY,
            albumNames = emptyList(),
            limit = 100,
            empties = null
        )
        val query = groupedAlbumScrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select count(*), to_char(to_timestamp(time), 'YYYY-MM-DD'), scrobble.album_name, scrobble.artist_name from SCROBBLE where scrobble.album_name <> '' and scrobble.user_name = 'shicks255' and scrobble.time >= 1635660000 and scrobble.time < 1635746400 group by to_char(to_timestamp(time), 'YYYY-MM-DD'),scrobble.album_name,scrobble.artist_name limit 100")
    }

    @Test
    fun `should build query from GroupedArtistScrobbleRequest`() {
        val groupedArtistScrobbleRequest = GroupedArtistScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 11, 1),
            timeGroup = TimeGroup.DAY,
            artistNames = listOf("Slayer"),
            limit = 10,
            empties = false
        )
        val query = groupedArtistScrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select count(*), to_char(to_timestamp(time), 'YYYY-MM-DD'), scrobble.artist_name from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.artist_name in ('Slayer') and scrobble.time >= 1635660000 and scrobble.time < 1635746400 group by to_char(to_timestamp(time), 'YYYY-MM-DD'),scrobble.artist_name limit 10")
    }

    @Test
    fun `should build query from GroupedArtistScrobbleRequest with null artist names`() {
        val groupedArtistScrobbleRequest = GroupedArtistScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 11, 1),
            timeGroup = TimeGroup.DAY,
            artistNames = null,
            limit = 10,
            empties = false
        )
        val query = groupedArtistScrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select count(*), to_char(to_timestamp(time), 'YYYY-MM-DD'), scrobble.artist_name from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.time >= 1635660000 and scrobble.time < 1635746400 group by to_char(to_timestamp(time), 'YYYY-MM-DD'),scrobble.artist_name limit 10")
    }

    @Test
    fun `should build query from GroupedArtistScrobbleRequest with empty artist names`() {
        val groupedArtistScrobbleRequest = GroupedArtistScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 11, 1),
            timeGroup = TimeGroup.DAY,
            artistNames = emptyList(),
            limit = 10,
            empties = false
        )
        val query = groupedArtistScrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select count(*), to_char(to_timestamp(time), 'YYYY-MM-DD'), scrobble.artist_name from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.time >= 1635660000 and scrobble.time < 1635746400 group by to_char(to_timestamp(time), 'YYYY-MM-DD'),scrobble.artist_name limit 10")
    }

    @Test
    fun `should build query from GroupedScrobbleRequest`() {
        val groupedScrobbleRequest = GroupedScrobbleRequest(
            userName = "shicks255",
            from = LocalDate.of(2021, 10, 31),
            to = LocalDate.of(2021, 11, 1),
            timeGroup = TimeGroup.DAY
        )
        val query = groupedScrobbleRequest.buildQuery()
        assertThat(query)
            .isEqualTo("select count(*), to_char(to_timestamp(time), 'YYYY-MM-DD') from SCROBBLE where scrobble.user_name = 'shicks255' and scrobble.time >= 1635660000 and scrobble.time < 1635746400 group by to_char(to_timestamp(time), 'YYYY-MM-DD')")
    }
}
