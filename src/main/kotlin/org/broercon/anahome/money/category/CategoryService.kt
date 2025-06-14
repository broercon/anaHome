package org.broercon.anahome.money.category

import org.springframework.stereotype.Service

@Service
class CategoryService(private val repository: CategoryRepository) {
    fun findAll(): List<CategoryEntity> = repository.findAll()
    fun findById(id: Long): CategoryEntity? = repository.findById(id).orElse(null)
    fun save(category: CategoryEntity): CategoryEntity = repository.save(category)
    fun deleteById(id: Long) = repository.deleteById(id)
}