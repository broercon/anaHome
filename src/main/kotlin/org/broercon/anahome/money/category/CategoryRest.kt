package org.broercon.anahome.money.category

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.constraints.NotNull
import kotlin.reflect.KClass

data class CategoryRest (
    @field:NotNull
    val id: Long = 0,

    @field:NotNull
    val name: String,
    // for example: Gehalt,

    @field:NotNull
    val type: CategoryType
    // Einnahmen/Ausgaben bzw. EXPENSE/INCOME
)


fun CategoryRest.toEntity() : CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        type = this.type
    )
}

fun CategoryEntity.toRest() : CategoryRest {
    return CategoryRest(
        id = this.id,
        name = this.name,
        type = this.type
    )
}

fun List<CategoryEntity>.toRest() : List<CategoryRest> = this.map { it.toRest() }


// Custom annotation definition
@Constraint(validatedBy = [CategoryTypeValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CategoryTypeValidation(
    val message: String = "Value must be 'INCOME', EXPENSE, EINNAHMEN or 'AUSGABEN'",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

// Validator logic
class CategoryTypeValidator : ConstraintValidator<CategoryTypeValidation, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return value == "INCOME" || value == "EXPENSE" || value == "EINNAHMEN" || value == "AUSGABEN"
    }
}