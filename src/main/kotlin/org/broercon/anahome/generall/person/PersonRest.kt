package org.broercon.anahome.generall.person

import org.jetbrains.annotations.NotNull

data class PersonRest(
    @field:NotNull
    val id: Long = 0,

    @field:NotNull
    val name: String,


    val isUser: Boolean? = false
)

fun PersonRest.toEntity(): PersonEntity {
    return PersonEntity(
        id = this.id,
        name = this.name,
        isUser = this.isUser ?: false
    )
}

fun PersonEntity.toRest() : PersonRest {
    return PersonRest(
        id = this.id,
        name = this.name,
        isUser = this.isUser
    )
}

fun List<PersonEntity>.toRest() :List<PersonRest> = this.map { it.toRest() }