package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.repositories.DataLoadRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class DataLoadServiceTest {

    @Mock
    lateinit var dataLoadRepository: DataLoadRepository

    @Mock
    lateinit var lastfmLoadService: LastFmLoadingService

    @InjectMocks
    lateinit var sut: DataLoadService

    @Test
    fun `should finish normally`() {
        val event = DataLoad(
                OffsetDateTime.now(),
                DataLoadStatus.RUNNING,
                0
        )

        `when`(dataLoadRepository.save(any()))
                .thenReturn(event)
        `when`(lastfmLoadService.loadRecent())
                .thenReturn(0)

        sut.performDataLoad()

        verify(dataLoadRepository, times(2)).save(any())
    }

    @Test
    fun `should save DataLoad entity if error thrown`() {
        val date = LocalDate.now()
        val event = DataLoad(
                OffsetDateTime.now(),
                DataLoadStatus.RUNNING,
                0
        )

        `when`(dataLoadRepository.save(any()))
                .thenReturn(event)
        `when`(lastfmLoadService.loadRecent())
                .then { throw LastFmException("") }

        sut.performDataLoad()

        verify(dataLoadRepository, times(2)).save(any())
    }
}