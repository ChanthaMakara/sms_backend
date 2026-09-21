package com.onemore.mission.report.domain

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "activity_reports")
class ActivityReport(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "mission_id", nullable = false)
    var missionId: Long,

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

    @Column(name = "achieved_results", nullable = false)
    var achievedResults: String,

    @Column(name = "next_plan")
    var nextPlan: String? = null,

    @Column(name = "attached_documents")
    var attachedDocuments: String? = null,

    @Column(name = "requester_signature_date")
    var requesterSignatureDate: LocalDate? = null,

    @Column(name = "function_manager_comment")
    var functionManagerComment: String? = null,

    @Column(name = "function_manager_signature_date")
    var functionManagerSignatureDate: LocalDate? = null,

    @Column(name = "biz_ops_comment")
    var bizOpsComment: String? = null,

    @Column(name = "biz_ops_signature_date")
    var bizOpsSignatureDate: LocalDate? = null,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant = Instant.now()
)