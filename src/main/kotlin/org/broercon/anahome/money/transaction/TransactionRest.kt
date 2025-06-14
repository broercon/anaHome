package org.broercon.anahome.money.transaction

import org.broercon.anahome.generall.person.PersonService
import org.broercon.anahome.money.category.CategoryService
import org.broercon.anahome.money.purpose.PurposeService
import org.broercon.anahome.money.vendor.VendorService
import org.springframework.stereotype.Component
import java.time.LocalDate

data class TransactionRest(
    val id: Long = 0,
    val amount: Double,
    val description: String?,
    val visible: Boolean = true,
    val dueDate: LocalDate?,

    val createdById: Long,
    val payerId: Long?,
    val forWhomId: Long?,
    val categoryId: Long,
    val vendorId: Long?,
    val purposeId: Long
)

@Component
class TransactionMapper(val personService: PersonService,
                              val categoryService: CategoryService,
                              val vendorService: VendorService,
                              val purposeService: PurposeService) {
    fun TransactionEntity.toRest(): TransactionRest {
        return TransactionRest(
            id = this.id,
            amount = this.amount,
            description = this.description,
            visible = this.visible,
            dueDate = this.dueDate,

            createdById = this.createdBy.id,
            payerId = this.payer?.id ?: 0,
            forWhomId = this.forWhom?.id ?: 0,
            categoryId = this.category.id,
            vendorId = this.vendor?.id ?: 0,
            purposeId = this.purpose.id,
        )
    }

    fun TransactionRest.toEntity(): TransactionEntity {
        personService.findById(this.createdById) ?: throw IllegalArgumentException("Created User must exists.")
        categoryService.findById(this.categoryId) ?: throw IllegalArgumentException("Category must exists.")
        purposeService.findById(this.purposeId) ?: throw IllegalArgumentException("Purpose must exists.")

        return TransactionEntity(
            id = this.id,
            amount = this.amount,
            description = this.description,
            visible = this.visible,
            dueDate = this.dueDate,
            createdBy = personService.findById(this.createdById)!!,
            payer = personService.findById(this.payerId?:0),
            forWhom = personService.findById(this.forWhomId?:0),
            category = categoryService.findById(this.categoryId)!!,
            vendor = vendorService.findById(this.vendorId?:0),
            purpose = purposeService.findById(this.purposeId)!!
        )
    }

    fun List<TransactionEntity>.toRest() :List<TransactionRest> = this.map { it.toRest() }
}

