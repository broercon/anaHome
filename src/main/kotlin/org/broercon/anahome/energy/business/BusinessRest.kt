package org.broercon.anahome.energy.business

import java.time.LocalDateTime


data class BusinessRest (
    // Energy properties Meter values
    var nameMeterType: String = "",
    var businessMeterRest: MutableList<BusinessMeterRest> = mutableListOf()
)

data class BusinessMeterRest(
    var nameMeter: String = "",
    var location: String = "",
    var businessColumns : MutableList<BusinessColumnRest> = mutableListOf(),
)

data class BusinessColumnRest (
    var effectiveFrom: LocalDateTime = LocalDateTime.MIN,
    var effectiveTo: LocalDateTime? = null,

    var unit: String = "", // GP/AP

    // HT/NT with meterUnit
    // HT/NT with meterUnit
    // HT/NT with meterUnit
    var meterUnitLabel: String = "",

    // Value Energy
    var volume: Double = 0.0,

    // Value Tariff
    var nameTariff: String  = "",
    var price: Double = 0.0
)