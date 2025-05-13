package org.anaHome.org.broercon.anahome.energy.model


import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "meters")
data class Meter(
    @Id
    val id: Long,

    val name: String,

    @Column(name = "meter_number")
    val meterNumber: String,

    val location: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_type_id")
    val meterType: MeterType
)

