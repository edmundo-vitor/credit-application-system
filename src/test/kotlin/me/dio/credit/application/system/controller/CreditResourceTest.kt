package me.dio.credit.application.system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit.application.system.dto.request.CreditDto
import me.dio.credit.application.system.entity.Customer
import me.dio.credit.application.system.repository.CreditRepository
import me.dio.credit.application.system.repository.CustomerRepository
import me.dio.credit.application.system.service.CustomerServiceTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
  @Autowired
  private lateinit var creditRepository: CreditRepository

  @Autowired
  private lateinit var customerRepository: CustomerRepository

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var objectMapper: ObjectMapper

  companion object {
    const val URL: String = "/api/credits"
  }

  private lateinit var customerEntity: Customer

  @BeforeEach
  fun setup() {
    creditRepository.deleteAll()
    customerRepository.deleteAll()
    customerEntity = customerRepository.save(CustomerServiceTest.buildCustomer())
  }

  @AfterEach
  fun tearDown() {
    creditRepository.deleteAll()
    customerRepository.deleteAll()
  }

  @Test
  fun `should save a credit and return 201 status`() {
    //given
    val creditDto: CreditDto = buildCredit()
    val valueAsString: String = objectMapper.writeValueAsString(creditDto)
    //when
    //then
    mockMvc.perform(
      MockMvcRequestBuilders.post(URL)
        .contentType(MediaType.APPLICATION_JSON)
        .content(valueAsString)
    )
      .andExpect(MockMvcResultMatchers.status().isCreated)
      .andExpect(MockMvcResultMatchers.jsonPath("$").isString)
      .andDo(MockMvcResultHandlers.print())
  }

  private fun buildCredit(
          creditValue: BigDecimal = BigDecimal.valueOf(100.0),
          dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
          numberOfInstallments: Int = 15,
          customerId: Long? = customerEntity.id
  ): CreditDto = CreditDto(
          creditValue = creditValue,
          dayFirstOfInstallment = dayFirstInstallment,
          numberOfInstallments = numberOfInstallments,
          customerId = customerId ?: 0,
  )
}