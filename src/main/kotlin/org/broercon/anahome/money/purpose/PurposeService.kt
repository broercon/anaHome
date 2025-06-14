package org.broercon.anahome.money.purpose

import org.springframework.stereotype.Service

@Service
class PurposeService(private val repository: PurposeRepository) {
    fun findAll(): List<PurposeEntity> = repository.findAll()
    fun findById(id: Long): PurposeEntity? = repository.findById(id).orElse(null)
    fun save(vendor: PurposeEntity): PurposeEntity = repository.save(vendor)
    fun deleteById(id: Long) = repository.deleteById(id)
}