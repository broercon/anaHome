package org.anaHome.org.broercon.anahome.energy.metertype


data class RestMeterType(
    val id: Long,

    // Zum Beispiel Strom/Gas/Wasser
    val name: String
)


// Extension function (Receiver function)
fun RestMeterType.toDomain(): MeterTypeEntity = MeterTypeEntity(
    id = this.id,    // map id from RestMeterType
    name = this.name // map name from RestMeterType
)

fun MeterTypeEntity?.toRest(): RestMeterType? = RestMeterType(
    id = this?.id ?: 0,    // map id from RestMeterType
    name = this?.name ?: "Unkown" // map name from RestMeterType
)

fun List<MeterTypeEntity?>.toRest(): List<RestMeterType?> = this.map { it.toRest() }