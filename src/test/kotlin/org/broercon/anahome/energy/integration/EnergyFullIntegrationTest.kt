package org.broercon.anahome.energy.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.anaHome.org.broercon.anahome.energy.metertype.MeterTypeRest
import org.broercon.anahome.Application
import org.broercon.anahome.energy.consumptionentry.ConsumptionEntryRest
import org.broercon.anahome.energy.meter.MeterRest
import org.broercon.anahome.energy.meterUnit.MeterUnitRest
import org.broercon.anahome.energy.tariffplan.TariffPlanRest
import org.broercon.anahome.energy.tariffrate.TariffRateRest
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
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
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `fulltest__ Test of the application and create and configure`() {
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

        val typePreisPlan = TariffPlanRest(
            id = 1,
            name = "Preis MeterTyp",
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            meterTypeId = meterTypeId,
            meterId = null,
            meterUnitId = null
        )

        result = mockMvc.perform(post("/api/energy/tariffplan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(typePreisPlan)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Preis MeterTyp"))
            .andExpect(jsonPath("$.meterTypeId").value(meterTypeId))
            .andReturn()
        val planMeterTypId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()

        val PreisRateMeterType = TariffRateRest(
            id = 1,
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            unitPrice = 0.98.toBigDecimal(),
            tariffPlanId = planMeterTypId,
        )

        result = mockMvc.perform(post("/api/energy/tariffrate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(PreisRateMeterType)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.unitPrice").value(0.98.toBigDecimal()))
            .andExpect(jsonPath("$.tariffPlanId").value(planMeterTypId))
            .andReturn()

        val meterPreisPlan = TariffPlanRest(
            id = 1,
            name = "Preis Meter",
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            meterTypeId = null,
            meterId = meterId,
            meterUnitId = null
        )

        result = mockMvc.perform(post("/api/energy/tariffplan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(meterPreisPlan)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Preis Meter"))
            .andExpect(jsonPath("$.meterId").value(meterId))
            .andReturn()
        val planMeterId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()

        val PreisRateMeter = TariffRateRest(
            id = 1,
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            unitPrice = 0.98.toBigDecimal(),
            tariffPlanId = planMeterId,
        )

        result = mockMvc.perform(post("/api/energy/tariffrate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(PreisRateMeter)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.unitPrice").value(0.98.toBigDecimal()))
            .andExpect(jsonPath("$.tariffPlanId").value(planMeterId))
            .andReturn()

        val HtPreisPlan = TariffPlanRest(
            id = 1,
            name = "Preis HT",
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            meterTypeId = null,
            meterId = null,
            meterUnitId = unitHtId
        )

        result = mockMvc.perform(post("/api/energy/tariffplan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(HtPreisPlan)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Preis HT"))
            .andExpect(jsonPath("$.meterUnitId").value(unitHtId))
            .andReturn()
        val planHtId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()

        val htPreisRate = TariffRateRest(
            id = 1,
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            unitPrice = 0.98.toBigDecimal(),
            tariffPlanId = planHtId,
        )

        result = mockMvc.perform(post("/api/energy/tariffrate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(htPreisRate)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.unitPrice").value(0.98.toBigDecimal()))
            .andExpect(jsonPath("$.tariffPlanId").value(planHtId))
            .andReturn()

        val ntPreisPlan = TariffPlanRest(
            id = 1,
            name = "Preis NT",
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            meterTypeId = null,
            meterId = null,
            meterUnitId = unitNtId
        )

        result = mockMvc.perform(post("/api/energy/tariffplan")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ntPreisPlan)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Preis NT"))
            .andExpect(jsonPath("$.meterUnitId").value(unitNtId))
            .andReturn()
        val planNtId = objectMapper.readTree(result.response.contentAsString).get("id").asLong()

        val ntPreisRate = TariffRateRest(
            id = 1,
            effectiveFrom = LocalDateTime.of(2025, 1, 1, 0, 0),
            effectiveTo = LocalDateTime.of(2025, 1, 31, 0, 0),
            unitPrice = 0.98.toBigDecimal(),
            tariffPlanId = planNtId,
        )

        result = mockMvc.perform(post("/api/energy/tariffrate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(ntPreisRate)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.unitPrice").value(0.98.toBigDecimal()))
            .andExpect(jsonPath("$.tariffPlanId").value(planNtId))
            .andReturn()


    }


}