package org.broercon.anahome.money.transaction

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
@RequestMapping("/api/money/transaction")
class TransactionController(
    private val transactionService: TransactionService,
    private val transactionMapper: TransactionMapper
) {
    @GetMapping
    fun getAll(): List<TransactionRest> = transactionMapper.run{transactionService.findAll().toRest() }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<TransactionRest> =
        transactionMapper.run {transactionService.findById(id)?.let { ResponseEntity.ok(it.toRest()) }
            ?: ResponseEntity.notFound().build() }

    @PostMapping
    fun create(@RequestBody dto: TransactionRest): ResponseEntity<TransactionRest> {
        lateinit var  transactionEntiy: TransactionEntity
        transactionMapper.run { transactionEntiy =  dto.toEntity() }
        return transactionMapper.run{ ResponseEntity.status(HttpStatus.CREATED).body(transactionService.save(transactionEntiy).toRest())}
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        transactionService.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}