package com.steven.hicks.lastFmService

import com.steven.hicks.lastFmService.entities.LastFmException
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.repositories.DataLoadRepository
import com.steven.hicks.lastFmService.services.DataLoadService
import com.steven.hicks.lastFmService.services.LastFmLoadingService
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
                LocalDate.now(),
                OffsetDateTime.now(),
                DataLoadStatus.RUNNING,
                0
        )

        `when`(dataLoadRepository.save(any()))
                .thenReturn(event)
        `when`(lastfmLoadService.loadDay(LocalDate.now().minusDays(1)))
                .thenReturn(0)

        sut.performDataLoad()

        verify(dataLoadRepository, times(2)).save(any())
    }

    @Test
    fun `dsf`() {
        val date = LocalDate.now()
        val event = DataLoad(
                date,
                OffsetDateTime.now(),
                DataLoadStatus.RUNNING,
                0
        )

        `when`(dataLoadRepository.save(any()))
                .thenReturn(event)
        `when`(lastfmLoadService.loadDay(date))
                .then { throw LastFmException("") }

        sut.performDataLoad(date)

        verify(dataLoadRepository, times(2)).save(any())
    }
}