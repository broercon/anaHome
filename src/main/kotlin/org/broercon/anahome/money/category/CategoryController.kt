package org.broercon.anahome.money.category

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
@RequestMapping("/api/money/category")
class CategoryController(
    private val categoryService: CategoryService
) {
    @GetMapping
    fun getAll(): List<CategoryRest> = categoryService.findAll().toRest()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<CategoryRest> =
        categoryService.findById(id)?.let { ResponseEntity.ok(it.toRest()) }
            ?: ResponseEntity.notFound().build()

    @PostMapping
    fun create(@RequestBody dto: CategoryRest): ResponseEntity<CategoryRest> {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(dto.toEntity()).toRest())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        categoryService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}