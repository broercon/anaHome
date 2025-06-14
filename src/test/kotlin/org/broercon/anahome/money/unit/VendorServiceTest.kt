package org.broercon.anahome.money.unit

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
class VendorServiceTest {

    @InjectMocks
    private lateinit var vendorService: VendorService

    @Mock
    private lateinit var vendorRepository: VendorRepository

    @BeforeEach
    fun setup() {
        // Clean up database
        vendorRepository.deleteAll()
    }

    @Test
    fun `should return all vendors`() {
        val vendors: List<VendorEntity> = listOf(VendorEntity(id = 1, name = "Vendor A", type = "Retail"))
        whenever(vendorRepository.findAll()).thenReturn(vendors)

        val result = vendorService.findAll()

        assertEquals(1, result.size)
        assertEquals("Vendor A", result[0].name)
        verify(vendorRepository).findAll()
    }

    @Test
    fun `should return vendor by ID`() {
        val vendor = VendorEntity(name = "Vendor B", type = "Wholesale")
        whenever(vendorRepository.findById(2L)).thenReturn(Optional.of(vendor))

        val result = vendorService.findById(2L)

        assertNotNull(result)
        assertEquals("Vendor B", result?.name)
        verify(vendorRepository).findById(2L)
    }

    @Test
    fun `should return null if vendor not found`() {
        whenever(vendorRepository.findById(999L)).thenReturn(Optional.empty())

        val result = vendorService.findById(999L)

        assertNull(result)
        verify(vendorRepository).findById(999L)
    }

    @Test
    fun `should save vendor`() {
        val vendor = VendorEntity(id = 1, name = "New Vendor", type = "Retail")
        whenever(vendorRepository.save(vendor)).thenReturn(vendor)

        val result = vendorService.save(vendor)

        assertEquals(1, result.id)
        verify(vendorRepository).save(vendor)
    }

    @Test
    fun `should delete vendor by ID`() {
        doNothing().`when`(vendorRepository).deleteById(1L)

        vendorService.deleteById(1L)

        verify(vendorRepository).deleteById(1L)
    }


}