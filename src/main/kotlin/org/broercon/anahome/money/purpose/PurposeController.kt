package org.broercon.anahome.money.purpose

import org.broercon.anahome.money.category.CategoryRest
import org.broercon.anahome.money.category.CategoryService
import org.broercon.anahome.money.category.toEntity
import org.broercon.anahome.money.category.toRest

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
@RequestMapping("/api/money/purpose")
class PurposeController(
    private val purposeService: PurposeService
) {
    @GetMapping
    fun getAll(): List<PurposeRest> = purposeService.findAll().toRest()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<PurposeRest> =
        purposeService.findById(id)?.let { ResponseEntity.ok(it.toRest()) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody dto: PurposeRest): ResponseEntity<PurposeRest> {
        return ResponseEntity.status(HttpStatus.CREATED).body(purposeService.save(dto.toEntity()).toRest())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        purposeService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}