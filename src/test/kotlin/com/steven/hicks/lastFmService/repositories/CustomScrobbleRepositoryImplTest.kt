package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.GroupBy
import com.steven.hicks.lastFmService.controllers.dtos.SortBy
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import javax.persistence.EntityManager

@ExtendWith(MockitoExtension::class)
class CustomScrobbleRepositoryImplTest {

    @Mock
    lateinit var entityManager: EntityManager

    @InjectMocks
    lateinit var sut: CustomScrobbleRepositoryImpl

    @Test
    fun `should build grouped query`() {
        val request = GroupedScrobbleRequest(
            from = LocalDate.now(),
            to = LocalDate.now(),
            timeGroup = TimeGroup.DAY
        )

        val result = sut.buildGroupedQuery(request)

    }

    @Test
    fun `should build album grouped query`() {
        val request = GroupedAlbumScrobbleRequest(
            from = LocalDate.now(),
            to = LocalDate.now(),
            albumNames = listOf("Reign In Blood"),
            group = GroupBy.ARTIST,
            sort = SortBy.ALBUM,
            timeGroup = TimeGroup.DAY
        )

        val result = sut.buildAlbumGroupedQuery(request)
    }

    @Test
    fun `should build artist grouped query`() {
        val request = GroupedArtistScrobbleRequest(
            from = LocalDate.now(),
            to = LocalDate.now(),
            artistNames = listOf("Pink Floyd"),
            group = GroupBy.ARTIST,
            sort = SortBy.ALBUM,
            timeGroup = TimeGroup.DAY
        )

        val result = sut.buildArtistGroupedQuery(request)
    }

    @Test
    fun `should include time clause`() {
        val result = sut.includeTimeClause("", LocalDate.now(), LocalDate.now())

    }

    @Test
    fun `should build query`() {
        val request = ScrobbleRequest(
            artistName = "Pink Floyd",
            albumName = "Animals",
            from = LocalDate.now(),
            to = LocalDate.now(),
            limit = 200,
            sort = SortBy.ALBUM
        )

        val result = sut.buildQuery(request)
    }

}
