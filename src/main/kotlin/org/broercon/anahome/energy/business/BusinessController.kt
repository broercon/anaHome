package org.broercon.anahome.energy.business

import org.broercon.anahome.energy.consumptionentry.ConsumptionEntryRest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/energy")
class BusinessController (val businessService: BusinessService) {

    @GetMapping("")
    fun getAllData(@RequestParam("from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) start: LocalDateTime?,
                   @RequestParam("to", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end: LocalDateTime?) : ResponseEntity<List<BusinessRest>> {
        return ResponseEntity<ConsumptionEntryRest>.ok(businessService.getAllData(start, end))
    }

    @GetMapping("/{id}")
    fun getAllDataOfMeterType(@PathVariable id: Long,
                              @RequestParam("from", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) start: LocalDateTime,
                              @RequestParam("to", required = true)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) end: LocalDateTime) : ResponseEntity<BusinessRest> {
        return ResponseEntity<ConsumptionEntryRest>.ok(businessService.getAllDataOfMeterType(id, start, end))
    }
}