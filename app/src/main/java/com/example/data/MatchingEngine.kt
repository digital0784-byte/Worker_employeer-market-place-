package com.example.data

import com.example.model.GeoLocation
import com.example.model.JobPost
import com.example.model.MatchBreakdown
import com.example.model.WorkerProfile
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MatchingEngine {

    /**
     * Calculates comprehensive match score between a worker profile and a job post.
     * Weights:
     * - Skills Overlap: 40%
     * - Experience Compatibility: 25%
     * - Location / Distance: 20%
     * - Salary Alignment: 15%
     */
    fun calculateMatch(worker: WorkerProfile, job: JobPost): MatchBreakdown {
        // 1. Skills Match (0-100)
        val workerSkillsLower = worker.skills.map { it.trim().lowercase() }.toSet()
        val requiredSkillsLower = job.requiredSkills.map { it.trim().lowercase() }.toSet()

        val matchingSkillsList = mutableListOf<String>()
        val skillsScore: Int = if (requiredSkillsLower.isEmpty()) {
            85
        } else {
            var overlapCount = 0
            for (req in requiredSkillsLower) {
                val matched = workerSkillsLower.any { wSkill ->
                    wSkill.contains(req) || req.contains(wSkill)
                }
                if (matched) {
                    overlapCount++
                    matchingSkillsList.add(req.replaceFirstChar { it.uppercase() })
                }
            }
            ((overlapCount.toDouble() / requiredSkillsLower.size.toDouble()) * 100).toInt().coerceIn(20, 100)
        }

        // 2. Experience Match (0-100)
        val expRequired = job.experienceRequiredYears.coerceAtLeast(1)
        val workerExp = worker.totalYearsExperience
        val expScore: Int = when {
            workerExp >= expRequired -> 100
            workerExp == expRequired - 1 -> 80
            workerExp >= 1 -> 60
            else -> 40
        }

        // 3. Location Match (0-100)
        val distanceKm = calculateDistanceKm(
            worker.preferredLocation.latitude,
            worker.preferredLocation.longitude,
            job.location.latitude,
            job.location.longitude
        )
        val locationScore: Int = when {
            worker.preferredLocation.cityName.equals(job.location.cityName, ignoreCase = true) && distanceKm <= 10.0 -> 100
            worker.preferredLocation.cityName.equals(job.location.cityName, ignoreCase = true) -> 90
            distanceKm <= 25.0 -> 80
            distanceKm <= 50.0 -> 65
            else -> 45
        }

        // 4. Salary Match (0-100)
        val salaryScore: Int = if (worker.expectedSalaryMin <= job.salaryMax && worker.expectedSalaryMax >= job.salaryMin) {
            100
        } else if (worker.expectedSalaryMin <= job.salaryMax * 1.2) {
            80
        } else {
            60
        }

        // Availability factor
        val availabilityBonus = if (worker.isAvailable) 5 else -10

        // Weighted sum
        val overall = (
            (skillsScore * 0.40) +
            (expScore * 0.25) +
            (locationScore * 0.20) +
            (salaryScore * 0.15) +
            availabilityBonus
        ).toInt().coerceIn(10, 99)

        return MatchBreakdown(
            overallScore = overall,
            skillsScore = skillsScore,
            experienceScore = expScore,
            locationScore = locationScore,
            salaryScore = salaryScore,
            matchedSkills = matchingSkillsList
        )
    }

    /**
     * Haversine formula for distance in kilometers
     */
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
}
