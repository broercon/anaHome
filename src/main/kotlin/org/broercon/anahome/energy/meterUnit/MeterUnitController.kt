package org.broercon.anahome.energy.meterUnit

import org.broercon.anahome.energy.consumptionentry.ConsumptionVolumesRest
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
@RequestMapping("/api/energy/meterunit")
class MeterUnitController (var service : MeterUnitService, var mapper : MeterUnitMapper) {

    @GetMapping("")
    fun getMeterUnit() : List<MeterUnitRest?> =  mapper.run { service.getAll().toRest() }

    @GetMapping("/{id}")
    fun getMeterUnitById(@PathVariable id: Long): MeterUnitRest? = mapper.run { service.getById(id).toRest() }

    @PostMapping
    fun create(@RequestBody dto: MeterUnitRest): ResponseEntity<MeterUnitRest> {
        val created = mapper.run { service.create(dto.toDomain()) }
        val response = mapper.run { created.toRest() }
        val location = URI.create("/meter/${created.id}") // adapt path as needed
        return ResponseEntity.created(location).body(response)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: MeterUnitRest): ResponseEntity<MeterUnitRest> =
        ResponseEntity.ok(mapper.run { service.save(id, dto.toDomain()).toRest()})

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.accepted().build()
    }
}