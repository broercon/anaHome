package org.broercon.anahome.energy.meter


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
@RequestMapping("/api/energy/meter")
class MeterController(var service : MeterService, var mapper: MeterMapper) {

    @GetMapping("")
    fun getMeterTypes() : List<MeterRest?> = mapper.run { service.getAll().toRest() }

    @PostMapping
    fun create(@RequestBody dto: MeterRest): ResponseEntity<MeterRest> {
        val created = mapper.run { service.create(dto.toDomain()) }
        val response = mapper.run { created.toRest() }
        val location = URI.create("/meter/${created.id}") // adapt path as needed

        return ResponseEntity.created(location).body(response)
    }

    @GetMapping("/{id}")
    fun getMeterTypeById(@PathVariable id: Long): ResponseEntity<MeterRest> {
        val entity: MeterEntity? = service.getById(id)
        return ResponseEntity<MeterRest>.ok(mapper.run {  entity.toRest() } )
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: MeterRest): ResponseEntity<MeterRest> =
        ResponseEntity.ok(mapper.run { service.save(id,  dto.toDomain()).toRest()} )

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.accepted().build()
    }
}

