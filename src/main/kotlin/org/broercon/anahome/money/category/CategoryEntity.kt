package org.broercon.anahome.money.category

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "category")
data class CategoryEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,
    // for example: Gehalt,

    @Enumerated(EnumType.STRING)
    val type: CategoryType
)

enum class CategoryType {
    INCOME, EXPENSE
}