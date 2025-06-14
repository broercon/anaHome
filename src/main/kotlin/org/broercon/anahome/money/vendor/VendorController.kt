package org.broercon.anahome.money.vendor

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/money/vendor")
class VendorController(
    private val vendorService: VendorService
) {
    @GetMapping
    fun getAll(): List<VendorRest> = vendorService.findAll().toRest()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<VendorRest> =
        vendorService.findById(id)?.let { ResponseEntity.ok(it.toRest()) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody dto: VendorRest): ResponseEntity<VendorRest> {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendorService.save(dto.toEntity()).toRest())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        vendorService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}