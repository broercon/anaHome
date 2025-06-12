package org.broercon.anahome.energy.tariffrate

import jakarta.validation.Valid
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
@RequestMapping("/api/energy/tariffrate")
class TariffRateController (var service : TariffRateService, var mapper : TariffRateMapper) {

    @GetMapping("")
    fun getAll() : List<TariffRateRest> =  mapper.run { service.getAll().toRest() }

    @GetMapping("/plan/{id}")
    fun getAllByTariffPlan(@PathVariable id: Long) : List<TariffRateRest> =  mapper.run { service.getAllByTariffPlan(id).toRest() }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TariffRateRest? = mapper.run { service.getById(id).toRest() }

    @PostMapping
    fun create(@RequestBody @Valid dto: TariffRateRest): ResponseEntity<TariffRateRest> {
        val created = mapper.run { service.create(dto.toDomain()) }
        val response = mapper.run { created.toRest() }
        val location = URI.create("/tariffrate/${created.id}") // adapt path as needed
        return ResponseEntity.created(location).body(response)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid dto: TariffRateRest): ResponseEntity<TariffRateRest> =
        ResponseEntity.ok(mapper.run { service.save(id, dto.toDomain()).toRest()})

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.accepted().build()
    }
}