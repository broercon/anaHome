package org.broercon.anahome.money.purpose

import org.jetbrains.annotations.NotNull


data class PurposeRest(
    @field:NotNull
    val id: Long = 0,

    @field:NotNull
    val name: String,

    val description: String? = null
)


fun PurposeRest.toEntity() : PurposeEntity {
    return PurposeEntity(
        id = this.id,
        name = this.name,
        description = this.description
    )
}

fun PurposeEntity.toRest() : PurposeRest {
    return PurposeRest(
        id = this.id,
        name = this.name,
        description = this.description
    )
}

fun List<PurposeEntity>.toRest() : List<PurposeRest> = this.map { it.toRest() }


