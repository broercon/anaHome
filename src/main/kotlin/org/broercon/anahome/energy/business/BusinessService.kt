package org.broercon.anahome.energy.business

import org.broercon.anahome.energy.consumptionentry.ConsumptionEntryService
import org.broercon.anahome.energy.consumptionentry.ConsumptionVolumesRest
import org.broercon.anahome.energy.meter.MeterService
import org.broercon.anahome.energy.meterUnit.MeterUnitService
import org.broercon.anahome.energy.metertype.MeterTypeService
import org.broercon.anahome.energy.tariffplan.TariffPlanService
import org.broercon.anahome.energy.tariffrate.TariffRateService
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class BusinessService (
    private val meterTypeService: MeterTypeService,
    private val meterService: MeterService,
    private val meterUnitService: MeterUnitService,
    private val consumptionEntryService: ConsumptionEntryService,
    private val tariffplanService: TariffPlanService,
    private val tariffRateService: TariffRateService
){
    fun getAllData(start: LocalDateTime?, end: LocalDateTime?) : List<BusinessRest> {
        val returnValue = mutableListOf<BusinessRest>()
        val meterTypes = meterTypeService.getAll()

        meterTypes.forEach { typesIt ->
            returnValue.add(getAllDataOfMeterType(typesIt.id, start ?: LocalDateTime.now().minusYears(1.toLong()), end ?: LocalDateTime.now() ))
        }

        return returnValue
    }

    fun getAllDataOfMeterType(idMeterType: Long, start: LocalDateTime, end: LocalDateTime): BusinessRest {
        val returnValue = BusinessRest()

        // validate Timestamp of ConsumptionEntries
        if (!consumptionEntryService.isVolumeByMeterTypeAndPeriod(idMeterType, start, end)) throw IllegalStateException("Start or End doesn't have an Consumption Entry.")

        // Fill Name of MeterType
        val meterTypeEntity = meterTypeService.getById(idMeterType)
        returnValue.nameMeterType = meterTypeEntity.name

        val meter = meterService.getByMeterType(meterTypeEntity.id)

        meter.forEach { itMeter ->
            val businessMeterRest = BusinessMeterRest(itMeter.name, itMeter.location)

            // Volumes and prices for MeterUnits
            val meterUnit = meterUnitService.getAllByMeter(itMeter.id!!)
            meterUnit.forEach { itMeterUnit ->
                val businessColumnRest = BusinessColumnRest(unit = itMeterUnit.unit, meterUnitLabel = itMeterUnit.label)

                // todo check Dates
                val volumes: List<ConsumptionVolumesRest> = consumptionEntryService.getVolumesByUnit(itMeterUnit.id!!)
                volumes.forEach { itVolume ->
                    businessColumnRest.effectiveTo = itVolume.start
                    businessColumnRest.effectiveFrom = itVolume.end
                    businessColumnRest.volume = itVolume.total

                    // add prices
                    val tariffPlan = tariffplanService.getByMeterUnitAndPeriod(itMeterUnit.id!!,itVolume.start, itVolume.end)
                    if (tariffPlan.size != 0 ) {
                        if (tariffPlan.size != 1) throw IncorrectResultSizeDataAccessException(1) // TODO handle

                        businessColumnRest.nameTariff = tariffPlan.first().name

                        val tariffRate = tariffRateService.getByTariffPlanAndPeriod(tariffPlan.first().id!!, itVolume.start, itVolume.end)
                        if(tariffRate.size != 1) throw IncorrectResultSizeDataAccessException(1)

                        if (tariffRate.first().unit == "GP") {
                            businessColumnRest.volume = ChronoUnit.DAYS.between(itVolume.start,itVolume.end).toDouble()
                            businessColumnRest.unit = "Day"
                        }
                        businessColumnRest.price = tariffRate.first().unitPrice

                        businessMeterRest.businessColumns.add(businessColumnRest)
                    }
                }
            }

            // Volumes and prices for Meter
            val tariffPlan = tariffplanService.getByMeterAndPeriod(itMeter.id, start, end)

            tariffPlan.forEach { itTariffplan ->
                val businessColumnRest = BusinessColumnRest(meterUnitLabel = itMeter.name, nameTariff = itTariffplan.name, )
// todo Period
                val tariffRate = tariffRateService.getByTariffPlanAndPeriod(tariffPlan.first().id!!, itTariffplan.effectiveFrom,itTariffplan.effectiveTo?: end)
                if(tariffRate.size != 1) throw IncorrectResultSizeDataAccessException(1)

                if (tariffRate.first().unit == "GP") {
                    businessColumnRest.volume = ChronoUnit.DAYS.between(itTariffplan.effectiveFrom,itTariffplan.effectiveTo).toDouble()
                    businessColumnRest.unit = "Day"
                }
                businessColumnRest.price = tariffRate.first().unitPrice

                businessMeterRest.businessColumns.add(businessColumnRest)
            }

            if (businessMeterRest.businessColumns.size != 0) returnValue.businessMeterRest.add(businessMeterRest)

        }

        // Volumes and prices for meterType
        val businessMeterTypeRest = BusinessMeterRest(meterTypeEntity.name)

        // Volumes and prices for Meter
        val tariffPlan = tariffplanService.getByMeterTypeAndPeriod(meterTypeEntity.id, start, end)

        tariffPlan.forEach { itTariffPlan ->
            val businessColumnRest = BusinessColumnRest(meterUnitLabel = meterTypeEntity.name, nameTariff = itTariffPlan.name, )
            businessColumnRest.effectiveFrom = start
            businessColumnRest.effectiveTo = end

            val tariffRate = tariffRateService.getByTariffPlanAndPeriod(tariffPlan.first().id!!, start,end)
            tariffRate.forEach { rateIt ->
                val businessColumnRestCopy = businessColumnRest.copy()
                if (rateIt.effectiveFrom > businessColumnRestCopy.effectiveFrom) businessColumnRestCopy.effectiveFrom = rateIt.effectiveFrom
                if (rateIt.effectiveTo != null && rateIt.effectiveTo < businessColumnRestCopy.effectiveTo ) businessColumnRestCopy.effectiveTo = rateIt.effectiveTo
                businessColumnRestCopy.unit = rateIt.unit
                if (rateIt.unit == "Days") {
                    businessColumnRestCopy.volume = ChronoUnit.DAYS.between(businessColumnRestCopy.effectiveFrom,businessColumnRestCopy.effectiveTo).toDouble()
                } else {
                    businessColumnRestCopy.volume = consumptionEntryService.getVolumeByMeterTypeAndPeriod(meterTypeEntity.id, businessColumnRestCopy.effectiveFrom,businessColumnRestCopy.effectiveTo!! )
                }

                businessColumnRestCopy.price = rateIt.unitPrice

                businessMeterTypeRest.businessColumns.add(businessColumnRestCopy)
            }
        }

        returnValue.businessMeterRest.add(businessMeterTypeRest)
        return returnValue
    }
    private fun validatePriceVolumes() : Boolean {
        return true
    }
}



