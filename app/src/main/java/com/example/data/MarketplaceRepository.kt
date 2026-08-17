package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MarketplaceRepository {

    // Initial Seed Data with Ethiopian & East African Marketplace items
    private val ethiopianCities = listOf(
        GeoLocation("Addis Ababa", "Bole", 9.0107, 38.7612),
        GeoLocation("Addis Ababa", "Kirkos / Meskel Sq", 9.0125, 38.7520),
        GeoLocation("Addis Ababa", "Piazza / Arada", 9.0348, 38.7525),
        GeoLocation("Addis Ababa", "Akaki Kality", 8.8950, 38.7650),
        GeoLocation("Hawassa", "Piazza Lakefront", 7.0504, 38.4955),
        GeoLocation("Adama", "City Center", 8.5400, 39.2700),
        GeoLocation("Bahir Dar", "Kebele 04", 11.5936, 37.3908),
        GeoLocation("Dire Dawa", "Kezira", 9.6009, 41.8501),
        GeoLocation("Mekelle", "Hawelti", 13.4967, 39.4753),
        GeoLocation("Jimma", "Hermata", 7.6734, 36.8344),
        GeoLocation("Gondar", "Arada", 12.6075, 37.4589),
        GeoLocation("Bishoftu", "Lake Babogaya", 8.7523, 38.9785)
    )

    // Current logged-in user
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Data stores
    private val _jobs = MutableStateFlow<List<JobPost>>(emptyList())
    val jobs: StateFlow<List<JobPost>> = _jobs.asStateFlow()

    private val _workers = MutableStateFlow<List<WorkerProfile>>(emptyList())
    val workers: StateFlow<List<WorkerProfile>> = _workers.asStateFlow()

    private val _employers = MutableStateFlow<List<EmployerProfile>>(emptyList())
    val employers: StateFlow<List<EmployerProfile>> = _employers.asStateFlow()

    private val _applications = MutableStateFlow<List<JobApplication>>(emptyList())
    val applications: StateFlow<List<JobApplication>> = _applications.asStateFlow()

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messages: StateFlow<Map<String, List<ChatMessage>>> = _messages.asStateFlow()

    private val _verifications = MutableStateFlow<List<VerificationItem>>(emptyList())
    val verifications: StateFlow<List<VerificationItem>> = _verifications.asStateFlow()

    private val _reports = MutableStateFlow<List<ReportItem>>(emptyList())
    val reports: StateFlow<List<ReportItem>> = _reports.asStateFlow()

    private val _announcements = MutableStateFlow<List<AdminAnnouncement>>(emptyList())
    val announcements: StateFlow<List<AdminAnnouncement>> = _announcements.asStateFlow()

    private val _reviews = MutableStateFlow<List<ReviewRating>>(emptyList())
    val reviews: StateFlow<List<ReviewRating>> = _reviews.asStateFlow()

    // Current worker profile if role == WORKER
    private val _currentWorkerProfile = MutableStateFlow<WorkerProfile?>(null)
    val currentWorkerProfile: StateFlow<WorkerProfile?> = _currentWorkerProfile.asStateFlow()

    // Current employer profile if role == EMPLOYER
    private val _currentEmployerProfile = MutableStateFlow<EmployerProfile?>(null)
    val currentEmployerProfile: StateFlow<EmployerProfile?> = _currentEmployerProfile.asStateFlow()

    init {
        initializeSeedData()
    }

    private fun initializeSeedData() {
        // Sample Workers
        val sampleWorkers = listOf(
            WorkerProfile(
                uid = "w_001",
                fullName = "Alemayehu Tadesse",
                profileImage = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                phoneNumber = "+251 91 123 4567",
                headline = "Certified Master Electrician & Solar Installer",
                bio = "10+ years commercial & residential electrical wiring, certified by Ethiopian Ministry of Water & Energy. Experienced in solar power systems and generator maintenance.",
                skills = listOf("Electrical Wiring", "Solar Installation", "Generator Maintenance", "Circuit Design", "Safety Protocols"),
                totalYearsExperience = 8,
                experienceList = listOf(
                    WorkExperience("Senior Electrician", "Ethio Telecom Subcontractor", 4, "High voltage cabling and transformer installations"),
                    WorkExperience("Site Electrician", "Midroc Construction", 4, "Residential building wiring in Bole & CMC")
                ),
                educationList = listOf(
                    EducationItem("Tegbare-Id Technical College", "Advanced Diploma in Electrical Engineering", "2016")
                ),
                certifications = listOf(
                    CertificationItem("National Certificate of Competence (CoC Level IV)", "Ethiopian TVET Agency", "2018")
                ),
                preferredJobTypes = listOf(EmploymentType.FullTime, EmploymentType.Contract),
                expectedSalaryMin = 14000.0,
                expectedSalaryMax = 22000.0,
                preferredLocation = ethiopianCities[0],
                isAvailable = true,
                rating = 4.9,
                reviewCount = 24,
                verificationStatus = VerificationStatus.verified,
                nationalIdNumber = "ET-ID-8829103"
            ),
            WorkerProfile(
                uid = "w_002",
                fullName = "Bethlehem Haile",
                profileImage = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150",
                phoneNumber = "+251 92 345 6789",
                headline = "Full Stack Mobile & Kotlin Developer",
                bio = "Passionate mobile developer building modern Android apps with Compose, Firebase, and payment integrations like Telebirr & CBE Birr.",
                skills = listOf("Android Kotlin", "Jetpack Compose", "Firebase", "REST APIs", "Telebirr API", "UI/UX Design"),
                totalYearsExperience = 4,
                experienceList = listOf(
                    WorkExperience("Android Engineer", "Fintech Startup Addis", 2, "Built digital wallet companion app"),
                    WorkExperience("Junior Developer", "Tech Ethiopia", 2, "Maintained delivery service app")
                ),
                educationList = listOf(
                    EducationItem("Addis Ababa University (AAiT)", "BSc in Computer Science", "2021")
                ),
                certifications = listOf(
                    CertificationItem("Google Associate Android Developer", "Google Developers", "2022")
                ),
                preferredJobTypes = listOf(EmploymentType.FullTime, EmploymentType.Freelance),
                expectedSalaryMin = 25000.0,
                expectedSalaryMax = 45000.0,
                preferredLocation = ethiopianCities[1],
                isAvailable = true,
                rating = 5.0,
                reviewCount = 18,
                verificationStatus = VerificationStatus.verified,
                nationalIdNumber = "ET-ID-4491023"
            ),
            WorkerProfile(
                uid = "w_003",
                fullName = "Dawit Bekele",
                profileImage = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
                phoneNumber = "+251 93 987 6543",
                headline = "Licensed Heavy Vehicle Driver & Fleet Operator",
                bio = "Grade 5 Public & Cargo driving license. Experienced in long-distance logistics (Djibouti Corridor, Hawassa Industrial Park). Clean driving record.",
                skills = listOf("Heavy Truck Driving", "Logistics", "Vehicle Maintenance", "GPS Navigation", "Cargo Securing"),
                totalYearsExperience = 7,
                experienceList = listOf(
                    WorkExperience("Freight Driver", "Ethio-Djibouti Logistics", 4, "Cross-border container haulage"),
                    WorkExperience("Commercial Driver", "BGI Ethiopia", 3, "Regional distribution")
                ),
                educationList = listOf(
                    EducationItem("Addis Ababa Transport College", "Commercial Driving Certificate", "2017")
                ),
                certifications = listOf(
                    CertificationItem("Grade 5 Driving License", "Federal Transport Authority", "2017")
                ),
                preferredJobTypes = listOf(EmploymentType.FullTime, EmploymentType.Contract),
                expectedSalaryMin = 16000.0,
                expectedSalaryMax = 26000.0,
                preferredLocation = ethiopianCities[3],
                isAvailable = true,
                rating = 4.7,
                reviewCount = 15,
                verificationStatus = VerificationStatus.verified,
                nationalIdNumber = "ET-ID-9921045"
            ),
            WorkerProfile(
                uid = "w_004",
                fullName = "Selamawit Girma",
                profileImage = "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150",
                phoneNumber = "+251 94 555 1234",
                headline = "Executive Chef & Hotel Operations Specialist",
                bio = "Graduate of Lion Hotel & Tourism Institute. Expert in Ethiopian and International culinary dishes, banquet catering, kitchen inventory and hygiene.",
                skills = listOf("Culinary Arts", "Menu Planning", "Food Safety (HACCP)", "Catering", "Baking", "Team Leadership"),
                totalYearsExperience = 6,
                experienceList = listOf(
                    WorkExperience("Sous Chef", "Skylight Hotel Addis", 3, "Supervised hot kitchen line for 400+ seat banquet hall"),
                    WorkExperience("Chef de Partie", "Haile Resort Hawassa", 3, "Prepared fine dining international meals")
                ),
                educationList = listOf(
                    EducationItem("Catering and Tourism Training Institute (CTTI)", "Diploma in Food Preparation", "2018")
                ),
                certifications = listOf(
                    CertificationItem("ServSafe Food Manager", "National Restaurant Association", "2020")
                ),
                preferredJobTypes = listOf(EmploymentType.FullTime, EmploymentType.Contract),
                expectedSalaryMin = 18000.0,
                expectedSalaryMax = 30000.0,
                preferredLocation = ethiopianCities[4],
                isAvailable = true,
                rating = 4.85,
                reviewCount = 20,
                verificationStatus = VerificationStatus.verified,
                nationalIdNumber = "ET-ID-1102948"
            ),
            WorkerProfile(
                uid = "w_005",
                fullName = "Yohannes Mengistu",
                profileImage = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150",
                phoneNumber = "+251 91 777 8899",
                headline = "Expert Carpenter & Architectural Woodworker",
                bio = "Specializing in custom kitchen cabinetry, office furniture, doors, parquet flooring, and modern gypsum finishes. 9 years field experience.",
                skills = listOf("Carpentry", "Wood Finishing", "Furniture Design", "Parquet Flooring", "Interior Fitting"),
                totalYearsExperience = 9,
                experienceList = listOf(
                    WorkExperience("Lead Carpenter", "Nova Wood Works", 5, "Fitted out luxury apartments in Bole & Kazanchis"),
                    WorkExperience("Furniture Craftsman", "Wood Art Ethiopia", 4, "Custom oak & mahogany furniture production")
                ),
                preferredJobTypes = listOf(EmploymentType.Contract, EmploymentType.DailyWork),
                expectedSalaryMin = 12000.0,
                expectedSalaryMax = 20000.0,
                preferredLocation = ethiopianCities[0],
                isAvailable = true,
                rating = 4.75,
                reviewCount = 14,
                verificationStatus = VerificationStatus.verified,
                nationalIdNumber = "ET-ID-5582914"
            )
        )
        _workers.value = sampleWorkers

        // Sample Employers
        val sampleEmployers = listOf(
            EmployerProfile(
                uid = "emp_001",
                companyName = "Abyssinia Construction & Real Estate",
                logoUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb18086f6?w=150",
                industry = "Construction & Infrastructure",
                description = "Leading tier-1 contractor managing commercial high-rises and residential developments across Addis Ababa and Hawassa.",
                contactPhone = "+251 11 661 2233",
                contactEmail = "hr@abyssiniaconstruction.et",
                website = "www.abyssiniaconstruction.et",
                location = ethiopianCities[0],
                businessRegistrationNumber = "ET-MOC-2015-9921",
                verificationStatus = VerificationStatus.verified,
                rating = 4.9,
                reviewCount = 32,
                totalJobsPosted = 12,
                activeJobsCount = 4
            ),
            EmployerProfile(
                uid = "emp_002",
                companyName = "EthioTech Solutions PLC",
                logoUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=150",
                industry = "Information Technology",
                description = "Software innovation hub providing enterprise cloud, mobile fintech solutions, and government digital services.",
                contactPhone = "+251 11 550 4455",
                contactEmail = "careers@ethiotech.com",
                website = "www.ethiotech.com",
                location = ethiopianCities[1],
                businessRegistrationNumber = "ET-MOT-2019-1124",
                verificationStatus = VerificationStatus.verified,
                rating = 4.8,
                reviewCount = 22,
                totalJobsPosted = 8,
                activeJobsCount = 3
            ),
            EmployerProfile(
                uid = "emp_003",
                companyName = "Rift Valley Logistics & Transport",
                logoUrl = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=150",
                industry = "Logistics & Supply Chain",
                description = "Fleet management and freight forwarding connecting Addis Ababa with Djibouti, Modjo Dry Port, and Hawassa Industrial Park.",
                contactPhone = "+251 22 111 8877",
                contactEmail = "jobs@riftvalleylogistics.et",
                website = "www.riftvalleylogistics.et",
                location = ethiopianCities[5], // Adama
                businessRegistrationNumber = "ET-MOT-2018-7741",
                verificationStatus = VerificationStatus.verified,
                rating = 4.7,
                reviewCount = 19,
                totalJobsPosted = 6,
                activeJobsCount = 2
            ),
            EmployerProfile(
                uid = "emp_004",
                companyName = "Kuriftu Hospitality & Resorts",
                logoUrl = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=150",
                industry = "Hospitality & Tourism",
                description = "Premier luxury resort collection offering world-class eco-tourism, culinary excellence, and convention services.",
                contactPhone = "+251 11 662 9900",
                contactEmail = "talent@kurifturesorts.com",
                location = ethiopianCities[11], // Bishoftu
                businessRegistrationNumber = "ET-MOT-2012-3321",
                verificationStatus = VerificationStatus.verified,
                rating = 4.95,
                reviewCount = 45,
                totalJobsPosted = 15,
                activeJobsCount = 5
            )
        )
        _employers.value = sampleEmployers

        // Sample Jobs
        val sampleJobs = listOf(
            JobPost(
                jobId = "job_001",
                employerId = "emp_001",
                employerName = "Abyssinia Construction & Real Estate",
                employerLogo = "https://images.unsplash.com/photo-1541888946425-d0fbb18086f6?w=150",
                employerRating = 4.9,
                title = "Senior Site Electrician & Solar Technician",
                description = "We are seeking a seasoned commercial electrician for our 18-story mixed-use tower in Bole Atlas. Responsibilities include 3-phase wiring, solar backup integration, and elevator power connections.",
                category = "Electrical",
                requiredSkills = listOf("Electrical Wiring", "Solar Installation", "Circuit Design", "Safety Protocols"),
                experienceRequiredYears = 5,
                salaryMin = 16000.0,
                salaryMax = 24000.0,
                salaryType = SalaryType.Monthly,
                employmentType = EmploymentType.FullTime,
                location = ethiopianCities[0],
                numberOfWorkers = 3,
                applicationDeadline = "2026-09-15",
                status = JobStatus.active,
                applicantsCount = 6
            ),
            JobPost(
                jobId = "job_002",
                employerId = "emp_002",
                employerName = "EthioTech Solutions PLC",
                employerLogo = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=150",
                employerRating = 4.8,
                title = "Android Mobile App Engineer (Kotlin / Compose)",
                description = "Join our digital payments team to build high-performance native Android applications. You will work on offline-first transaction features, biometric authentication, and Telebirr wallet payment rails.",
                category = "Software & IT",
                requiredSkills = listOf("Android Kotlin", "Jetpack Compose", "Firebase", "REST APIs"),
                experienceRequiredYears = 3,
                salaryMin = 30000.0,
                salaryMax = 50000.0,
                salaryType = SalaryType.Monthly,
                employmentType = EmploymentType.FullTime,
                location = ethiopianCities[1],
                numberOfWorkers = 2,
                applicationDeadline = "2026-09-20",
                status = JobStatus.active,
                applicantsCount = 11
            ),
            JobPost(
                jobId = "job_003",
                employerId = "emp_003",
                employerName = "Rift Valley Logistics & Transport",
                employerLogo = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=150",
                employerRating = 4.7,
                title = "Heavy Freight Truck Drivers (Addis-Djibouti Route)",
                description = "Urgent opening for 4 certified heavy vehicle drivers. Good mechanical knowledge of Sino Truck / Mercedes Actros, valid Grade 5 license, and clean background check required.",
                category = "Driving & Logistics",
                requiredSkills = listOf("Heavy Truck Driving", "Logistics", "Vehicle Maintenance", "Cargo Securing"),
                experienceRequiredYears = 4,
                salaryMin = 18000.0,
                salaryMax = 28000.0,
                salaryType = SalaryType.Monthly,
                employmentType = EmploymentType.FullTime,
                location = ethiopianCities[5],
                numberOfWorkers = 4,
                applicationDeadline = "2026-09-10",
                status = JobStatus.active,
                applicantsCount = 8
            ),
            JobPost(
                jobId = "job_004",
                employerId = "emp_004",
                employerName = "Kuriftu Hospitality & Resorts",
                employerLogo = "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=150",
                employerRating = 4.95,
                title = "Head Pastry Chef & Culinary Supervisor",
                description = "Seeking an experienced culinary artist to lead our resort bakery and fine dining dessert line. High hygiene standards and ability to train apprentice cooks essential.",
                category = "Hotel & Culinary",
                requiredSkills = listOf("Culinary Arts", "Menu Planning", "Baking", "Food Safety (HACCP)"),
                experienceRequiredYears = 4,
                salaryMin = 20000.0,
                salaryMax = 32000.0,
                salaryType = SalaryType.Monthly,
                employmentType = EmploymentType.FullTime,
                location = ethiopianCities[11],
                numberOfWorkers = 1,
                applicationDeadline = "2026-09-30",
                status = JobStatus.active,
                applicantsCount = 4
            ),
            JobPost(
                jobId = "job_005",
                employerId = "emp_001",
                employerName = "Abyssinia Construction & Real Estate",
                employerLogo = "https://images.unsplash.com/photo-1541888946425-d0fbb18086f6?w=150",
                employerRating = 4.9,
                title = "Finish Carpenter & Gypsum Craftsmen (Contract)",
                description = "High-end interior wood finish work for 24 luxury penthouse units. Must have experience with hardwood floor installation, acoustic panels, and fitted wardrobes.",
                category = "Carpentry & Woodwork",
                requiredSkills = listOf("Carpentry", "Wood Finishing", "Furniture Design", "Parquet Flooring"),
                experienceRequiredYears = 3,
                salaryMin = 14000.0,
                salaryMax = 22000.0,
                salaryType = SalaryType.Monthly,
                employmentType = EmploymentType.Contract,
                location = ethiopianCities[0],
                numberOfWorkers = 5,
                applicationDeadline = "2026-09-18",
                status = JobStatus.active,
                applicantsCount = 7
            ),
            JobPost(
                jobId = "job_006",
                employerId = "emp_001",
                employerName = "Abyssinia Construction & Real Estate",
                employerLogo = "https://images.unsplash.com/photo-1541888946425-d0fbb18086f6?w=150",
                employerRating = 4.9,
                title = "Master Plumber & Pipe Fitter",
                description = "Commercial water supply, drainage networks, solar water heating systems, and water pump pressure calibration for apartment complex.",
                category = "Plumbing",
                requiredSkills = listOf("Plumbing", "Pipe Fitting", "Water Pumps", "Solar Water Heating"),
                experienceRequiredYears = 3,
                salaryMin = 13000.0,
                salaryMax = 20000.0,
                salaryType = SalaryType.Monthly,
                employmentType = EmploymentType.FullTime,
                location = ethiopianCities[0],
                numberOfWorkers = 2,
                applicationDeadline = "2026-09-25",
                status = JobStatus.active,
                applicantsCount = 3
            )
        )
        _jobs.value = sampleJobs

        // Sample Applications
        val sampleApplications = listOf(
            JobApplication(
                applicationId = "app_001",
                jobId = "job_001",
                jobTitle = "Senior Site Electrician & Solar Technician",
                employerId = "emp_001",
                employerName = "Abyssinia Construction & Real Estate",
                workerId = "w_001",
                workerName = "Alemayehu Tadesse",
                workerPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                workerRating = 4.9,
                workerSkills = listOf("Electrical Wiring", "Solar Installation", "Generator Maintenance"),
                status = ApplicationStatus.interview,
                message = "I have 8 years of certified commercial electrical experience in Bole and Hawassa. Available for on-site technical interview immediately.",
                proposedRate = 19000.0,
                interviewDate = "2026-08-22 10:00 AM (Site Office, Bole Atlas)",
                offerDetails = "",
                appliedAt = System.currentTimeMillis() - 86400000L * 2
            ),
            JobApplication(
                applicationId = "app_002",
                jobId = "job_002",
                jobTitle = "Android Mobile App Engineer (Kotlin / Compose)",
                employerId = "emp_002",
                employerName = "EthioTech Solutions PLC",
                workerId = "w_002",
                workerName = "Bethlehem Haile",
                workerPhoto = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150",
                workerRating = 5.0,
                workerSkills = listOf("Android Kotlin", "Jetpack Compose", "Firebase", "REST APIs"),
                status = ApplicationStatus.shortlisted,
                message = "Certified Google Android Developer with strong experience in Compose and payment integrations.",
                proposedRate = 35000.0,
                appliedAt = System.currentTimeMillis() - 86400000L * 3
            ),
            JobApplication(
                applicationId = "app_003",
                jobId = "job_004",
                jobTitle = "Head Pastry Chef & Culinary Supervisor",
                employerId = "emp_004",
                employerName = "Kuriftu Hospitality & Resorts",
                workerId = "w_004",
                workerName = "Selamawit Girma",
                workerPhoto = "https://images.unsplash.com/photo-1580489944761-15a19d654956?w=150",
                workerRating = 4.85,
                workerSkills = listOf("Culinary Arts", "Menu Planning", "Baking"),
                status = ApplicationStatus.accepted,
                message = "CTTI graduate with 6 years experience in luxury resort kitchens.",
                proposedRate = 26000.0,
                offerDetails = "Accepted monthly offer of 28,000 ETB + accommodation at resort",
                appliedAt = System.currentTimeMillis() - 86400000L * 7
            )
        )
        _applications.value = sampleApplications

        // Sample Conversations & Messages
        val sampleConversations = listOf(
            ChatConversation(
                chatId = "chat_001",
                jobId = "job_001",
                jobTitle = "Senior Site Electrician & Solar Technician",
                participantIds = listOf("w_001", "emp_001"),
                workerId = "w_001",
                workerName = "Alemayehu Tadesse",
                workerPhoto = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150",
                employerId = "emp_001",
                employerName = "Abyssinia Construction & Real Estate",
                employerLogo = "https://images.unsplash.com/photo-1541888946425-d0fbb18086f6?w=150",
                lastMessage = "Great! Please bring your CoC Level IV certificates tomorrow at 10 AM.",
                lastMessageTimestamp = System.currentTimeMillis() - 3600000L * 3,
                unreadCount = 1
            ),
            ChatConversation(
                chatId = "chat_002",
                jobId = "job_002",
                jobTitle = "Android Mobile App Engineer (Kotlin / Compose)",
                participantIds = listOf("w_002", "emp_002"),
                workerId = "w_002",
                workerName = "Bethlehem Haile",
                workerPhoto = "https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150",
                employerId = "emp_002",
                employerName = "EthioTech Solutions PLC",
                employerLogo = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=150",
                lastMessage = "We loved your portfolio of Compose applications!",
                lastMessageTimestamp = System.currentTimeMillis() - 3600000L * 8,
                unreadCount = 0
            )
        )
        _conversations.value = sampleConversations

        val sampleMessagesMap = mapOf(
            "chat_001" to listOf(
                ChatMessage("m1", "emp_001", "Abyssinia Construction", UserRole.EMPLOYER, "Hello Alemayehu, we reviewed your application for the Site Electrician position. Impressive background!", "", true, System.currentTimeMillis() - 7200000L),
                ChatMessage("m2", "w_001", "Alemayehu Tadesse", UserRole.WORKER, "Thank you very much! I have worked on similar 3-phase and solar setups.", "", true, System.currentTimeMillis() - 5400000L),
                ChatMessage("m3", "emp_001", "Abyssinia Construction", UserRole.EMPLOYER, "Great! Please bring your CoC Level IV certificates tomorrow at 10 AM.", "", false, System.currentTimeMillis() - 3600000L)
            ),
            "chat_002" to listOf(
                ChatMessage("m4", "w_002", "Bethlehem Haile", UserRole.WORKER, "Hello! Thank you for reviewing my Android development application.", "", true, System.currentTimeMillis() - 10800000L),
                ChatMessage("m5", "emp_002", "EthioTech Solutions", UserRole.EMPLOYER, "We loved your portfolio of Compose applications!", "", true, System.currentTimeMillis() - 8600000L)
            )
        )
        _messages.value = sampleMessagesMap

        // Sample Verifications for Admin Queue
        val sampleVerifications = listOf(
            VerificationItem(
                verificationId = "ver_001",
                userId = "w_003",
                userName = "Dawit Bekele",
                role = UserRole.WORKER,
                documentType = "Grade 5 Commercial Driving License",
                documentNumber = "DRV-ET-2023-8812",
                documentImageUrl = "https://images.unsplash.com/photo-1633409302455-58e18bb992e2?w=300",
                status = VerificationStatus.pending,
                submittedAt = System.currentTimeMillis() - 86400000L
            ),
            VerificationItem(
                verificationId = "ver_002",
                userId = "emp_003",
                userName = "Rift Valley Logistics & Transport",
                role = UserRole.EMPLOYER,
                documentType = "Ministry of Trade Commercial License",
                documentNumber = "ET-MOT-2018-7741",
                documentImageUrl = "https://images.unsplash.com/photo-1450133064473-71024230f91b?w=300",
                status = VerificationStatus.pending,
                submittedAt = System.currentTimeMillis() - 86400000L * 2
            )
        )
        _verifications.value = sampleVerifications

        // Sample Reports
        val sampleReports = listOf(
            ReportItem(
                reportId = "rep_001",
                reporterId = "w_005",
                reporterName = "Yohannes Mengistu",
                reportedUserId = "usr_suspicious_01",
                reportedUserName = "Apex Unregistered Staffing",
                jobId = "job_fake_01",
                jobTitle = "Work in Dubai without visa fee",
                reason = ReportReason.Fraud,
                description = "Account is asking applicants for 5,000 ETB upfront processing fee before interview. Violates marketplace rules.",
                status = ReportStatus.open,
                createdAt = System.currentTimeMillis() - 86400000L
            )
        )
        _reports.value = sampleReports

        // Sample Announcements
        val sampleAnnouncements = listOf(
            AdminAnnouncement(
                announcementId = "ann_001",
                title = "🎉 New Verified Worker Badges & Telebirr Payment Rails",
                body = "All workers can now verify their CoC / National ID credentials directly from their profile for higher employer match ratings.",
                targetRole = "ALL",
                isUrgent = false,
                createdAt = System.currentTimeMillis() - 86400000L * 2
            ),
            AdminAnnouncement(
                announcementId = "ann_002",
                title = "📢 Construction Safety Standards Update in Addis Ababa",
                body = "Employers posting construction and masonry jobs must provide PPE compliance confirmation in accordance with Addis Ababa labor bureau guidelines.",
                targetRole = "EMPLOYER",
                isUrgent = true,
                createdAt = System.currentTimeMillis() - 86400000L * 5
            )
        )
        _announcements.value = sampleAnnouncements

        // Default login as Worker Alemayehu Tadesse
        setDemoUser(UserRole.WORKER)
    }

    // Auth & Role switching
    fun setDemoUser(role: UserRole) {
        when (role) {
            UserRole.WORKER -> {
                val worker = _workers.value.firstOrNull() ?: return
                val user = UserAccount(
                    uid = worker.uid,
                    fullName = worker.fullName,
                    phoneNumber = worker.phoneNumber,
                    email = "alemayehu@ethio-work.et",
                    profileImage = worker.profileImage,
                    role = UserRole.WORKER,
                    status = UserStatus.active,
                    verificationStatus = worker.verificationStatus,
                    location = worker.preferredLocation
                )
                _currentUser.value = user
                _currentWorkerProfile.value = worker
                _currentEmployerProfile.value = null
            }
            UserRole.EMPLOYER -> {
                val emp = _employers.value.firstOrNull() ?: return
                val user = UserAccount(
                    uid = emp.uid,
                    fullName = emp.companyName,
                    phoneNumber = emp.contactPhone,
                    email = emp.contactEmail,
                    profileImage = emp.logoUrl,
                    role = UserRole.EMPLOYER,
                    status = UserStatus.active,
                    verificationStatus = emp.verificationStatus,
                    location = emp.location
                )
                _currentUser.value = user
                _currentEmployerProfile.value = emp
                _currentWorkerProfile.value = null
            }
            UserRole.ADMIN -> {
                val user = UserAccount(
                    uid = "admin_001",
                    fullName = "National Marketplace Administrator",
                    phoneNumber = "+251 11 123 0000",
                    email = "admin@marketplace.gov.et",
                    profileImage = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150",
                    role = UserRole.ADMIN,
                    status = UserStatus.active,
                    verificationStatus = VerificationStatus.verified,
                    location = ethiopianCities[0]
                )
                _currentUser.value = user
                _currentWorkerProfile.value = null
                _currentEmployerProfile.value = null
            }
        }
    }

    fun login(email: String, role: UserRole) {
        setDemoUser(role)
    }

    fun register(fullName: String, email: String, phone: String, role: UserRole) {
        val uid = "usr_" + UUID.randomUUID().toString().take(8)
        val user = UserAccount(
            uid = uid,
            fullName = fullName,
            email = email,
            phoneNumber = phone,
            role = role,
            status = UserStatus.active,
            verificationStatus = VerificationStatus.pending
        )
        _currentUser.value = user
        if (role == UserRole.WORKER) {
            val newWorker = WorkerProfile(
                uid = uid,
                fullName = fullName,
                phoneNumber = phone,
                headline = "General Specialist",
                skills = listOf("General Work", "Communication"),
                verificationStatus = VerificationStatus.pending
            )
            _workers.value = listOf(newWorker) + _workers.value
            _currentWorkerProfile.value = newWorker
        } else if (role == UserRole.EMPLOYER) {
            val newEmp = EmployerProfile(
                uid = uid,
                companyName = fullName,
                contactEmail = email,
                contactPhone = phone,
                verificationStatus = VerificationStatus.pending
            )
            _employers.value = listOf(newEmp) + _employers.value
            _currentEmployerProfile.value = newEmp
        }
    }

    fun logout() {
        _currentUser.value = null
        _currentWorkerProfile.value = null
        _currentEmployerProfile.value = null
    }

    // Worker operations
    fun updateWorkerProfile(updated: WorkerProfile) {
        _currentWorkerProfile.value = updated
        _workers.value = _workers.value.map { if (it.uid == updated.uid) updated else it }
    }

    fun toggleWorkerAvailability(isAvailable: Boolean) {
        val current = _currentWorkerProfile.value ?: return
        val updated = current.copy(isAvailable = isAvailable)
        updateWorkerProfile(updated)
    }

    fun applyForJob(jobId: String, message: String, proposedRate: Double) {
        val user = _currentUser.value ?: return
        val worker = _currentWorkerProfile.value ?: return
        val job = _jobs.value.firstOrNull { it.jobId == jobId } ?: return

        val newApp = JobApplication(
            applicationId = "app_" + UUID.randomUUID().toString().take(8),
            jobId = job.jobId,
            jobTitle = job.title,
            employerId = job.employerId,
            employerName = job.employerName,
            workerId = user.uid,
            workerName = user.fullName,
            workerPhoto = user.profileImage,
            workerRating = worker.rating,
            workerSkills = worker.skills,
            status = ApplicationStatus.pending,
            message = message,
            proposedRate = if (proposedRate > 0) proposedRate else job.salaryMin,
            appliedAt = System.currentTimeMillis()
        )

        _applications.value = listOf(newApp) + _applications.value

        // Increase applicants count
        _jobs.value = _jobs.value.map {
            if (it.jobId == jobId) it.copy(applicantsCount = it.applicantsCount + 1) else it
        }
    }

    fun cancelApplication(applicationId: String) {
        _applications.value = _applications.value.map {
            if (it.applicationId == applicationId) it.copy(status = ApplicationStatus.cancelled) else it
        }
    }

    // Employer operations
    fun postJob(job: JobPost) {
        val newJob = job.copy(
            jobId = if (job.jobId.isEmpty()) "job_" + UUID.randomUUID().toString().take(8) else job.jobId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        _jobs.value = listOf(newJob) + _jobs.value
    }

    fun updateJob(updated: JobPost) {
        _jobs.value = _jobs.value.map { if (it.jobId == updated.jobId) updated.copy(updatedAt = System.currentTimeMillis()) else it }
    }

    fun deleteJob(jobId: String) {
        _jobs.value = _jobs.value.filterNot { it.jobId == jobId }
    }

    fun updateJobStatus(jobId: String, status: JobStatus) {
        _jobs.value = _jobs.value.map { if (it.jobId == jobId) it.copy(status = status) else it }
    }

    fun updateApplicationStatus(applicationId: String, status: ApplicationStatus, interviewNote: String = "", offerNote: String = "") {
        _applications.value = _applications.value.map {
            if (it.applicationId == applicationId) {
                it.copy(
                    status = status,
                    interviewDate = if (interviewNote.isNotEmpty()) interviewNote else it.interviewDate,
                    offerDetails = if (offerNote.isNotEmpty()) offerNote else it.offerDetails,
                    updatedAt = System.currentTimeMillis()
                )
            } else it
        }
    }

    // Chat operations
    fun getOrCreateChat(workerId: String, employerId: String, jobId: String, jobTitle: String): String {
        val existing = _conversations.value.firstOrNull {
            it.workerId == workerId && it.employerId == employerId
        }
        if (existing != null) return existing.chatId

        val worker = _workers.value.firstOrNull { it.uid == workerId }
        val employer = _employers.value.firstOrNull { it.uid == employerId }

        val newChatId = "chat_" + UUID.randomUUID().toString().take(8)
        val newConv = ChatConversation(
            chatId = newChatId,
            jobId = jobId,
            jobTitle = jobTitle,
            participantIds = listOf(workerId, employerId),
            workerId = workerId,
            workerName = worker?.fullName ?: "Worker",
            workerPhoto = worker?.profileImage ?: "",
            employerId = employerId,
            employerName = employer?.companyName ?: "Employer",
            employerLogo = employer?.logoUrl ?: "",
            lastMessage = "Conversation started",
            lastMessageTimestamp = System.currentTimeMillis(),
            unreadCount = 0
        )
        _conversations.value = listOf(newConv) + _conversations.value
        _messages.value = _messages.value + (newChatId to emptyList())
        return newChatId
    }

    fun sendMessage(chatId: String, text: String, imageUrl: String = "") {
        val user = _currentUser.value ?: return
        val msg = ChatMessage(
            messageId = "msg_" + UUID.randomUUID().toString().take(8),
            senderId = user.uid,
            senderName = user.fullName,
            senderRole = user.role,
            text = text,
            imageUrl = imageUrl,
            isRead = false,
            timestamp = System.currentTimeMillis()
        )

        val currentList = _messages.value[chatId] ?: emptyList()
        _messages.value = _messages.value + (chatId to (currentList + msg))

        // update conversation
        _conversations.value = _conversations.value.map {
            if (it.chatId == chatId) {
                it.copy(
                    lastMessage = if (text.isNotEmpty()) text else "📷 Photo attachment",
                    lastMessageTimestamp = System.currentTimeMillis()
                )
            } else it
        }
    }

    // Reviews & Ratings
    fun submitReview(review: ReviewRating) {
        val newRev = review.copy(
            ratingId = "rev_" + UUID.randomUUID().toString().take(8),
            createdAt = System.currentTimeMillis()
        )
        _reviews.value = listOf(newRev) + _reviews.value
    }

    fun hasReviewedJob(userId: String, jobId: String): Boolean {
        return _reviews.value.any { it.reviewerId == userId && it.jobId == jobId }
    }

    // Reports
    fun submitReport(report: ReportItem) {
        val newRep = report.copy(
            reportId = "rep_" + UUID.randomUUID().toString().take(8),
            createdAt = System.currentTimeMillis()
        )
        _reports.value = listOf(newRep) + _reports.value
    }

    fun resolveReport(reportId: String, status: ReportStatus, notes: String) {
        _reports.value = _reports.value.map {
            if (it.reportId == reportId) it.copy(status = status, resolutionNotes = notes) else it
        }
    }

    // Verifications
    fun submitVerificationRequest(documentType: String, documentNumber: String, docUrl: String) {
        val user = _currentUser.value ?: return
        val item = VerificationItem(
            verificationId = "ver_" + UUID.randomUUID().toString().take(8),
            userId = user.uid,
            userName = user.fullName,
            role = user.role,
            documentType = documentType,
            documentNumber = documentNumber,
            documentImageUrl = docUrl,
            status = VerificationStatus.pending,
            submittedAt = System.currentTimeMillis()
        )
        _verifications.value = listOf(item) + _verifications.value
    }

    fun processVerification(verificationId: String, approve: Boolean, reason: String = "") {
        val item = _verifications.value.firstOrNull { it.verificationId == verificationId } ?: return
        val newStatus = if (approve) VerificationStatus.verified else VerificationStatus.rejected

        _verifications.value = _verifications.value.map {
            if (it.verificationId == verificationId) it.copy(status = newStatus, rejectionReason = reason) else it
        }

        // Update in worker/employer list
        if (item.role == UserRole.WORKER) {
            _workers.value = _workers.value.map {
                if (it.uid == item.userId) it.copy(verificationStatus = newStatus) else it
            }
        } else if (item.role == UserRole.EMPLOYER) {
            _employers.value = _employers.value.map {
                if (it.uid == item.userId) it.copy(verificationStatus = newStatus) else it
            }
        }
    }

    // Admin Announcements
    fun postAnnouncement(title: String, body: String, targetRole: String, isUrgent: Boolean) {
        val ann = AdminAnnouncement(
            announcementId = "ann_" + UUID.randomUUID().toString().take(8),
            title = title,
            body = body,
            targetRole = targetRole,
            isUrgent = isUrgent,
            createdAt = System.currentTimeMillis()
        )
        _announcements.value = listOf(ann) + _announcements.value
    }

    fun getCities(): List<GeoLocation> = ethiopianCities
}
