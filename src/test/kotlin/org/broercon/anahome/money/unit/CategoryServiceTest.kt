package org.broercon.anahome.money.unit

import org.broercon.anahome.money.category.CategoryEntity
import org.broercon.anahome.money.category.CategoryRepository
import org.broercon.anahome.money.category.CategoryService
import org.broercon.anahome.money.category.CategoryType
import org.broercon.anahome.money.vendor.VendorEntity
import org.broercon.anahome.money.vendor.VendorRepository
import org.broercon.anahome.money.vendor.VendorService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.context.annotation.Import
import java.util.Optional
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@Import(TestContainersConfig::class)
@ExtendWith(MockitoExtension::class)
class CategoryServiceTest {

    @InjectMocks
    private lateinit var categoryService: CategoryService

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    private lateinit var EntityOne: CategoryEntity
    private lateinit var EntityTwo: CategoryEntity

    @BeforeEach
    fun setup() {
        // Clean up database
        categoryRepository.deleteAll()
        EntityOne = CategoryEntity(id = 1, name = "Category Name 1", type = CategoryType.INCOME)
        EntityTwo = CategoryEntity(id = 2, name = "Category Name 2", type = CategoryType.INCOME)
    }

    @Test
    fun `should return all vendors`() {
        whenever(categoryRepository.findAll()).thenReturn(listOf(EntityOne, EntityTwo))

        val result = categoryService.findAll()

        assertEquals(2, result.size)
        assertEquals("Category Name 1", result[0].name)
        verify(categoryRepository).findAll()
    }

    @Test
    fun `should return vendor by ID`() {
        whenever(categoryRepository.findById(1L)).thenReturn(Optional.of(EntityOne))

        val result = categoryService.findById(1L)

        assertNotNull(result)
        assertEquals(EntityOne.name, result?.name)
        verify(categoryRepository).findById(1L)
    }

    @Test
    fun `should return null if vendor not found`() {
        whenever(categoryRepository.findById(999L)).thenReturn(Optional.empty())

        val result = categoryService.findById(999L)

        assertNull(result)
        verify(categoryRepository).findById(999L)
    }

    @Test
    fun `should save vendor`() {
        whenever(categoryRepository.save(EntityOne)).thenReturn(EntityOne)

        val result = categoryService.save(EntityOne)

        assertEquals(1, result.id)
        verify(categoryRepository).save(EntityOne)
    }

    @Test
    fun `should delete vendor by ID`() {
        doNothing().`when`(categoryRepository).deleteById(1L)

        categoryService.deleteById(1L)

        verify(categoryRepository).deleteById(1L)
    }
}