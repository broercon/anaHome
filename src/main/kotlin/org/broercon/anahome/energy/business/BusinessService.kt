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

@Service
class BusinessService (
    private val meterTypeService: MeterTypeService,
    private val meterService: MeterService,
    private val meterUnitService: MeterUnitService,
    private val consumptionEntryService: ConsumptionEntryService,
    private val tariffplanService: TariffPlanService,
    private val tariffRateService: TariffRateService
){
    fun getAllData(idMeterType: Long): BusinessRest {
        val returnValue = BusinessRest()

        // Fill Name of MeterType
        val meterTypeEntity = meterTypeService.getById(idMeterType)
        returnValue.nameMeterType = meterTypeEntity.name

        val meter = meterService.getByMeterType(meterTypeEntity.id)

        meter.forEach { itMeter ->
            val businessMeterRest = BusinessMeterRest(itMeter.name, itMeter.location)
            val meterUnit = meterUnitService.getAllByMeter(itMeter.id!!)

            meterUnit.forEach { itMeterUnit ->
                val businessColumnRest = BusinessColumnRest(meterUnit = itMeterUnit.unit)
                val volumes: List<ConsumptionVolumesRest> = consumptionEntryService.getVolumesByUnit(itMeterUnit.id!!)

                volumes.forEach { itVolume ->
                    businessColumnRest.effectiveTo = itVolume.start
                    businessColumnRest.effectiveFrom = itVolume.end
                    businessColumnRest.volume = itVolume.total

                    // add prices
                    val tariffPlan = tariffplanService.getByMeterTypeAndPeriod(itMeterUnit.id!!,itVolume.start, itVolume.end)
                    if (tariffPlan.size > 1) throw IncorrectResultSizeDataAccessException(1) // TODO handle

                    businessColumnRest.nameTariff = tariffPlan.first().label

                    val tariffRate = tariffRateService.getByTariffPlanAndPeriod(tariffPlan.first().id!!, itVolume.start, itVolume.end)
                    if(tariffRate.size > 1) throw IncorrectResultSizeDataAccessException(1)

                    businessColumnRest.price = tariffRate.first().unitPrice

                    businessMeterRest.businessColumns.add(businessColumnRest)
                }
            }
            returnValue.businessMeterRest.add(businessMeterRest)
        }

        return returnValue
    }
    private fun validatePriceVolumes() : Boolean {
        return true
    }
}