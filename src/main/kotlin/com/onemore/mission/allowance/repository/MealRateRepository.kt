package com.onemore.mission.allowance.repository

import com.onemore.mission.allowance.domain.MealRate
import com.onemore.mission.mission.domain.LocationTier
import com.onemore.mission.user.domain.JobLevel
import org.springframework.data.jpa.repository.JpaRepository

interface MealRateRepository : JpaRepository<MealRate, Long> {

    fun findByLocationTierAndJobLevel(
        locationTier: LocationTier,
        jobLevel: JobLevel
    ): MealRate?
}