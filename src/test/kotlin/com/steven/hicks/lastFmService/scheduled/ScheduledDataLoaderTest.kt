package com.steven.hicks.lastFmService.scheduled

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.services.DataLoadService
import com.steven.hicks.lastFmService.services.LastFmLoadingService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import java.time.OffsetDateTime

@ExtendWith(MockitoExtension::class)
class ScheduledDataLoaderTest {

    @Mock
    lateinit var dataLoadService: DataLoadService
    @Mock
    lateinit var lastFmLoadingService: LastFmLoadingService

    @InjectMocks
    lateinit var sut: ScheduledDataLoader

    @Test
    fun `should load data`() {
        `when`(dataLoadService.createDataLoad())
            .thenReturn(DataLoad(
                timestamp = OffsetDateTime.now(),
                status = DataLoadStatus.RUNNING,
                count = 1,
                error = null
            ))
        `when`(lastFmLoadingService.loadRecent("shicks255"))
            .thenReturn(1)

        sut.loadDay()

        verify(dataLoadService, times(1)).createDataLoad()
        verify(lastFmLoadingService, times(1)).loadRecent("shicks255")
        verify(dataLoadService, times(1)).saveDataLoad(any())
    }
}
