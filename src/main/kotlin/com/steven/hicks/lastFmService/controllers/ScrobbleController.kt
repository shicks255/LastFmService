package com.steven.hicks.lastFmService.controllers

import com.steven.hicks.lastFmService.aspects.Logged
import com.steven.hicks.lastFmService.controllers.dtos.TimeGroup
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedAlbumScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedArtistScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.GroupedScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRequest
import com.steven.hicks.lastFmService.controllers.dtos.request.ScrobbleRunningTotalRequest
import com.steven.hicks.lastFmService.controllers.dtos.response.ArtistStats
import com.steven.hicks.lastFmService.controllers.dtos.response.DataByDay
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByAlbum
import com.steven.hicks.lastFmService.controllers.dtos.response.GroupedResponseByArtist
import com.steven.hicks.lastFmService.controllers.dtos.response.RunningTotalResponse
import com.steven.hicks.lastFmService.entities.ScrobbleField
import com.steven.hicks.lastFmService.entities.data.Scrobble
import com.steven.hicks.lastFmService.entities.queryBuilding.Direction
import com.steven.hicks.lastFmService.services.ScrobbleService
import com.steven.hicks.lastFmService.services.StatsService
import io.micrometer.core.annotation.Timed
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.constraints.Max

@RestController
@RequestMapping("/api/v1/scrobbles")
@Suppress("MagicNumber")
class ScrobbleController(
    val scrobbleService: ScrobbleService,
    val statsService: StatsService
) {

    companion object {
        const val USERNAME_DESCRIPTION = "Username to fetch data for"
    }

    @GetMapping
    @Logged
    @Timed
    @Operation(description = "Get a sorted list of scrobbles for a specific user")
    fun getScrobbles(
        @Parameter(description = USERNAME_DESCRIPTION, required = true, example = "shicks255")
        @RequestParam(required = true)
        userName: String,
        @Parameter(description = "Optional artist of scrobbles to fetch")
        @RequestParam
        artistName: String?,
        @Parameter(description = "Optional album of scrobbles to fetch")
        @RequestParam
        albumName: String?,
        @Parameter(description = "Optional start date of scrobbles to fetch", example = "2022-01-01")
        @RequestParam
        from: String?,
        @Parameter(description = "Optional end date of scrobbles to fetch", example = "2022-01-15")
        @RequestParam
        to: String?,
        @Parameter(description = "Limit of scrobbles to return (max 200)")
        @RequestParam(required = true, defaultValue = "200")
        @Max(200)
        limit: Int?,
        @Parameter(description = "Optional field to sort results by")
        @RequestParam
        sort: ScrobbleField?,
        @Parameter(description = "Optional direction when sorting results ")
        @RequestParam
        direction: Direction?
    ): List<Scrobble> {
        val request = ScrobbleRequest(
            userName = userName,
            artistName = artistName?.toLowerCase(),
            albumName = albumName?.toLowerCase(),
            from = if (from != null) LocalDate.parse(from) else null,
            to = if (to != null) LocalDate.parse(to).coerceAtMost(LocalDate.now()) else null,
            limit = limit,
            sort = sort,
            direction
        )
        return scrobbleService
            .getTracks(request)
    }

    @GetMapping("/grouped")
    @Logged
    @Timed
    fun getScrobblesGrouped(
        @RequestParam userName: String,
        @RequestParam from: String?,
        @RequestParam to: String?,
        @RequestParam timeGroup: TimeGroup
    ): List<DataByDay> {
        val request = GroupedScrobbleRequest(
            userName = userName,
            from = if (from != null) LocalDate.parse(from) else null,
            to = if (to != null) LocalDate.parse(to).coerceAtMost(LocalDate.now()) else null,
            timeGroup = timeGroup
        )

        return scrobbleService.getTracksGrouped(request)
    }

    @GetMapping("/artistsGrouped")
    @Logged
    @Timed
    fun getArtistScrobblesGrouped(
        @Parameter(description = USERNAME_DESCRIPTION, required = true, example = "shicks255")
        @RequestParam
        userName: String,
        @Parameter(description = "Start date", required = true, example = "2022-01-01")
        @RequestParam(required = true)
        from: String?,
        @Parameter(description = "End date", example = "2022-01-15")
        @RequestParam
        to: String?,
        @Parameter(description = "Only return albums from these values")
        @RequestParam
        artistNames: List<String>?,
        @Parameter(description = "Time frame to group the results")
        @RequestParam
        timeGroup: TimeGroup,
        @Parameter(description = "Limit the number of artists returned (max 200)")
        @RequestParam(required = true, defaultValue = "200")
        @Max(200)
        limit: Int?,
        @Parameter(description = "Fill in empty timeFrames with 0 values")
        @RequestParam
        empties: Boolean? = false
    ): GroupedResponseByArtist {
        val request = GroupedArtistScrobbleRequest(
            userName = userName,
            from = LocalDate.parse(from),
            to = if (to != null) LocalDate.parse(to).coerceAtMost(LocalDate.now()) else LocalDate.now(),
            artistNames = artistNames?.map { it.toLowerCase() },
            timeGroup = timeGroup,
            limit = limit,
            empties = empties
        )

        return scrobbleService.getArtistTracksGrouped(request)
    }

    @GetMapping("/albumsGrouped")
    @Logged
    @Timed
    @Operation(description = "Get list of scrobbles grouped by album")
    fun getAlbumScrobblesGrouped(
        @Parameter(description = USERNAME_DESCRIPTION, required = true, example = "shicks255")
        @RequestParam
        userName: String,
        @Parameter(description = "Start date", required = true, example = "2022-01-01")
        @RequestParam(required = true)
        from: String?,
        @Parameter(description = "End date", example = "2022-01-15")
        @RequestParam
        to: String?,
        @Parameter(description = "Only return albums from these values")
        @RequestParam
        albumNames: List<String>?,
        @Parameter(description = "Only return albums from these artists")
        @RequestParam
        artistNames: List<String>?,
        @Parameter(description = "Time frame to group the results")
        @RequestParam
        timeGroup: TimeGroup,
        @Parameter(description = "Limit the number of albums returned (max 200)")
        @RequestParam(required = true, defaultValue = "200")
        @Max(200)
        limit: Int?,
        @Parameter(description = "Fill in empty timeFrames with 0 values")
        @RequestParam
        empties: Boolean? = false
    ): GroupedResponseByAlbum {
        val request = GroupedAlbumScrobbleRequest(
            userName = userName,
            from = LocalDate.parse(from),
            to = if (to != null) LocalDate.parse(to).coerceAtMost(LocalDate.now()) else LocalDate.now(),
            albumNames = albumNames?.map { it.toLowerCase() },
            artistNames = artistNames?.map { it.toLowerCase() },
            timeGroup = timeGroup,
            limit = limit,
            empties = empties
        )
        return scrobbleService.getAlbumTracksGrouped(request)
    }

    @GetMapping("/runningTotals")
    @Logged
    @Timed
    fun getScrobbleRunningTotals(
        @Parameter(description = USERNAME_DESCRIPTION, required = true, example = "shicks255")
        @RequestParam
        userName: String,
        @Parameter(description = "Optional start date of scrobbles to fetch", example = "2022-01-01")
        @RequestParam
        from: String?,
        @Parameter(description = "Optional end date of scrobbles to fetch", example = "2022-01-15")
        @RequestParam
        to: String?,
        @RequestParam
        timeGroup: TimeGroup,
    ): RunningTotalResponse {
        val fromm = if (from != null) LocalDate.parse(from) else LocalDate.of(2000, 1, 1)
        val too = if (to != null) LocalDate.parse(to).coerceAtMost(LocalDate.now()) else LocalDate.now()
        val request = ScrobbleRunningTotalRequest(userName.toLowerCase(), fromm, too, timeGroup)

        return scrobbleService.getScrobbleRunningTotals(request)
    }

    @GetMapping("/artistStats")
    @Logged
    @Timed
    fun getArtistStats(
        @Parameter(description = "Artist name to get stats for", required = true, example = "Pink Floyd")
        @RequestParam
        artistName: String,
        @Parameter(description = USERNAME_DESCRIPTION, required = true, example = "shicks255")
        @RequestParam
        userName: String
    ): ArtistStats {
        return statsService.getArtistStats(userName.toLowerCase(), artistName.toLowerCase())
    }
}
