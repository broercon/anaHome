package org.broercon.anahome.money.vendor

import org.jetbrains.annotations.NotNull


data class VendorRest(
    @field:NotNull
    val id: Long,

    @field:NotNull
    val name: String,

    val type: String? = null,
)

fun VendorRest.toEntity() : VendorEntity {
   return VendorEntity(
       id = this.id,
       name = this.name,
       type = this.type
   )
}

fun VendorEntity.toRest() : VendorRest {
    return VendorRest(
        id = this.id,
        name = this.name,
        type = this.type
    )
}

fun List<VendorEntity>.toRest() : List<VendorRest> = this.map { it.toRest() }