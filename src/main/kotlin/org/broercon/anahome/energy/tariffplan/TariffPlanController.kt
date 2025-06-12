package org.broercon.anahome.energy.tariffplan

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
@RequestMapping("/api/energy/tariffplan")
class TariffPlanController (var service : TariffPlanService, var mapper : TariffPlanMapper) {

    @GetMapping("")
    fun getMeterTypes() : List<TariffPlanRest?> =  mapper.run { service.getAll().toRest() }

    @PostMapping
    fun create(@RequestBody @Valid dto: TariffPlanRest): ResponseEntity<TariffPlanRest> {
        val created = mapper.run { service.create(dto.toDomain()) }
        val response = mapper.run { created.toRest() }
        val location = URI.create("/tariffplan/${created.id}") // adapt path as needed
        return ResponseEntity.created(location).body(response)
    }

    @GetMapping("/{id}")
    fun getMeterTypeById(@PathVariable id: Long): TariffPlanRest? = mapper.run { service.getById(id).toRest() }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody @Valid dto: TariffPlanRest): ResponseEntity<TariffPlanRest> =
        ResponseEntity.ok(mapper.run { service.save(id, dto.toDomain()).toRest()})

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.accepted().build()
    }
}