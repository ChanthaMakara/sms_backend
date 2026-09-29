package com.onemore.mission.vehicle.controller

import com.onemore.mission.common.response.ApiResponse
import com.onemore.mission.vehicle.dto.request.UpdateVehicleRequestStatusRequest
import com.onemore.mission.vehicle.dto.response.VehicleRequestResponse
import com.onemore.mission.vehicle.service.VehicleRequestService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/annexes/vehicle-requests")
class AnnexVehicleRequestController(
    private val vehicleRequestService: VehicleRequestService
) {

    @GetMapping
    fun getAllVehicleRequests(): ApiResponse<List<VehicleRequestResponse>> {
        return ApiResponse.success(vehicleRequestService.getAllVehicleRequests())
    }

    @PatchMapping("/{id}")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: UpdateVehicleRequestStatusRequest
    ): ApiResponse<VehicleRequestResponse> {
        return ApiResponse.success(vehicleRequestService.updateStatus(id, request.status))
    }
}
