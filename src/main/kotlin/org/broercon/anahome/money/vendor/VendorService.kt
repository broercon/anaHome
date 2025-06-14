package org.broercon.anahome.money.vendor

import org.broercon.anahome.money.transaction.TransactionEntity
import org.broercon.anahome.money.transaction.TransactionRepository
import org.springframework.stereotype.Service

@Service
class VendorService(private val repository: VendorRepository) {
    fun findAll(): List<VendorEntity> = repository.findAll()
    fun findById(id: Long): VendorEntity? = repository.findById(id).orElse(null)
    fun save(vendor: VendorEntity): VendorEntity = repository.save(vendor)
    fun deleteById(id: Long) = repository.deleteById(id)
}