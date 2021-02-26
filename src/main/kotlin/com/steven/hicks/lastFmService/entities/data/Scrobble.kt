package com.steven.hicks.lastFmService.entities.data

import javax.persistence.*

@Entity
@Table(indexes =
[Index(name = "idx_scrobble_unique", columnList = "name, artistName, time", unique = true),
Index(name = "idx_artist_name", columnList = "artistName"),
Index(name = "idx_artist_name_album_name", columnList = "artistName, albumName")]
)
data class Scrobble(

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int?,
    @Column(columnDefinition = "text")
    val name: String,
    val artistMbid: String,
    val artistName: String,
    val albumMbid: String,
    val albumName: String,
    val time: Long
)
