package com.onemore.mission.common.response

import org.springframework.data.domain.Page

data class PageResponse<T>(
    val items: List<T>,
    val page: Int,
    val pageSize: Int,
    val total: Long,
    val totalPages: Int
) {
    companion object {
        fun <T> from(page: Page<T>, pageNumber: Int): PageResponse<T> {
            return PageResponse(
                items = page.content,
                page = pageNumber,
                pageSize = page.size,
                total = page.totalElements,
                totalPages = page.totalPages
            )
        }
    }
}
