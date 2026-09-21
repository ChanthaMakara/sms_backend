package com.onemore.mission.common.response

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <T> success(data: T, message: String = "Success"): ApiResponse<T> {
            return ApiResponse(true, message, data)
        }

        fun <T> error(message: String): ApiResponse<T> {
            return ApiResponse(false, message, null)
        }
    }
}