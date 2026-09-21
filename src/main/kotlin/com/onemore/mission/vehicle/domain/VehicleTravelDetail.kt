package com.onemore.mission.vehicle.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "vehicle_travel_details")
class VehicleTravelDetail(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_request_id", nullable = false)
    var vehicleRequest: VehicleRequest? = null,

    @Column(name = "travel_date", nullable = false)
    var travelDate: LocalDate,

    @Column(name = "origin", nullable = false)
    var origin: String,

    @Column(name = "destination", nullable = false)
    var destination: String,

    @Column(name = "purpose_of_travel")
    var purposeOfTravel: String? = null,

    @Column(name = "distance_km", nullable = false, precision = 10, scale = 2)
    var distanceKm: BigDecimal,

    @Column(name = "remarks")
    var remarks: String? = null
)