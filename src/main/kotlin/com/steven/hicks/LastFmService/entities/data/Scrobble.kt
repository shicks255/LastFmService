package com.steven.hicks.LastFmService.entities.data

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Scrobble(

    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int,
    val artistMbid: String,
    val artistName: String,
    val albumMbid: String,
    val albumName: String,
    val time: String


)
