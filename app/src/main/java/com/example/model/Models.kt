package com.example.model

enum class UserRole {
    WORKER,
    EMPLOYER,
    ADMIN
}

enum class UserStatus {
    active,
    suspended,
    blocked,
    pending
}

enum class VerificationStatus {
    unverified,
    pending,
    verified,
    rejected
}

enum class EmploymentType(val label: String) {
    FullTime("Full Time"),
    PartTime("Part Time"),
    Temporary("Temporary"),
    Contract("Contract"),
    DailyWork("Daily Work"),
    Freelance("Freelance")
}

enum class SalaryType(val label: String) {
    Monthly("per month"),
    Daily("per day"),
    Hourly("per hour"),
    FixedProject("fixed project")
}

enum class JobStatus {
    draft,
    active,
    paused,
    closed,
    filled
}

enum class ApplicationStatus(val label: String) {
    pending("Pending Review"),
    shortlisted("Shortlisted"),
    interview("Interview Stage"),
    accepted("Offer Accepted"),
    rejected("Application Rejected"),
    cancelled("Cancelled")
}

enum class ReportReason(val label: String) {
    FakeAccount("Fake Account"),
    FakeJob("Fake Job Posting"),
    Fraud("Financial Fraud / Scam"),
    Harassment("Harassment / Abuse"),
    Spam("Spam / Advertising"),
    UnsafeWorkplace("Unsafe Workplace"),
    Other("Other Issue")
}

enum class ReportStatus {
    open,
    investigating,
    resolved,
    dismissed
}

data class GeoLocation(
    val cityName: String = "Addis Ababa",
    val specificArea: String = "Bole",
    val latitude: Double = 9.0107,
    val longitude: Double = 38.7612,
    val country: String = "Ethiopia"
)

data class WorkExperience(
    val title: String = "",
    val company: String = "",
    val durationYears: Int = 1,
    val description: String = ""
)

data class EducationItem(
    val institution: String = "",
    val degree: String = "",
    val graduationYear: String = ""
)

data class CertificationItem(
    val title: String = "",
    val issuer: String = "",
    val year: String = ""
)

data class UserAccount(
    val uid: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val profileImage: String = "",
    val role: UserRole = UserRole.WORKER,
    val status: UserStatus = UserStatus.active,
    val verificationStatus: VerificationStatus = VerificationStatus.unverified,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val language: String = "en",
    val location: GeoLocation = GeoLocation()
)

data class WorkerProfile(
    val uid: String = "",
    val fullName: String = "",
    val profileImage: String = "",
    val phoneNumber: String = "",
    val headline: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val experienceList: List<WorkExperience> = emptyList(),
    val totalYearsExperience: Int = 1,
    val educationList: List<EducationItem> = emptyList(),
    val certifications: List<CertificationItem> = emptyList(),
    val preferredJobTypes: List<EmploymentType> = listOf(EmploymentType.FullTime),
    val expectedSalaryMin: Double = 5000.0,
    val expectedSalaryMax: Double = 15000.0,
    val salaryType: SalaryType = SalaryType.Monthly,
    val preferredLocation: GeoLocation = GeoLocation(),
    val isAvailable: Boolean = true,
    val rating: Double = 4.8,
    val reviewCount: Int = 12,
    val verificationStatus: VerificationStatus = VerificationStatus.verified,
    val verificationDocumentUrl: String = "",
    val nationalIdNumber: String = "",
    val completedJobsCount: Int = 8
)

data class EmployerProfile(
    val uid: String = "",
    val companyName: String = "",
    val logoUrl: String = "",
    val industry: String = "",
    val description: String = "",
    val contactPhone: String = "",
    val contactEmail: String = "",
    val website: String = "",
    val location: GeoLocation = GeoLocation(),
    val businessRegistrationNumber: String = "",
    val verificationStatus: VerificationStatus = VerificationStatus.verified,
    val rating: Double = 4.9,
    val reviewCount: Int = 18,
    val totalJobsPosted: Int = 5,
    val activeJobsCount: Int = 3
)

