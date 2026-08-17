package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel
import com.example.viewmodel.WorkerWithMatch

@Composable
fun EmployerDashboardScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToPostJob: () -> Unit,
    onNavigateToApplicants: () -> Unit,
    onNavigateToWorkers: () -> Unit,
    onStartChat: (workerId: String, employerId: String, jobId: String, jobTitle: String) -> Unit
) {
    val employer by viewModel.currentEmployerProfile.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val allApplications by viewModel.allApplications.collectAsState()
    val activeEmployer = employer ?: EmployerProfile(companyName = "Employer")

    val employerJobs = allJobs.filter { it.employerId == activeEmployer.uid }
    val employerApplications = allApplications.filter { it.employerId == activeEmployer.uid }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Company Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryTeal.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Business, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeEmployer.companyName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = activeEmployer.industry.ifEmpty { "General Enterprise" },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        VerificationBadge(status = activeEmployer.verificationStatus)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    RatingStars(rating = activeEmployer.rating, reviewCount = activeEmployer.reviewCount)
                }
            }
        }

        // Metrics Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Active Jobs",
                        value = "${employerJobs.count { it.status == JobStatus.active }}",
                        icon = Icons.Default.Work,
                        iconColor = PrimaryTeal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Applicants",
                        value = "${employerApplications.size}",
                        icon = Icons.Default.People,
                        iconColor = AccentAmber,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToApplicants() }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Interviews",
                        value = "${employerApplications.count { it.status == ApplicationStatus.interview }}",
                        icon = Icons.Default.Event,
                        iconColor = Color(0xFF7E22CE),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onNavigateToApplicants() }
                    )
                    MetricStatCard(
                        title = "Hired",
                        value = "${employerApplications.count { it.status == ApplicationStatus.accepted }}",
                        icon = Icons.Default.CheckCircle,
                        iconColor = AccentEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Quick Actions Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onNavigateToPostJob,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("employer_post_job_action_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Post New Job", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onNavigateToWorkers,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Find Candidates", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Recent Applicants Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Applicants",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = onNavigateToApplicants) {
                    Text("View All", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (employerApplications.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.GroupAdd,
                    title = "No applicants yet",
                    subtitle = "Post a job opening to start receiving applications from verified Ethiopian workers."
                )
            }
        } else {
            items(employerApplications.take(4)) { app ->
                EmployerApplicantCard(
                    application = app,
                    viewModel = viewModel,
                    onChat = { onStartChat(app.workerId, app.employerId, app.jobId, app.jobTitle) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun EmployerJobsScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToApplicants: () -> Unit
) {
    val employer by viewModel.currentEmployerProfile.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()
    val activeEmployer = employer ?: EmployerProfile()

    val employerJobs = allJobs.filter { it.employerId == activeEmployer.uid }
    var showPostJobDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Posted Jobs (${employerJobs.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = { showPostJobDialog = true },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("open_post_job_dialog_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Post Job", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (employerJobs.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.WorkOutline,
                title = "No Posted Jobs",
                subtitle = "Click 'Post Job' to publish your job openings and recruit top talent."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(employerJobs) { job ->
                    EmployerJobManagementCard(
                        job = job,
                        onPause = { viewModel.updateJobStatus(job.jobId, JobStatus.paused) },
                        onResume = { viewModel.updateJobStatus(job.jobId, JobStatus.active) },
                        onClose = { viewModel.updateJobStatus(job.jobId, JobStatus.closed) },
                        onDelete = { viewModel.deleteJob(job.jobId) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }

    if (showPostJobDialog) {
        PostJobDialog(
            viewModel = viewModel,
            onDismiss = { showPostJobDialog = false }
        )
    }
}

@Composable
fun EmployerJobManagementCard(
    job: JobPost,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("employer_job_card_${job.jobId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${job.category} • ${job.employmentType.label}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                JobStatusBadge(status = job.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${job.salaryMin.toInt()} - ${job.salaryMax.toInt()} ETB ${job.salaryType.label} • 📍 ${job.location.cityName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👥 ${job.applicantsCount} Applicants",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (job.status == JobStatus.active) {
                        OutlinedButton(
                            onClick = onPause,
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Pause", fontSize = 11.sp)
                        }
                    } else if (job.status == JobStatus.paused) {
                        Button(
                            onClick = onResume,
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Resume", fontSize = 11.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = onClose,
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Close", fontSize = 11.sp)
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = AccentRose, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PostJobDialog(
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electrical") }
    var skillsInput by remember { mutableStateOf("Electrical Wiring, Safety") }
    var expYears by remember { mutableStateOf("2") }
    var salaryMin by remember { mutableStateOf("12000") }
    var salaryMax by remember { mutableStateOf("20000") }
    var salaryType by remember { mutableStateOf(SalaryType.Monthly) }
    var employmentType by remember { mutableStateOf(EmploymentType.FullTime) }
    var numberOfWorkers by remember { mutableStateOf("1") }
    var deadline by remember { mutableStateOf("2026-09-30") }

    val cities = viewModel.getCities()
    var selectedCity by remember { mutableStateOf(cities.first()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Post New Job Opening", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Job Title (e.g. Master Electrician)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_job_title_input")
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Electrical, Driving, IT)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = skillsInput,
                    onValueChange = { skillsInput = it },
                    label = { Text("Required Skills (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = salaryMin,
                        onValueChange = { salaryMin = it },
                        label = { Text("Min Salary (ETB)") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = salaryMax,
                        onValueChange = { salaryMax = it },
                        label = { Text("Max Salary (ETB)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = expYears,
                    onValueChange = { expYears = it },
                    label = { Text("Min Experience (Years)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = numberOfWorkers,
                    onValueChange = { numberOfWorkers = it },
                    label = { Text("Number of Workers Needed") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Job Description & Responsibilities") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val skills = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            viewModel.postJob(
                                title = title,
                                description = description,
                                category = category,
                                skills = skills,
                                experienceYears = expYears.toIntOrNull() ?: 1,
                                salaryMin = salaryMin.toDoubleOrNull() ?: 5000.0,
                                salaryMax = salaryMax.toDoubleOrNull() ?: 15000.0,
                                salaryType = salaryType,
                                employmentType = employmentType,
                                location = selectedCity,
                                numberOfWorkers = numberOfWorkers.toIntOrNull() ?: 1,
                                deadline = deadline
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("submit_post_job_button")
                ) {
                    Text("Publish Job Opening", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmployerWorkersScreen(
    viewModel: MarketplaceViewModel,
    onStartChat: (workerId: String, employerId: String, jobId: String, jobTitle: String) -> Unit
) {
    val candidateWorkers by viewModel.matchedWorkersForEmployer.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedWorkerDetail by remember { mutableStateOf<WorkerProfile?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search candidates by skill, name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text(
            text = "Matched Candidates (${candidateWorkers.size})",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(candidateWorkers) { workerWithMatch ->
                WorkerCandidateCard(
                    workerWithMatch = workerWithMatch,
                    onClick = { selectedWorkerDetail = workerWithMatch.worker },
                    onChat = {
                        val emp = viewModel.currentEmployerProfile.value
                        if (emp != null) {
                            onStartChat(workerWithMatch.worker.uid, emp.uid, "candidate_inquiry", "General Inquiry")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (selectedWorkerDetail != null) {
        CandidateDetailDialog(
            worker = selectedWorkerDetail!!,
            viewModel = viewModel,
            onDismiss = { selectedWorkerDetail = null },
            onChat = {
                val emp = viewModel.currentEmployerProfile.value
                if (emp != null) {
                    onStartChat(selectedWorkerDetail!!.uid, emp.uid, "candidate_inquiry", "General Inquiry")
                    selectedWorkerDetail = null
                }
            }
        )
    }
}

@Composable
fun WorkerCandidateCard(
    workerWithMatch: WorkerWithMatch,
    onClick: () -> Unit,
    onChat: () -> Unit
) {
    val worker = workerWithMatch.worker
    val match = workerWithMatch.matchBreakdown

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
            .testTag("worker_candidate_card_${worker.uid}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PrimaryTeal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = worker.fullName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = worker.headline,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                MatchScoreBadge(score = match.overallScore)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                worker.skills.take(4).forEach { skill ->
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = skill,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStars(rating = worker.rating, reviewCount = worker.reviewCount)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${worker.totalYearsExperience} yrs exp",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onChat,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Message", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CandidateDetailDialog(
    worker: WorkerProfile,
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit,
    onChat: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Candidate Profile", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = worker.fullName, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = worker.headline, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(6.dp))
                VerificationBadge(status = worker.verificationStatus)

                Spacer(modifier = Modifier.height(12.dp))
                RatingStars(rating = worker.rating, reviewCount = worker.reviewCount)

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Bio", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(text = worker.bio.ifEmpty { "Experienced professional ready for work." }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Skills", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    worker.skills.forEach { s ->
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(6.dp)) {
                            Text(text = s, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                DetailItemRow("Expected Salary", "${worker.expectedSalaryMin.toInt()} - ${worker.expectedSalaryMax.toInt()} ETB ${worker.salaryType.label}")
                DetailItemRow("Location", "${worker.preferredLocation.cityName}, ${worker.preferredLocation.specificArea}")
                DetailItemRow("Phone", worker.phoneNumber)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onChat,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Initiate Chat / Interview", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EmployerApplicantsScreen(
    viewModel: MarketplaceViewModel,
    onStartChat: (workerId: String, employerId: String, jobId: String, jobTitle: String) -> Unit
) {
    val employer by viewModel.currentEmployerProfile.collectAsState()
    val allApplications by viewModel.allApplications.collectAsState()
    val activeEmployer = employer ?: EmployerProfile()

    val employerApps = allApplications.filter { it.employerId == activeEmployer.uid }

    var selectedStatusFilter by remember { mutableStateOf<ApplicationStatus?>(null) }

    val filteredApps = if (selectedStatusFilter == null) {
        employerApps
    } else {
        employerApps.filter { it.status == selectedStatusFilter }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Applicant Pipeline (${employerApps.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Filter chips for pipeline status
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("All (${employerApps.size})", fontSize = 11.sp) }
                )
            }
            items(ApplicationStatus.values()) { status ->
                val count = employerApps.count { it.status == status }
                FilterChip(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = status },
                    label = { Text("${status.label} ($count)", fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredApps.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.PeopleOutline,
                title = "No applicants in this stage",
                subtitle = "Applicants will appear here as they apply for your posted jobs."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredApps) { app ->
                    EmployerApplicantCard(
                        application = app,
                        viewModel = viewModel,
                        onChat = { onStartChat(app.workerId, app.employerId, app.jobId, app.jobTitle) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun EmployerApplicantCard(
    application: JobApplication,
    viewModel: MarketplaceViewModel,
    onChat: () -> Unit
) {
    var showInterviewDialog by remember { mutableStateOf(false) }
    var showOfferDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("applicant_card_${application.applicationId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PrimaryTeal.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = application.workerName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Applied for: ${application.jobTitle}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                ApplicationStatusBadge(status = application.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (application.message.isNotEmpty()) {
                Text(
                    text = "\"${application.message}\"",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = "Proposed Rate: ${application.proposedRate.toInt()} ETB",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pipeline Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (application.status == ApplicationStatus.pending) {
                    OutlinedButton(
                        onClick = { viewModel.updateApplicantStatus(application.applicationId, ApplicationStatus.shortlisted) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Shortlist", fontSize = 11.sp)
                    }
                }

                if (application.status == ApplicationStatus.pending || application.status == ApplicationStatus.shortlisted) {
                    Button(
                        onClick = { showInterviewDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Interview", fontSize = 11.sp)
                    }
                }

                if (application.status == ApplicationStatus.interview) {
                    Button(
                        onClick = { showOfferDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                    ) {
                        Text("Send Offer", fontSize = 11.sp)
                    }
                }

                OutlinedButton(
                    onClick = onChat,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Chat", fontSize = 11.sp)
                }

                if (application.status != ApplicationStatus.rejected && application.status != ApplicationStatus.accepted) {
                    TextButton(
                        onClick = { viewModel.updateApplicantStatus(application.applicationId, ApplicationStatus.rejected) },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("Reject", color = AccentRose, fontSize = 11.sp)
                    }
                }
            }
        }
    }

    if (showInterviewDialog) {
        var interviewScheduleNote by remember { mutableStateOf("Tomorrow at 10:00 AM at Bole Atlas Site Office") }
        Dialog(onDismissRequest = { showInterviewDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Schedule Interview", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = interviewScheduleNote,
                        onValueChange = { interviewScheduleNote = it },
                        label = { Text("Date, Time & Meeting Location") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            viewModel.updateApplicantStatus(application.applicationId, ApplicationStatus.interview, interviewNote = interviewScheduleNote)
                            showInterviewDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm Interview Schedule")
                    }
                }
            }
        }
    }

    if (showOfferDialog) {
        var offerTerms by remember { mutableStateOf("Monthly base: 22,000 ETB + health insurance & lunch allowance") }
        Dialog(onDismissRequest = { showOfferDialog = false }) {
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Send Formal Job Offer", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = offerTerms,
                        onValueChange = { offerTerms = it },
                        label = { Text("Offer Salary & Contract Details") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = {
                            viewModel.updateApplicantStatus(application.applicationId, ApplicationStatus.accepted, offerNote = offerTerms)
                            showOfferDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald)
                    ) {
                        Text("Send & Confirm Hire")
                    }
                }
            }
        }
    }
}
