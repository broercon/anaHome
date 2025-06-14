package org.broercon.anahome.money.transaction

import org.broercon.anahome.money.purpose.PurposeEntity
import org.broercon.anahome.money.purpose.PurposeRepository
import org.springframework.stereotype.Service

@Service
class TransactionService(private val repository: TransactionRepository) {
    fun findAll(): List<TransactionEntity> = repository.findAll()
    fun findById(id: Long): TransactionEntity? = repository.findById(id).orElse(null)
    fun save(vendor: TransactionEntity): TransactionEntity = repository.save(vendor)
    fun deleteById(id: Long) = repository.deleteById(id)
}