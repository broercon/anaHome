package org.anaHome.org.broercon.anahome.energy.metertype

import org.broercon.anahome.energy.metertype.MeterTypeEntity


data class MeterTypeRest(
    val id: Long,

    // Zum Beispiel Strom/Gas/Wasser
    val name: String
)


// Extension function (Receiver function)
fun MeterTypeRest.toDomain(): MeterTypeEntity = MeterTypeEntity(
    id = this.id,    // map id from RestMeterType
    name = this.name // map name from RestMeterType
)

fun MeterTypeEntity?.toRest(): MeterTypeRest? = MeterTypeRest(
    id = this?.id ?: 0,    // map id from RestMeterType
    name = this?.name ?: "Unkown" // map name from RestMeterType
)

fun List<MeterTypeEntity?>.toRest(): List<MeterTypeRest?> = this.map { it.toRest() }