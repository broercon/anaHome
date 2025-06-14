package org.broercon.anahome.generall.person

import jakarta.persistence.*

@Entity
@Table(name = "person")
data class PersonEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,

    val isUser: Boolean = false
)