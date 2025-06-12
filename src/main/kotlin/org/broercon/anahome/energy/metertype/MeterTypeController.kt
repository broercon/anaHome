package org.broercon.anahome.energy.metertype

import org.anaHome.org.broercon.anahome.energy.metertype.MeterTypeRest
import org.anaHome.org.broercon.anahome.energy.metertype.toDomain
import org.anaHome.org.broercon.anahome.energy.metertype.toRest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/energy/metertype")
class MeterTypeController(var service : MeterTypeService) {

    @GetMapping("")
    fun getMeterTypes(): List<MeterTypeRest?> = service.getAll().toRest()

    @PostMapping
    fun create(@RequestBody dto: MeterTypeRest): ResponseEntity<MeterTypeRest> =
        ResponseEntity.ok(service.create(dto.toDomain()).toRest())

    @GetMapping("/{id}")
    fun getMeterTypeById(@PathVariable id: Long): MeterTypeRest? = service.getById(id)?.toRest()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: MeterTypeRest): ResponseEntity<MeterTypeRest> =
        ResponseEntity.ok(service.save(id, dto.toDomain()).toRest())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.accepted().build()
    }
}
