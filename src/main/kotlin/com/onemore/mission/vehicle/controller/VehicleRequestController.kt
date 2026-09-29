package com.onemore.mission.vehicle.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.vehicle.dto.request.CreateVehicleRequestRequest
import com.onemore.mission.vehicle.dto.response.VehicleRequestResponse
import com.onemore.mission.vehicle.service.VehicleRequestService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/missions/{missionId}/vehicle-requests")
class VehicleRequestController(
    private val vehicleRequestService: VehicleRequestService
) {

    // CREATE
    @PostMapping
    fun createVehicleRequest(
        @PathVariable missionId: Long,
        @Valid @RequestBody request: CreateVehicleRequestRequest
    ): ResponseEntity<ApiResponse<VehicleRequestResponse>> {

        val result =
            vehicleRequestService.createVehicleRequest(
                missionId,
                request
            )

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(result))
    }

    // GET ALL
    @GetMapping
    fun getVehicleRequests(
        @PathVariable missionId: Long
    ): ResponseEntity<ApiResponse<List<VehicleRequestResponse>>> {

        val result =
            vehicleRequestService.getVehicleRequestsByMission(
                missionId
            )

        return ResponseEntity.ok(
            ApiResponse.success(result)
        )
    }

    // GET BY ID
    @GetMapping("/{vehicleRequestId}")
    fun getVehicleRequestById(
        @PathVariable missionId: Long,
        @PathVariable vehicleRequestId: Long
    ): ResponseEntity<ApiResponse<VehicleRequestResponse>> {

        val result =
            vehicleRequestService.getVehicleRequestById(
                missionId,
                vehicleRequestId
            )

        return ResponseEntity.ok(
            ApiResponse.success(result)
        )
    }

    // UPDATE
    @PutMapping("/{vehicleRequestId}")
    fun updateVehicleRequest(
        @PathVariable missionId: Long,
        @PathVariable vehicleRequestId: Long,
        @Valid @RequestBody request: CreateVehicleRequestRequest
    ): ResponseEntity<ApiResponse<VehicleRequestResponse>> {

        val result =
            vehicleRequestService.updateVehicleRequest(
                missionId,
                vehicleRequestId,
                request
            )

        return ResponseEntity.ok(
            ApiResponse.success(result)
        )
    }

    // DELETE
    @DeleteMapping("/{vehicleRequestId}")
    fun deleteVehicleRequest(
        @PathVariable missionId: Long,
        @PathVariable vehicleRequestId: Long
    ): ResponseEntity<ApiResponse<String>> {

        vehicleRequestService.deleteVehicleRequest(
            missionId,
            vehicleRequestId
        )

        return ResponseEntity.ok(
            ApiResponse.success(
                "Vehicle request deleted successfully"
            )
        )
    }
}