data class JobPost(
    val jobId: String = "",
    val employerId: String = "",
    val employerName: String = "",
    val employerLogo: String = "",
    val employerRating: Double = 4.8,
    val title: String = "",
    val description: String = "",
    val category: String = "General",
    val requiredSkills: List<String> = emptyList(),
    val experienceRequiredYears: Int = 1,
    val salaryMin: Double = 5000.0,
    val salaryMax: Double = 12000.0,
    val salaryType: SalaryType = SalaryType.Monthly,
    val employmentType: EmploymentType = EmploymentType.FullTime,
    val location: GeoLocation = GeoLocation(),
    val numberOfWorkers: Int = 1,
    val applicationDeadline: String = "Open",
    val status: JobStatus = JobStatus.active,
    val applicantsCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class JobApplication(
    val applicationId: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val employerId: String = "",
    val employerName: String = "",
    val workerId: String = "",
    val workerName: String = "",
    val workerPhoto: String = "",
    val workerRating: Double = 4.8,
    val workerSkills: List<String> = emptyList(),
    val status: ApplicationStatus = ApplicationStatus.pending,
    val message: String = "",
    val proposedRate: Double = 0.0,
    val interviewDate: String = "",
    val offerDetails: String = "",
    val appliedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val messageId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderRole: UserRole = UserRole.WORKER,
    val text: String = "",
    val imageUrl: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatConversation(
    val chatId: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val participantIds: List<String> = emptyList(),
    val workerId: String = "",
    val workerName: String = "",
    val workerPhoto: String = "",
    val employerId: String = "",
    val employerName: String = "",
    val employerLogo: String = "",
    val lastMessage: String = "",
    val lastMessageTimestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
)

data class ReviewRating(
    val ratingId: String = "",
    val reviewerId: String = "",
    val reviewerName: String = "",
    val reviewerRole: UserRole = UserRole.EMPLOYER,
    val revieweeId: String = "",
    val revieweeName: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val rating: Int = 5,
    val reviewText: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class VerificationItem(
    val verificationId: String = "",
    val userId: String = "",
    val userName: String = "",
    val role: UserRole = UserRole.WORKER,
    val documentType: String = "National ID / Kebele Card",
    val documentNumber: String = "",
    val documentImageUrl: String = "",
    val status: VerificationStatus = VerificationStatus.pending,
    val rejectionReason: String = "",
    val submittedAt: Long = System.currentTimeMillis()
)

data class ReportItem(
    val reportId: String = "",
    val reporterId: String = "",
    val reporterName: String = "",
    val reportedUserId: String = "",
    val reportedUserName: String = "",
    val jobId: String = "",
    val jobTitle: String = "",
    val reason: ReportReason = ReportReason.FakeAccount,
    val description: String = "",
    val status: ReportStatus = ReportStatus.open,
    val resolutionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class AdminAnnouncement(
    val announcementId: String = "",
    val title: String = "",
    val body: String = "",
    val targetRole: String = "ALL", // "ALL", "WORKER", "EMPLOYER"
    val isUrgent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

enum class NotificationType(val titleRes: String) {
    NEW_JOB("New Job Opportunity"),
    NEW_APPLICATION("New Application Received"),
    APPLICATION_ACCEPTED("Application Accepted!"),
    APPLICATION_REJECTED("Application Update"),
    INTERVIEW_INVITATION("Interview Invitation"),
    JOB_OFFER("Official Job Offer"),
    NEW_MESSAGE("New Message Received"),
    VERIFICATION_STATUS("Verification Status Update"),
    ADMIN_ANNOUNCEMENT("Marketplace Announcement"),
    INFO("System Notification")
}

data class AppNotification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.INFO,
    val relatedEntityId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class MatchBreakdown(
    val overallScore: Int = 0,
    val skillsScore: Int = 0,
    val experienceScore: Int = 0,
    val locationScore: Int = 0,
    val salaryScore: Int = 0,
    val matchedSkills: List<String> = emptyList()
)
