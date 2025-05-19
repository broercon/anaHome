package org.broercon.anahome.energy

import com.fasterxml.jackson.databind.ObjectMapper
import org.anaHome.org.broercon.anahome.energy.metertype.MeterTypeRest
import org.broercon.anahome.Application
import org.broercon.anahome.energy.consumptionentry.ConsumptionEntryRest
import org.broercon.anahome.energy.meter.MeterRest
import org.broercon.anahome.energy.meterUnit.MeterUnitRest
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import kotlin.test.Test

@Import(TestContainersConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EnergyFullIntegrationTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `create meter type and meter then read it back`() {
        // Create Meter Type:
        val meterType = MeterTypeRest(id = 0, name = "Strom")
        var result: MvcResult = mockMvc.perform(
            post("/api/energy/metertype")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterType))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Strom"))
            .andReturn()

        val meterTypeId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()

        val meter = MeterRest(id = 0, name = "Stromzähler Keller", meterNumber = "ZN2205", location = "Keller", meterTypeEntityId = meterTypeId)

        result = mockMvc.perform(
            post("/api/energy/meter")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meter))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value("Stromzähler Keller"))
            .andReturn()

        val meterId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()
        val meterUnitHT = MeterUnitRest(id = 0, label = "HT", unit = "kWh", meterEntityId = meterId)

        result = mockMvc.perform(
            post("/api/energy/meterunit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterUnitHT))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.unit").value("kWh"))
            .andReturn()

        val unitHtId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()

        val consumptionHtStart = ConsumptionEntryRest(id = 0, timestamp = LocalDateTime.of(2025, 1, 1, 0, 0), meterReading = 200.0, comment = "Startwert", meterUnitEntityId = unitHtId)
        val consumptionHTEnde = ConsumptionEntryRest(id = 0, timestamp = LocalDateTime.of(2025, 12, 31, 0, 0), meterReading = 2000.0, comment = "EndWert", meterUnitEntityId = unitHtId)

        mockMvc.perform(post("/api/energy/consumptionentry")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(consumptionHtStart)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.meterReading").value(200.00))

        mockMvc.perform(post("/api/energy/consumptionentry")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(consumptionHTEnde)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.meterReading").value(2000.00))

        val meterUnitNT = MeterUnitRest(id = 0, label = "NT", unit = "kWh", meterEntityId = meterId)
        result = mockMvc.perform(
            post("/api/energy/meterunit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(meterUnitNT))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.unit").value("kWh"))
            .andReturn()

        val unitNtId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()

        val consumptionNTStart = ConsumptionEntryRest(id = 0, timestamp = LocalDateTime.of(2025, 1, 1, 0, 0), meterReading = 300.0, comment = "Startwert", meterUnitEntityId = unitNtId)
        val consumptionNTEnde = ConsumptionEntryRest(id = 0, timestamp = LocalDateTime.of(2025, 12, 31, 0, 0), meterReading = 3000.0, comment = "EndWert", meterUnitEntityId = unitNtId)

        mockMvc.perform(post("/api/energy/consumptionentry")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(consumptionNTStart)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.meterReading").value(300.00))

        mockMvc.perform(post("/api/energy/consumptionentry")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(consumptionNTEnde)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.meterReading").value(3000.00))


    }
}