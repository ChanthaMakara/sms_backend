package com.onemore.mission.allowance.service

import com.onemore.mission.allowance.repository.AccommodationRateRepository
import com.onemore.mission.allowance.repository.MealRateRepository
import com.onemore.mission.mission.domain.MissionParticipant
import com.onemore.mission.mission.domain.MissionType
import com.onemore.mission.mission.dto.response.MissionParticipantResponse
import com.onemore.mission.mission.dto.response.MissionResponse
import com.onemore.mission.mission.mapper.MissionMapper
import com.onemore.mission.mission.repository.MissionParticipantRepository
import com.onemore.mission.mission.repository.MissionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class AllowanceService(
    private val missionRepository: MissionRepository,
    private val mealRateRepository: MealRateRepository,
    private val accommodationRateRepository: AccommodationRateRepository,
    private val missionMapper: MissionMapper,
    private val participantRepository: MissionParticipantRepository
) {

    @Transactional
    fun calculateAndSave(missionId: Long): MissionResponse {
        val mission = missionRepository.findById(missionId)
            .orElseThrow {
                IllegalArgumentException("Mission not found with id: $missionId")
            }

        val participants = participantRepository.findAllByMissionIdIn(listOf(missionId))
        if (participants.isEmpty()) {
            throw IllegalStateException("Mission $missionId has no participants.")
        }

        val days = mission.numberOfTravelDays
        val nights = (days - 1).coerceAtLeast(0)
        val daysBd = days.toBigDecimal()
        val nightsBd = nights.toBigDecimal()
        val isIndividual = mission.missionType == MissionType.INDIVIDUAL

        var sumBreakfast = BigDecimal.ZERO
        var sumLunch = BigDecimal.ZERO
        var sumDinner = BigDecimal.ZERO
        var sumAccommodation = BigDecimal.ZERO

        if (!isIndividual) {
            mission.breakfastAmount = null
            mission.lunchAmount = null
            mission.dinnerAmount = null
            mission.accommodationAmountPerNight = null
        }

        for (p in participants) {
            val mealRate = mealRateRepository.findByLocationTierAndJobLevel(
                mission.locationTier, p.jobLevel
            ) ?: throw IllegalStateException(
                "No meal rate configured for locationTier=${mission.locationTier}, jobLevel=${p.jobLevel} (${p.fullName})"
            )

            val accommodationRate = accommodationRateRepository.findByLocationTierAndJobLevel(
                mission.locationTier, p.jobLevel
            ) ?: throw IllegalStateException(
                "No accommodation rate configured for locationTier=${mission.locationTier}, jobLevel=${p.jobLevel} (${p.fullName})"
            )

            p.breakfastTotal = mealRate.breakfastAmount.multiply(daysBd)
            p.lunchTotal = mealRate.lunchAmount.multiply(daysBd)
            p.dinnerTotal = mealRate.dinnerAmount.multiply(daysBd)
            p.accommodationTotal = accommodationRate.amountPerNight.multiply(nightsBd)
            p.totalExpense = p.breakfastTotal.add(p.lunchTotal).add(p.dinnerTotal).add(p.accommodationTotal)

            sumBreakfast = sumBreakfast.add(p.breakfastTotal)
            sumLunch = sumLunch.add(p.lunchTotal)
            sumDinner = sumDinner.add(p.dinnerTotal)
            sumAccommodation = sumAccommodation.add(p.accommodationTotal)

            if (isIndividual) {
                mission.breakfastAmount = mealRate.breakfastAmount
                mission.lunchAmount = mealRate.lunchAmount
                mission.dinnerAmount = mealRate.dinnerAmount
                mission.accommodationAmountPerNight = accommodationRate.amountPerNight
            }
        }
        participantRepository.saveAll(participants)

        val headcount = participants.size
        mission.breakfastQuantity = days * headcount
        mission.lunchQuantity = days * headcount
        mission.dinnerQuantity = days * headcount
        mission.numberOfNightStay = nights

        mission.breakfastTotal = sumBreakfast
        mission.lunchTotal = sumLunch
        mission.dinnerTotal = sumDinner
        mission.accommodationTotal = sumAccommodation
        mission.totalExpense = sumBreakfast.add(sumLunch).add(sumDinner).add(sumAccommodation)

        val savedMission = missionRepository.save(mission)

        val sorted = participants
            .sortedWith(compareByDescending<MissionParticipant> { it.requester }.thenBy { it.fullName })
            .map(MissionParticipantResponse::from)

        return missionMapper.toResponse(savedMission, sorted)
    }
}
