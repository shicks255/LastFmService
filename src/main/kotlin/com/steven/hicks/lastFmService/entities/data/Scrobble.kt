package com.steven.hicks.lastFmService.entities.data

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    indexes =
    [
        Index(name = "idx_scrobble_unique", columnList = "userName, name, artistName, time", unique = true),
        Index(name = "idx_user_artist", columnList = "userName, artistName"),
        Index(name = "idx_user_artist_album", columnList = "userName, artistName, albumName")
    ]
)
data class Scrobble(

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int?,
    @Column(nullable = false)
    val userName: String,
    @Column(columnDefinition = "text", nullable = false)
    val name: String,
    val artistMbid: String,
    @Column(nullable = false)
    val artistName: String,
    val albumMbid: String,
    @Column(nullable = false)
    val albumName: String,
    @Column(nullable = false)
    val time: Long
) {

    override fun toString(): String {
        return """
            $time:$name - $artistName - $albumName
        """.trimIndent()
    }
}
