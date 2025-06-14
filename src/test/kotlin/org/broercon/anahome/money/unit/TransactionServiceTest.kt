package org.broercon.anahome.money.unit

import org.broercon.anahome.generall.person.PersonEntity
import org.broercon.anahome.money.category.CategoryEntity
import org.broercon.anahome.money.category.CategoryType
import org.broercon.anahome.money.purpose.PurposeEntity
import org.broercon.anahome.money.transaction.TransactionEntity
import org.broercon.anahome.money.transaction.TransactionRepository
import org.broercon.anahome.money.transaction.TransactionService
import org.broercon.anahome.money.vendor.VendorEntity
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.annotation.Import
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Import(TestContainersConfig::class)
@ExtendWith(MockitoExtension::class)
class TransactionServiceTest {

    @InjectMocks
    private lateinit var transactionService: TransactionService

    @Mock
    private lateinit var transactionRepository: TransactionRepository

    private lateinit var EntityOne: TransactionEntity
    private lateinit var EntityTwo: TransactionEntity
    private lateinit var personEntityOne: PersonEntity
    private lateinit var personEntityTwo: PersonEntity
    private lateinit var vendorEntity: VendorEntity
    private lateinit var purposeEntity: PurposeEntity
    private lateinit var categoryEntity: CategoryEntity

    @BeforeEach
    fun setup() {
        // Clean up database
        transactionRepository.deleteAll()
        personEntityOne = PersonEntity(id = 1, name = "tTestUser", isUser = true)
        personEntityTwo = PersonEntity(id = 2, name = "tTestUser 2", isUser = true)

        categoryEntity = CategoryEntity(id = 1, name = "Category Entity", type = CategoryType.INCOME)
        vendorEntity = VendorEntity(id = 1, name = "Vendor Entity Name", type = "Ventor Entity Type")
        purposeEntity = PurposeEntity(id = 1, name = "Purpose Entity Name", description = "Purpose Entitiy Description")
        EntityOne = TransactionEntity(id = 1L, amount = 32.3, description = "Description 1 ", visible = true, dueDate = LocalDate.now(), createdBy = personEntityOne, payer = personEntityTwo, forWhom = personEntityTwo, category = categoryEntity, vendor = vendorEntity, purpose = purposeEntity)
        EntityTwo = TransactionEntity(id = 2L, amount = 33.3, description = "Description 2 ", visible = true, dueDate = LocalDate.now(), createdBy = personEntityOne, payer = personEntityTwo, forWhom = personEntityTwo, category = categoryEntity, vendor = vendorEntity, purpose = purposeEntity)
    }

    @Test
    fun `should return all transaction`() {
        whenever(transactionRepository.findAll()).thenReturn(listOf(EntityOne, EntityTwo))

        val result = transactionService.findAll()

        assertEquals(2, result.size)
        assertEquals("Description 1 ", result[0].description)
        verify(transactionRepository).findAll()
    }

    @Test
    fun `should return transaction by ID`() {
        whenever(transactionRepository.findById(1L)).thenReturn(Optional.of(EntityOne))

        val result = transactionService.findById(1L)

        assertNotNull(result)
        assertEquals(EntityOne.description, result?.description)
        verify(transactionRepository).findById(1L)
    }

    @Test
    fun `should return null if transaction not found`() {
        whenever(transactionRepository.findById(999L)).thenReturn(Optional.empty())

        val result = transactionService.findById(999L)

        assertNull(result)
        verify(transactionRepository).findById(999L)
    }

    @Test
    fun `should save transaction`() {
        whenever(transactionRepository.save(EntityOne)).thenReturn(EntityOne)

        val result = transactionService.save(EntityOne)

        assertEquals(1, result.id)
        verify(transactionRepository).save(EntityOne)
    }

    @Test
    fun `should delete transaction by ID`() {
        doNothing().`when`(transactionRepository).deleteById(1L)

        transactionService.deleteById(1L)

        verify(transactionRepository).deleteById(1L)
    }
}