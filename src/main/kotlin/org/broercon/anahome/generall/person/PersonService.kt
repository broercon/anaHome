package org.broercon.anahome.generall.person

import org.springframework.stereotype.Service

@Service
class PersonService(private val repository: PersonRepository) {
    fun findAll(): List<PersonEntity> = repository.findAll()
    fun findById(id: Long): PersonEntity? = repository.findById(id).orElse(null)
    fun save(person: PersonEntity): PersonEntity = repository.save(person)
    fun deleteById(id: Long) = repository.deleteById(id)
}