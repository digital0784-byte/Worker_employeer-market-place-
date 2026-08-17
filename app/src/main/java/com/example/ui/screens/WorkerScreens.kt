package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.MatchingEngine
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.JobWithMatch
import com.example.viewmodel.MarketplaceViewModel

val ALL_CATEGORIES = listOf(
    "All",
    "Electrical",
    "Software & IT",
    "Driving & Logistics",
    "Hotel & Culinary",
    "Carpentry & Woodwork",
    "Plumbing",
    "Construction",
    "Healthcare & Nursing",
    "Tailoring & Fashion",
    "Agriculture"
)

@Composable
fun WorkerHomeScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToJobs: () -> Unit,
    onNavigateToApplications: () -> Unit,
    onSelectJob: (JobPost) -> Unit
) {
    val worker by viewModel.currentWorkerProfile.collectAsState()
    val matchedJobs by viewModel.matchedJobsFeed.collectAsState()
    val applications by viewModel.allApplications.collectAsState()
    val activeWorker = worker ?: WorkerProfile(fullName = "Worker")

    val workerApplications = applications.filter { it.workerId == activeWorker.uid }
    val topMatches = matchedJobs.take(4)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Profile Header
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryTeal.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Avatar",
                                    tint = PrimaryTeal,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = activeWorker.fullName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = activeWorker.headline.ifEmpty { "Skilled Professional" },
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        VerificationBadge(status = activeWorker.verificationStatus)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RatingStars(rating = activeWorker.rating, reviewCount = activeWorker.reviewCount)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "📍 ${activeWorker.preferredLocation.cityName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Availability Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (activeWorker.isAvailable) "Available" else "Busy",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (activeWorker.isAvailable) PrimaryTeal else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = activeWorker.isAvailable,
                                onCheckedChange = { viewModel.toggleAvailability(it) },
                                modifier = Modifier.testTag("worker_availability_switch")
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricStatCard(
                    title = "Applied",
                    value = "${workerApplications.size}",
                    icon = Icons.Default.Send,
                    iconColor = PrimaryTeal,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToApplications() }
                )
                MetricStatCard(
                    title = "Interviews",
                    value = "${workerApplications.count { it.status == ApplicationStatus.interview }}",
                    icon = Icons.Default.Event,
                    iconColor = AccentAmber,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToApplications() }
                )
                MetricStatCard(
                    title = "Offers",
                    value = "${workerApplications.count { it.status == ApplicationStatus.accepted }}",
                    icon = Icons.Default.CheckCircle,
                    iconColor = AccentEmerald,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigateToApplications() }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Categories Horizontal Bar
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Explore Job Categories",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ALL_CATEGORIES) { cat ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            viewModel.setSelectedCategory(cat)
                            onNavigateToJobs()
                        },
                        label = { Text(cat, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = when (cat) {
                                    "Electrical" -> Icons.Default.Bolt
                                    "Software & IT" -> Icons.Default.Code
                                    "Driving & Logistics" -> Icons.Default.DirectionsCar
                                    "Hotel & Culinary" -> Icons.Default.Restaurant
                                    "Carpentry & Woodwork" -> Icons.Default.Carpenter
                                    "Plumbing" -> Icons.Default.Plumbing
                                    else -> Icons.Default.Work
                                },
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Top Matched Jobs Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.top_matches),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Calculated based on your skills & experience",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                TextButton(onClick = onNavigateToJobs) {
                    Text("View All", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Top Matched Jobs Cards
        items(topMatches) { item ->
            JobCardItem(
                jobWithMatch = item,
                onClick = { onSelectJob(item.job) },
                onApply = { onSelectJob(item.job) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun WorkerJobsScreen(
    viewModel: MarketplaceViewModel,
    onSelectJob: (JobPost) -> Unit
) {
    val matchedJobs by viewModel.matchedJobsFeed.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()

    val cities = listOf("All Cities") + viewModel.getCities().map { it.cityName }.distinct()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search jobs by title, skills, company...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("job_search_field"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Filter chips (Category)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(ALL_CATEGORIES) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { viewModel.setSelectedCategory(cat) },
                    label = { Text(cat, fontSize = 12.sp) }
                )
            }
        }

        // City filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(cities) { city ->
                FilterChip(
                    selected = selectedCity == city,
                    onClick = { viewModel.setSelectedCity(city) },
                    label = { Text("📍 $city", fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (matchedJobs.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.WorkOff,
                title = "No Jobs Found",
                subtitle = "Try adjusting your search query or filters to find more opportunities."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(matchedJobs) { item ->
                    JobCardItem(
                        jobWithMatch = item,
                        onClick = { onSelectJob(item.job) },
                        onApply = { onSelectJob(item.job) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun JobCardItem(
    jobWithMatch: JobWithMatch,
    onClick: () -> Unit,
    onApply: () -> Unit
) {
    val job = jobWithMatch.job
    val match = jobWithMatch.matchBreakdown

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick)
            .testTag("job_card_${job.jobId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Title & Match Score Badge
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
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = job.employerName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                MatchScoreBadge(score = match.overallScore)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details tags (Salary, Location, Employment Type)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${job.salaryMin.toInt()} - ${job.salaryMax.toInt()} ETB ${job.salaryType.label}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "📍 ${job.location.cityName} (${job.location.specificArea})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Skills chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                job.requiredSkills.take(3).forEach { skill ->
                    val isMatched = match.matchedSkills.any { it.equals(skill, ignoreCase = true) }
                    Surface(
                        color = if (isMatched) Color(0xFFCCFBF1) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = skill,
                            fontSize = 10.sp,
                            fontWeight = if (isMatched) FontWeight.Bold else FontWeight.Normal,
                            color = if (isMatched) Color(0xFF0F766E) else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${job.applicantsCount} applicants • ${job.employmentType.label}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onApply,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("apply_job_button_${job.jobId}")
                ) {
                    Text("Details / Apply", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailDialog(
    job: JobPost,
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit,
    onStartChat: (workerId: String, employerId: String, jobId: String, jobTitle: String) -> Unit
) {
    var showApplyForm by remember { mutableStateOf(false) }
    var applicationMessage by remember { mutableStateOf("") }
    var proposedRate by remember { mutableStateOf(job.salaryMin.toString()) }
    var showReportDialog by remember { mutableStateOf(false) }

    val worker by viewModel.currentWorkerProfile.collectAsState()
    val activeWorker = worker ?: WorkerProfile(skills = listOf("General Work"))
    val match = MatchingEngine.calculateMatch(activeWorker, job)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Job Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = job.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = job.employerName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Match Breakdown Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Marketplace Match Engine",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            MatchScoreBadge(score = match.overallScore)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Skills: ${match.skillsScore}%", fontSize = 11.sp)
                            Text("Experience: ${match.experienceScore}%", fontSize = 11.sp)
                            Text("Location: ${match.locationScore}%", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Info Grid
                Text(
                    text = "Position Information",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                DetailItemRow("Category", job.category)
                DetailItemRow("Employment Type", job.employmentType.label)
                DetailItemRow("Salary", "${job.salaryMin.toInt()} - ${job.salaryMax.toInt()} ETB ${job.salaryType.label}")
                DetailItemRow("Location", "${job.location.cityName}, ${job.location.specificArea}")
                DetailItemRow("Experience Required", "${job.experienceRequiredYears}+ years")
                DetailItemRow("Open Positions", "${job.numberOfWorkers} positions")
                DetailItemRow("Application Deadline", job.applicationDeadline)

                Spacer(modifier = Modifier.height(14.dp))

                // Required Skills
                Text(
                    text = "Required Skills",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    job.requiredSkills.forEach { skill ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = skill,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                Text(
                    text = "Description",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = job.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Apply Section
                if (showApplyForm) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Submit Application",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = applicationMessage,
                                onValueChange = { applicationMessage = it },
                                label = { Text("Cover Note / Why you're a good fit") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("apply_cover_note_field"),
                                minLines = 3
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = proposedRate,
                                onValueChange = { proposedRate = it },
                                label = { Text("Proposed Expected Rate (ETB)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    val rate = proposedRate.toDoubleOrNull() ?: job.salaryMin
                                    viewModel.applyForJob(job.jobId, applicationMessage, rate)
                                    onDismiss()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_apply_button")
                            ) {
                                Text("Submit Application", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val workerUser = viewModel.currentUser.value
                                if (workerUser != null) {
                                    onStartChat(workerUser.uid, job.employerId, job.jobId, job.title)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Message", fontSize = 13.sp)
                        }

                        Button(
                            onClick = { showApplyForm = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("open_apply_form_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Apply Now", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Report button
                TextButton(
                    onClick = { showReportDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.Flag, contentDescription = null, tint = AccentRose, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report this job posting", color = AccentRose, fontSize = 12.sp)
                }
            }
        }
    }

    if (showReportDialog) {
        ReportSubmissionDialog(
            reportedUserId = job.employerId,
            reportedUserName = job.employerName,
            jobId = job.jobId,
            jobTitle = job.title,
            viewModel = viewModel,
            onDismiss = { showReportDialog = false }
        )
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun WorkerApplicationsScreen(
    viewModel: MarketplaceViewModel,
    onStartChat: (workerId: String, employerId: String, jobId: String, jobTitle: String) -> Unit
) {
    val applications by viewModel.allApplications.collectAsState()
    val worker by viewModel.currentWorkerProfile.collectAsState()
    val activeWorker = worker ?: WorkerProfile(uid = "w_001")

    val workerApps = applications.filter { it.workerId == activeWorker.uid }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "My Applications (${workerApps.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(16.dp)
        )

        if (workerApps.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.AssignmentLate,
                title = "No Applications Yet",
                subtitle = "Browse jobs and submit applications to track your progress here."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(workerApps) { app ->
                    ApplicationCard(
                        application = app,
                        onCancel = { viewModel.cancelApplication(app.applicationId) },
                        onChat = { onStartChat(app.workerId, app.employerId, app.jobId, app.jobTitle) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun ApplicationCard(
    application: JobApplication,
    onCancel: () -> Unit,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
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
                        text = application.jobTitle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = application.employerName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
                ApplicationStatusBadge(status = application.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (application.interviewDate.isNotEmpty()) {
                Surface(
                    color = Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFF7E22CE), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Interview Scheduled: ${application.interviewDate}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7E22CE)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (application.offerDetails.isNotEmpty()) {
                Surface(
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Celebration, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Offer: ${application.offerDetails}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF15803D)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (application.message.isNotEmpty()) {
                Text(
                    text = "\"${application.message}\"",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (application.status == ApplicationStatus.pending) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel", color = AccentRose, fontSize = 12.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                OutlinedButton(
                    onClick = onChat,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat with Employer", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun WorkerProfileScreen(viewModel: MarketplaceViewModel) {
    val worker by viewModel.currentWorkerProfile.collectAsState()
    val activeWorker = worker ?: WorkerProfile()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showVerifyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Profile Header
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(PrimaryTeal.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryTeal, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = activeWorker.fullName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = activeWorker.headline.ifEmpty { "Skilled Professional" },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                VerificationBadge(status = activeWorker.verificationStatus)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    RatingStars(rating = activeWorker.rating, reviewCount = activeWorker.reviewCount)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "🏆 ${activeWorker.completedJobsCount} Completed Jobs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_worker_profile_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Profile", fontSize = 12.sp)
                    }

                    if (activeWorker.verificationStatus != VerificationStatus.verified) {
                        Button(
                            onClick = { showVerifyDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("request_worker_verification_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Verify ID", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Skills Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Skills & Expertise", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    activeWorker.skills.forEach { skill ->
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = skill,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Work Experience
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Work Experience (${activeWorker.totalYearsExperience} Years)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (activeWorker.experienceList.isEmpty()) {
                    Text(text = "No work experience added yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    activeWorker.experienceList.forEach { exp ->
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text(text = exp.title, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${exp.company} • ${exp.durationYears} years", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                            Text(text = exp.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    }
                }
            }
        }

        // Salary & Location
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Preferences", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                DetailItemRow("Expected Salary", "${activeWorker.expectedSalaryMin.toInt()} - ${activeWorker.expectedSalaryMax.toInt()} ETB ${activeWorker.salaryType.label}")
                DetailItemRow("Preferred City", "${activeWorker.preferredLocation.cityName} (${activeWorker.preferredLocation.specificArea})")
                DetailItemRow("Phone Contact", activeWorker.phoneNumber.ifEmpty { "Not set" })
            }
        }
    }

    if (showEditProfileDialog) {
        EditWorkerProfileDialog(
            worker = activeWorker,
            viewModel = viewModel,
            onDismiss = { showEditProfileDialog = false }
        )
    }

    if (showVerifyDialog) {
        WorkerVerificationDialog(
            viewModel = viewModel,
            onDismiss = { showVerifyDialog = false }
        )
    }
}

@Composable
fun EditWorkerProfileDialog(
    worker: WorkerProfile,
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit
) {
    var headline by remember { mutableStateOf(worker.headline) }
    var bio by remember { mutableStateOf(worker.bio) }
    var skillsInput by remember { mutableStateOf(worker.skills.joinToString(", ")) }
    var expYears by remember { mutableStateOf(worker.totalYearsExperience.toString()) }
    var salaryMin by remember { mutableStateOf(worker.expectedSalaryMin.toString()) }
    var salaryMax by remember { mutableStateOf(worker.expectedSalaryMax.toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Edit Worker Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = headline,
                    onValueChange = { headline = it },
                    label = { Text("Professional Headline") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio / Summary") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = skillsInput,
                    onValueChange = { skillsInput = it },
                    label = { Text("Skills (comma separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = expYears,
                    onValueChange = { expYears = it },
                    label = { Text("Years of Experience") },
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val skillsList = skillsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                        viewModel.updateWorkerProfile(
                            headline = headline,
                            bio = bio,
                            skills = skillsList,
                            experienceYears = expYears.toIntOrNull() ?: 1,
                            salaryMin = salaryMin.toDoubleOrNull() ?: 5000.0,
                            salaryMax = salaryMax.toDoubleOrNull() ?: 15000.0,
                            location = worker.preferredLocation
                        )
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WorkerVerificationDialog(
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit
) {
    var docType by remember { mutableStateOf("Ethiopian National ID (Fayda)") }
    var docNumber by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "Identity Verification", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Submit your official ID to earn the Verified Worker badge and boost employer hiring confidence.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = docType,
                    onValueChange = { docType = it },
                    label = { Text("Document Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = docNumber,
                    onValueChange = { docNumber = it },
                    label = { Text("ID Number / Kebele Card Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (docNumber.isNotBlank()) {
                            viewModel.requestWorkerVerification(docType, docNumber)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit for Verification", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReportSubmissionDialog(
    reportedUserId: String,
    reportedUserName: String,
    jobId: String,
    jobTitle: String,
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit
) {
    var selectedReason by remember { mutableStateOf(ReportReason.FakeJob) }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = "Report Suspicious Activity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AccentRose)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Reporting: $reportedUserName • $jobTitle",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Select Reason:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))

                ReportReason.values().forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = reason.label, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Details / Explanation") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.submitReport(
                            reportedUserId = reportedUserId,
                            reportedUserName = reportedUserName,
                            jobId = jobId,
                            jobTitle = jobTitle,
                            reason = selectedReason,
                            description = description
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRose),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Safety Report", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
