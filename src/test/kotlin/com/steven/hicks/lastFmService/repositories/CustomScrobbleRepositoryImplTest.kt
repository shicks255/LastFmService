package com.steven.hicks.lastFmService.repositories

import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.Table
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.ZoneOffset
import javax.persistence.EntityManager
import javax.persistence.Query

@ExtendWith(MockitoExtension::class)
class CustomScrobbleRepositoryImplTest {

    @Mock
    lateinit var entityManager: EntityManager

    @Mock
    lateinit var query: Query

    @InjectMocks
    lateinit var sut: CustomScrobbleRepositoryImpl

    @Test
    fun `should build grouped query`() {
        val today = LocalDate.now()
        val todayTimestamp =
            today.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(CustomScrobbleRepositoryImpl.UTC_OFFSET))

        val request = GroupedScrobbleRequest(
            userName = "shicks255",
            from = today,
            to = today,
            timeGroup = TimeGroup.DAY
        )

        val result = request.buildQuery()

        val selectConst =
            "select ${ScrobbleField.COUNT_STAR.field}, to_char(to_timestamp(time), 'YYYY-MM-DD') from ${Table.SCROBBLE}"
        val whereConst =
            "where ${ScrobbleField.USER_NAME.field} = 'shicks255' and ${ScrobbleField.TIME.field} >= $todayTimestamp and ${ScrobbleField.TIME.field} < $todayTimestamp"
        val groupByConst = "group by to_char(to_timestamp(time), 'YYYY-MM-DD')"

        assertThat(result)
            .contains(selectConst)
            .contains(whereConst)
            .contains(groupByConst)

        `when`(entityManager.createNativeQuery(result))
            .thenReturn(query)

        sut.getGroupedScrobbles(request)
    }

    @Test
    fun `should build album grouped query`() {
        val today = LocalDate.now()
        val todayTimestamp =
            today.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(CustomScrobbleRepositoryImpl.UTC_OFFSET))

        val request = GroupedAlbumScrobbleRequest(
            userName = "shicks255",
            from = today,
            to = today,
            albumNames = listOf("Reign In Blood"),
            artistNames = null,
            timeGroup = TimeGroup.DAY,
            limit = null
        )

        val result = request.buildQuery()

        val selectConst =
            "select ${ScrobbleField.COUNT_STAR.field}, to_char(to_timestamp(time), 'YYYY-MM-DD'), ${ScrobbleField.ALBUM_NAME.field}, ${ScrobbleField.ARTIST_NAME.field} from ${Table.SCROBBLE}"
        val whereConst =
            "where lower(${ScrobbleField.ALBUM_NAME.field}) <> '' and ${ScrobbleField.USER_NAME.field} = 'shicks255' and lower(${ScrobbleField.ALBUM_NAME.field}) in ('Reign In Blood') and ${ScrobbleField.TIME.field} >= $todayTimestamp and ${ScrobbleField.TIME.field} < $todayTimestamp"
        val groupByConst =
            "group by to_char(to_timestamp(time), 'YYYY-MM-DD'),${ScrobbleField.ALBUM_NAME.field},${ScrobbleField.ARTIST_NAME.field}"

        assertThat(result)
            .contains(selectConst)
            .contains(whereConst)
            .contains(groupByConst)

        `when`(entityManager.createNativeQuery(result))
            .thenReturn(query)

        sut.getAlbumGroupedScrobbles(request)
    }

    @Test
    fun `should build artist grouped query`() {
        val today = LocalDate.now()
        val todayTimestamp =
            today.atStartOfDay().toEpochSecond(ZoneOffset.ofHours(CustomScrobbleRepositoryImpl.UTC_OFFSET))

        val request = GroupedArtistScrobbleRequest(
            userName = "shicks255",
            from = today,
            to = today,
            artistNames = listOf("Pink Floyd"),
            timeGroup = TimeGroup.DAY,
            limit = 10
        )

        val result = request.buildQuery()

        val selectConst =
            "select ${ScrobbleField.COUNT_STAR.field}, to_char(to_timestamp(time), 'YYYY-MM-DD'), ${ScrobbleField.ARTIST_NAME.field} from ${Table.SCROBBLE}"
        val whereConst =
            "where ${ScrobbleField.USER_NAME.field} = 'shicks255' and lower(${ScrobbleField.ARTIST_NAME.field}) in ('Pink Floyd') and ${ScrobbleField.TIME.field} >= $todayTimestamp and ${ScrobbleField.TIME.field} < $todayTimestamp"
        val groupByConst =
            "group by to_char(to_timestamp(time), 'YYYY-MM-DD'),${ScrobbleField.ARTIST_NAME.field}"

        assertThat(result)
            .contains(selectConst)
            .contains(whereConst)
            .contains(groupByConst)

        `when`(entityManager.createNativeQuery(result))
            .thenReturn(query)

        sut.getArtistGroupedScrobbles(request)
    }
}
