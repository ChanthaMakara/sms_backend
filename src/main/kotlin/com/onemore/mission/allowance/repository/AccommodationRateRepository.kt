package com.onemore.mission.allowance.repository

import com.onemore.mission.allowance.domain.AccommodationRate
import com.onemore.mission.mission.domain.LocationTier
import com.onemore.mission.user.domain.JobLevel
import org.springframework.data.jpa.repository.JpaRepository

interface AccommodationRateRepository : JpaRepository<AccommodationRate, Long> {

    fun findByLocationTierAndJobLevel(
        locationTier: LocationTier,
        jobLevel: JobLevel
    ): AccommodationRate?
}