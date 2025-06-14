package org.broercon.anahome.money.transaction

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.broercon.anahome.generall.person.PersonEntity
import org.broercon.anahome.money.category.CategoryEntity
import org.broercon.anahome.money.purpose.PurposeEntity
import org.broercon.anahome.money.vendor.VendorEntity
import java.time.LocalDate

@Entity
@Table(name = "transaction")
data class TransactionEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val amount: Double,

    val description: String?,

    val visible: Boolean = true,

    val dueDate: LocalDate?,

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    val createdBy: PersonEntity,

    @ManyToOne
    @JoinColumn(name = "payer_id")
    val payer: PersonEntity?,

    @ManyToOne
    @JoinColumn(name = "for_whom_id")
    val forWhom: PersonEntity?,

    @ManyToOne
    @JoinColumn(name = "category_id")
    val category: CategoryEntity,

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    val vendor: VendorEntity?,

    @ManyToOne
    @JoinColumn(name = "purpose_id")
    val purpose: PurposeEntity
)