package org.broercon.anahome.energy.consumptionentry

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/energy/consumptionentry")
class ConsumptionEntryController(var service : ConsumptionEntryService, var mapper: ConsumptionEntryMapper) {

    @GetMapping("")
    fun getMeterTypes() : List<ConsumptionEntryRest?> = mapper.run { service.getAll().toRest() }

    @GetMapping("/{id}")
    fun getMeterTypeById(@PathVariable id: Long): ResponseEntity<ConsumptionEntryRest> {
        val entity: ConsumptionEntryEntity? = service.getById(id)
        return ResponseEntity<ConsumptionEntryRest>.ok(mapper.run {  entity.toRest() } )
    }

    @PostMapping
    fun create(@RequestBody dto: ConsumptionEntryRest): ResponseEntity<ConsumptionEntryRest> {
        val created = mapper.run { service.create(dto.toDomain()) }
        val response = mapper.run { created.toRest() }
        val location = URI.create("/meter/${created.id}") // adapt path as needed

        return ResponseEntity.created(location).body(response)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ConsumptionEntryRest): ResponseEntity<ConsumptionEntryRest> =
        ResponseEntity.ok(mapper.run { service.save(id,  dto.toDomain()).toRest()} )

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.accepted().build()
    }
}