package com.onemore.mission.mileage.domain

import com.onemore.mission.user.domain.JobLevel
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "mileage_claims")
class MileageClaim(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "mission_id", nullable = false)
    var missionId: Long,

    @Column(name = "vehicle_request_id")
    var vehicleRequestId: Long? = null,

    @Column(name = "requester_name", nullable = false)
    var requesterName: String,

    @Column(name = "requester_id")
    var requesterId: String? = null,

    @Column(name = "position")
    var position: String? = null,

    @Column(name = "function_name")
    var function: String? = null,

    @Column(name = "business")
    var business: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", length = 50)
    var jobLevel: JobLevel? = null,

    @Column(name = "based_location")
    var basedLocation: String? = null,

    @Column(name = "destination_location")
    var destinationLocation: String? = null,

    @Column(name = "travel_start_date", nullable = false)
    var travelStartDate: LocalDate,

    @Column(name = "travel_end_date", nullable = false)
    var travelEndDate: LocalDate,

    @Column(name = "travel_objectives", nullable = false)
    var travelObjectives: String,

    @OneToMany(mappedBy = "mileageClaim", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var travelDetails: MutableList<MileageTravelDetail> = mutableListOf(),

    @Column(name = "total_distance_km", nullable = false, precision = 10, scale = 2)
    var totalDistanceKm: BigDecimal,

    @Column(name = "total_claim_amount", nullable = false, precision = 18, scale = 2)
    var totalClaimAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    var status: MileageClaimStatus = MileageClaimStatus.DRAFT,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)