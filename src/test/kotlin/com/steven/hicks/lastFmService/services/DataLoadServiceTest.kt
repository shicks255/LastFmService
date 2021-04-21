package com.steven.hicks.lastFmService.services

import com.steven.hicks.lastFmService.entities.data.LoadStatus
import com.steven.hicks.lastFmService.repositories.DataLoadRepository
import com.steven.hicks.lastFmService.repositories.LoadStatusRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class DataLoadServiceTest {

    @Mock
    lateinit var dataLoadRepository: DataLoadRepository

    @Mock
    lateinit var loadStatusRepository: LoadStatusRepository

    @InjectMocks
    lateinit var sut: DataLoadService

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
}
