package com.steven.hicks.lastFmService.integrationTests

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc

@ExtendWith(MockitoExtension::class)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DataJpaTest
class DataLoadingIntegrationTests {

//    @Test
    fun ttt() {
    }
}
