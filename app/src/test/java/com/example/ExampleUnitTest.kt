package com.example

import com.example.data.MatchingEngine
import com.example.model.EmploymentType
import com.example.model.GeoLocation
import com.example.model.JobPost
import com.example.model.WorkerProfile
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testMatchingEngine_highSkillOverlap() {
    val worker = WorkerProfile(
      skills = listOf("Plumbing", "Pipe Fitting", "Welding"),
      yearsOfExperience = 5,
      preferredLocation = GeoLocation(cityName = "Addis Ababa", latitude = 9.03, longitude = 38.74),
      preferredJobTypes = listOf(EmploymentType.full_time)
    )

    val job = JobPost(
      title = "Master Plumber",
      requiredSkills = listOf("Plumbing", "Pipe Fitting"),
      minExperienceYears = 3,
      location = GeoLocation(cityName = "Addis Ababa", latitude = 9.03, longitude = 38.74),
      employmentType = EmploymentType.full_time
    )

    val match = MatchingEngine.calculateMatch(worker, job)
    assertTrue("Overall score should be high for matching skills and location", match.overallScore >= 85)
    assertTrue("Skill score should be 100%", match.skillsMatchScore >= 95)
    assertTrue("Location score should be 100%", match.locationMatchScore >= 95)
  }

  @Test
  fun testDistanceCalculation() {
    val dist = MatchingEngine.calculateDistanceKm(9.03, 38.74, 9.03, 38.74)
    assertEquals(0.0, dist, 0.01)
  }
}
