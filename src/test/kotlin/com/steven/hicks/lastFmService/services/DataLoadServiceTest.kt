package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.data.DataLoad
import com.steven.hicks.lastFmService.entities.data.DataLoadStatus
import com.steven.hicks.lastFmService.entities.data.LoadStatus
import com.steven.hicks.lastFmService.repositories.DataLoadRepository
import com.steven.hicks.lastFmService.repositories.LoadStatusRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import java.time.OffsetDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class DataLoadServiceTest {

    @Mock
    lateinit var dataLoadRepository: DataLoadRepository

    @Mock
    lateinit var loadStatusRepository: LoadStatusRepository

    @InjectMocks
    lateinit var sut: DataLoadService

    @Test
    fun `should create a data load event`() {
        `when`(dataLoadRepository.save(any()))
            .thenReturn(any())

        val result = sut.createDataLoad("shicks255")
        verify(dataLoadRepository, times(1)).save(any())
        assertThat(result.status).isEqualTo(DataLoadStatus.RUNNING)
        // assertThat(result.timestamp).isBefore(OffsetDateTime.now())
    }

    @Test
    fun `should save a data load event`() {
        `when`(dataLoadRepository.save(any()))
            .thenReturn(any())

        sut.saveDataLoad(DataLoad(OffsetDateTime.now(), "shicks255", DataLoadStatus.RUNNING, 0))
        verify(dataLoadRepository, times(1)).save(any())
    }

    @Test
    fun `should start data load status`() {

        val loadStatus = LoadStatus(
            userName = "shicks255",
            totalPages = 1,
            currentPage = 1,
            timestamp = OffsetDateTime.now()
        )

        `when`(loadStatusRepository.save(any()))
            .thenReturn(loadStatus)

        val result = sut.startDataLoadTracking("shicks255", 1)

        assertThat(result)
            .isEqualTo(loadStatus)
    }

    @Test
    fun `should update load status`() {
        val loadStatus = LoadStatus(
            userName = "shicks255",
            totalPages = 1,
            currentPage = 1,
            timestamp = OffsetDateTime.now()
        )

        `when`(loadStatusRepository.findById("shicks255"))
            .thenReturn(Optional.of(loadStatus))
        `when`(loadStatusRepository.save(loadStatus))
            .thenReturn(loadStatus)

        val result = sut.updateDataLoadStatus("shicks255", 1)

        assertThat(result)
            .isEqualTo(loadStatus)
        verify(loadStatusRepository, times(1))
            .save(loadStatus)
    }

    @Test
    fun `should get a user's load status`() {

        val loadStatus = LoadStatus(
            userName = "shicks255",
            totalPages = 4,
            currentPage = 3,
            timestamp = OffsetDateTime.now()
        )

        `when`(loadStatusRepository.findById("shicks255"))
            .thenReturn(Optional.of(loadStatus))

        val response = sut.getDataLoadStatus("shicks255")

        assertThat(response.currentPage)
            .isEqualTo(1)
        assertThat(response.totalPages)
            .isEqualTo(4)
        assertThat(response.message)
            .contains("25%")
        verify(loadStatusRepository, times(1))
            .findById("shicks255")
        verifyNoMoreInteractions(loadStatusRepository)
    }

    @Test
    fun `should get a user's load status replacing zero current page and percent done`() {

        val loadStatus = LoadStatus(
            userName = "shicks255",
            totalPages = 4,
            currentPage = 4,
            timestamp = OffsetDateTime.now()
        )

        `when`(loadStatusRepository.findById("shicks255"))
            .thenReturn(Optional.of(loadStatus))

        val response = sut.getDataLoadStatus("shicks255")

        assertThat(response.currentPage)
            .isEqualTo(1)
        assertThat(response.totalPages)
            .isEqualTo(4)
        assertThat(response.message)
            .contains("25%")
        verify(loadStatusRepository, times(1))
            .findById("shicks255")
        verifyNoMoreInteractions(loadStatusRepository)
    }

    @Test
    fun `should return empty response`() {
        val response = sut.getDataLoadStatus("shicks255")

        assertThat(response.currentPage)
            .isEqualTo(0)
        assertThat(response.totalPages)
            .isEqualTo(0)
        assertThat(response.message)
            .isEmpty()
        verify(loadStatusRepository, times(1))
            .findById("shicks255")
        verifyNoMoreInteractions(loadStatusRepository)
    }

    @Test
    fun `should end a data load status`() {
        `when`(loadStatusRepository.deleteById(anyString()))
            .then { }

        sut.endDataLoadStatus(anyString())

        verify(loadStatusRepository, times(1))
            .deleteById(anyString())
    }
}
