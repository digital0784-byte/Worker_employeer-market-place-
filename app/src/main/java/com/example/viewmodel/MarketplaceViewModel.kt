package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.MarketplaceRepository
import com.example.data.MatchingEngine
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminMetrics(
    val totalWorkers: Int = 0,
    val totalEmployers: Int = 0,
    val activeJobsCount: Int = 0,
    val totalApplications: Int = 0,
    val successfulHires: Int = 0,
    val pendingVerifications: Int = 0,
    val openReports: Int = 0
)

data class JobWithMatch(
    val job: JobPost,
    val matchBreakdown: MatchBreakdown
)

data class WorkerWithMatch(
    val worker: WorkerProfile,
    val matchBreakdown: MatchBreakdown
)

class MarketplaceViewModel(
    val repository: MarketplaceRepository = MarketplaceRepository()
) : ViewModel() {

    val currentUser = repository.currentUser
    val isOnline = repository.isOnline
    val currentWorkerProfile = repository.currentWorkerProfile
    val currentEmployerProfile = repository.currentEmployerProfile

    val allJobs = repository.jobs
    val allWorkers = repository.workers
    val allEmployers = repository.employers
    val allApplications = repository.applications
    val conversations = repository.conversations
    val allMessages = repository.messages
    val verifications = repository.verifications
    val reports = repository.reports
    val announcements = repository.announcements
    val reviews = repository.reviews

    // Filters for Job Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedEmploymentType = MutableStateFlow<EmploymentType?>(null)
    val selectedEmploymentType = _selectedEmploymentType.asStateFlow()

    private val _selectedCity = MutableStateFlow("All Cities")
    val selectedCity = _selectedCity.asStateFlow()

    // Map filters
    private val _mapSearchRadiusKm = MutableStateFlow(25.0)
    val mapSearchRadiusKm = _mapSearchRadiusKm.asStateFlow()

    private val _mapShowJobs = MutableStateFlow(true)
    val mapShowJobs = _mapShowJobs.asStateFlow()

    private val _selectedMapCity = MutableStateFlow(repository.getCities().first())
    val selectedMapCity = _selectedMapCity.asStateFlow()

    // Active Chat
    private val _activeChatId = MutableStateFlow<String?>(null)
    val activeChatId = _activeChatId.asStateFlow()

    // App Language Code ("en", "am", "om", "ti", "so")
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage = _currentLanguage.asStateFlow()

    // Feedback Toast / Snackbar State
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage = _userMessage.asStateFlow()

    // Matched Jobs Feed for Worker
    val matchedJobsFeed: StateFlow<List<JobWithMatch>> = combine(
        allJobs,
        currentWorkerProfile,
        searchQuery,
        selectedCategory,
        combine(selectedEmploymentType, selectedCity) { empType, city -> Pair(empType, city) }
    ) { jobs, worker, query, cat, (empType, city) ->
        val activeWorker = worker ?: WorkerProfile(skills = listOf("General Work"))
        jobs.filter { job ->
            val matchesStatus = job.status == JobStatus.active
            val matchesQuery = query.isEmpty() ||
                job.title.contains(query, ignoreCase = true) ||
                job.description.contains(query, ignoreCase = true) ||
                job.requiredSkills.any { it.contains(query, ignoreCase = true) }
            val matchesCat = cat == "All" || job.category.equals(cat, ignoreCase = true)
            val matchesEmpType = empType == null || job.employmentType == empType
            val matchesCity = city == "All Cities" || job.location.cityName.equals(city, ignoreCase = true)

            matchesStatus && matchesQuery && matchesCat && matchesEmpType && matchesCity
        }.map { job ->
            val match = MatchingEngine.calculateMatch(activeWorker, job)
            JobWithMatch(job, match)
        }.sortedByDescending { it.matchBreakdown.overallScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Matched Workers for Employer
    val matchedWorkersForEmployer: StateFlow<List<WorkerWithMatch>> = combine(
        allWorkers,
        allJobs,
        currentEmployerProfile,
        searchQuery,
        selectedCity
    ) { workers, jobs, employer, query, city ->
        val primaryJob = jobs.firstOrNull { it.employerId == employer?.uid && it.status == JobStatus.active }
            ?: jobs.firstOrNull()
            ?: JobPost(title = "General Hiring", requiredSkills = listOf("Customer Service", "Technical"))

        workers.filter { worker ->
            val matchesQuery = query.isEmpty() ||
                worker.fullName.contains(query, ignoreCase = true) ||
                worker.headline.contains(query, ignoreCase = true) ||
                worker.skills.any { it.contains(query, ignoreCase = true) }
            val matchesCity = city == "All Cities" || worker.preferredLocation.cityName.equals(city, ignoreCase = true)
            matchesQuery && matchesCity
        }.map { worker ->
            val match = MatchingEngine.calculateMatch(worker, primaryJob)
            WorkerWithMatch(worker, match)
        }.sortedByDescending { it.matchBreakdown.overallScore }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Metrics
    val adminMetrics: StateFlow<AdminMetrics> = combine(
        allWorkers,
        allEmployers,
        allJobs,
        allApplications,
        combine(verifications, reports) { v, r -> Pair(v, r) }
    ) { wList, eList, jList, aList, (vList, rList) ->
        AdminMetrics(
            totalWorkers = wList.size,
            totalEmployers = eList.size,
            activeJobsCount = jList.count { it.status == JobStatus.active },
            totalApplications = aList.size,
            successfulHires = aList.count { it.status == ApplicationStatus.accepted },
            pendingVerifications = vList.count { it.status == VerificationStatus.pending },
            openReports = rList.count { it.status == ReportStatus.open }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminMetrics())

    // User Actions
    fun setRole(role: UserRole) {
        repository.setDemoUser(role)
        showMessage("Logged in as ${role.name}")
    }

    fun login(email: String, role: UserRole) {
        repository.login(email, role)
        showMessage("Welcome back!")
    }

    fun register(fullName: String, email: String, phone: String, role: UserRole) {
        repository.register(fullName, email, phone, role)
        showMessage("Account registered successfully!")
    }

    fun logout() {
        repository.logout()
        showMessage("Logged out")
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSelectedEmploymentType(type: EmploymentType?) {
        _selectedEmploymentType.value = type
    }

    fun setSelectedCity(city: String) {
        _selectedCity.value = city
    }

    fun setMapSearchRadius(radiusKm: Double) {
        _mapSearchRadiusKm.value = radiusKm
    }

    fun setMapShowJobs(showJobs: Boolean) {
        _mapShowJobs.value = showJobs
    }

    fun setSelectedMapCity(loc: GeoLocation) {
        _selectedMapCity.value = loc
    }

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
        showMessage("Language updated")
    }

    // Worker operations
    fun applyForJob(jobId: String, message: String, proposedRate: Double) {
        repository.applyForJob(jobId, message, proposedRate)
        showMessage("Application submitted successfully!")
    }

    fun cancelApplication(applicationId: String) {
        repository.cancelApplication(applicationId)
        showMessage("Application cancelled")
    }

    fun updateWorkerProfile(
        headline: String,
        bio: String,
        skills: List<String>,
        experienceYears: Int,
        salaryMin: Double,
        salaryMax: Double,
        location: GeoLocation
    ) {
        val current = currentWorkerProfile.value ?: return
        val updated = current.copy(
            headline = headline,
            bio = bio,
            skills = skills,
            totalYearsExperience = experienceYears,
            expectedSalaryMin = salaryMin,
            expectedSalaryMax = salaryMax,
            preferredLocation = location
        )
        repository.updateWorkerProfile(updated)
        showMessage("Profile updated successfully")
    }

    fun toggleAvailability(isAvailable: Boolean) {
        repository.toggleWorkerAvailability(isAvailable)
        showMessage(if (isAvailable) "Availability turned ON" else "Availability turned OFF")
    }

    fun requestWorkerVerification(docType: String, docNumber: String, docUrl: String = "") {
        repository.submitVerificationRequest(docType, docNumber, docUrl)
        showMessage("Verification documents submitted for review")
    }

    // Employer operations
    fun postJob(
        title: String,
        description: String,
        category: String,
        skills: List<String>,
        experienceYears: Int,
        salaryMin: Double,
        salaryMax: Double,
        salaryType: SalaryType,
        employmentType: EmploymentType,
        location: GeoLocation,
        numberOfWorkers: Int,
        deadline: String
    ) {
        val employer = currentEmployerProfile.value ?: return
        val newJob = JobPost(
            employerId = employer.uid,
            employerName = employer.companyName,
            employerLogo = employer.logoUrl,
            employerRating = employer.rating,
            title = title,
            description = description,
            category = category,
            requiredSkills = skills,
            experienceRequiredYears = experienceYears,
            salaryMin = salaryMin,
            salaryMax = salaryMax,
            salaryType = salaryType,
            employmentType = employmentType,
            location = location,
            numberOfWorkers = numberOfWorkers,
            applicationDeadline = deadline,
            status = JobStatus.active
        )
        repository.postJob(newJob)
        showMessage("Job posted successfully!")
    }

    fun updateJobStatus(jobId: String, status: JobStatus) {
        repository.updateJobStatus(jobId, status)
        showMessage("Job status updated to ${status.name}")
    }

    fun deleteJob(jobId: String) {
        repository.deleteJob(jobId)
        showMessage("Job deleted")
    }

    fun updateApplicantStatus(applicationId: String, status: ApplicationStatus, interviewNote: String = "", offerNote: String = "") {
        repository.updateApplicationStatus(applicationId, status, interviewNote, offerNote)
        showMessage("Applicant status updated to ${status.label}")
    }

    // Chat operations
    fun openChat(chatId: String) {
        _activeChatId.value = chatId
    }

    fun startChat(workerId: String, employerId: String, jobId: String, jobTitle: String): String {
        val chatId = repository.getOrCreateChat(workerId, employerId, jobId, jobTitle)
        _activeChatId.value = chatId
        return chatId
    }

    fun closeChat() {
        _activeChatId.value = null
    }

    fun sendMessage(chatId: String, text: String, imageUrl: String = "") {
        repository.sendMessage(chatId, text, imageUrl)
    }

    // Reviews & Ratings
    fun submitReview(
        revieweeId: String,
        revieweeName: String,
        jobId: String,
        jobTitle: String,
        rating: Int,
        reviewText: String
    ) {
        val user = currentUser.value ?: return
        val review = ReviewRating(
            reviewerId = user.uid,
            reviewerName = user.fullName,
            reviewerRole = user.role,
            revieweeId = revieweeId,
            revieweeName = revieweeName,
            jobId = jobId,
            jobTitle = jobTitle,
            rating = rating,
            reviewText = reviewText
        )
        repository.submitReview(review)
        showMessage("Rating submitted successfully!")
    }

    // Reports
    fun submitReport(
        reportedUserId: String,
        reportedUserName: String,
        jobId: String,
        jobTitle: String,
        reason: ReportReason,
        description: String
    ) {
        val user = currentUser.value ?: return
        val rep = ReportItem(
            reporterId = user.uid,
            reporterName = user.fullName,
            reportedUserId = reportedUserId,
            reportedUserName = reportedUserName,
            jobId = jobId,
            jobTitle = jobTitle,
            reason = reason,
            description = description
        )
        repository.submitReport(rep)
        showMessage("Report submitted to marketplace administrators")
    }

    // Admin Operations
    fun processVerification(verificationId: String, approve: Boolean, reason: String = "") {
        repository.processVerification(verificationId, approve, reason)
        showMessage(if (approve) "Verification approved!" else "Verification rejected")
    }

    fun resolveReport(reportId: String, status: ReportStatus, notes: String) {
        repository.resolveReport(reportId, status, notes)
        showMessage("Report status set to ${status.name}")
    }

    fun broadcastAnnouncement(title: String, body: String, targetRole: String, isUrgent: Boolean) {
        repository.postAnnouncement(title, body, targetRole, isUrgent)
        showMessage("Announcement broadcasted successfully")
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    fun clearMessage() {
        _userMessage.value = null
    }

    fun getCities(): List<GeoLocation> = repository.getCities()
}
