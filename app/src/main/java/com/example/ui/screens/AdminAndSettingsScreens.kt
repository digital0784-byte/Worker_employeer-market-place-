package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MarketplaceViewModel

@Composable
fun AdminDashboardScreen(viewModel: MarketplaceViewModel) {
    val metrics by viewModel.adminMetrics.collectAsState()
    val verifications by viewModel.verifications.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val workers by viewModel.allWorkers.collectAsState()
    val employers by viewModel.allEmployers.collectAsState()
    val announcements by viewModel.announcements.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showBroadcastDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Admin Top Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Admin Control Center",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "National Marketplace Moderation & Oversight",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showBroadcastDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("admin_broadcast_button")
                    ) {
                        Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Broadcast", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Overview Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricStatCard(
                        title = "Workers",
                        value = "${metrics.totalWorkers}",
                        icon = Icons.Default.Engineering,
                        iconColor = PrimaryTeal,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Employers",
                        value = "${metrics.totalEmployers}",
                        icon = Icons.Default.Business,
                        iconColor = AccentAmber,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Hires",
                        value = "${metrics.successfulHires}",
                        icon = Icons.Default.Verified,
                        iconColor = AccentEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Admin Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Verifications (${metrics.pendingVerifications})", fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Reports (${metrics.openReports})", fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Users", fontSize = 11.sp) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Announce", fontSize = 11.sp) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Verifications Queue
                val pending = verifications.filter { it.status == VerificationStatus.pending }
                if (pending.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.CheckCircleOutline,
                        title = "Verification Queue Clear",
                        subtitle = "All worker and employer identity documents have been reviewed."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(pending) { item ->
                            VerificationApprovalCard(
                                item = item,
                                onApprove = { viewModel.processVerification(item.verificationId, true) },
                                onReject = { viewModel.processVerification(item.verificationId, false, "Document image unclear") }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
            1 -> {
                // Safety Reports
                if (reports.isEmpty()) {
                    EmptyStateView(
                        icon = Icons.Default.Shield,
                        title = "No Open Reports",
                        subtitle = "The marketplace is secure and compliant."
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(reports) { rep ->
                            ReportModerationCard(
                                report = rep,
                                onResolve = { viewModel.resolveReport(rep.reportId, ReportStatus.resolved, "Account investigated and warned.") },
                                onDismiss = { viewModel.resolveReport(rep.reportId, ReportStatus.dismissed, "Insufficient evidence.") }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
            2 -> {
                // Users Directory
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text("Registered Workers (${workers.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(workers) { w ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(w.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(w.headline, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                VerificationBadge(status = w.verificationStatus)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Registered Employers (${employers.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(employers) { e ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(e.companyName, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(e.industry, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                VerificationBadge(status = e.verificationStatus)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
            3 -> {
                // Announcements List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(announcements) { ann ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(ann.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Surface(color = PrimaryTeal.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                        Text(ann.targetRole, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal, modifier = Modifier.padding(4.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(ann.body, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showBroadcastDialog) {
        BroadcastAnnouncementDialog(
            viewModel = viewModel,
            onDismiss = { showBroadcastDialog = false }
        )
    }
}

@Composable
fun VerificationApprovalCard(
    item: VerificationItem,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(text = item.userName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Role: ${item.role.name}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
                VerificationBadge(status = item.status)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Document: ${item.documentType}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Number: ${item.documentNumber}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onReject) {
                    Text("Reject", color = AccentRose, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Approve & Verify", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReportModerationCard(
    report: ReportItem,
    onResolve: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Reason: ${report.reason.label}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AccentRose)
                Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(4.dp)) {
                    Text(text = report.status.name.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309), modifier = Modifier.padding(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Reported User: ${report.reportedUserName}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Reported By: ${report.reporterName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "\"${report.description}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(6.dp)) {
                    Text("Dismiss", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onResolve,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentEmerald),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Resolve & Warn", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun BroadcastAnnouncementDialog(
    viewModel: MarketplaceViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var targetRole by remember { mutableStateOf("ALL") }
    var isUrgent by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Broadcast Announcement", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = body,
                    onValueChange = { body = it },
                    label = { Text("Message Body") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(checked = isUrgent, onCheckedChange = { isUrgent = it })
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mark as High Priority / Urgent Alert", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && body.isNotBlank()) {
                            viewModel.broadcastAnnouncement(title, body, targetRole, isUrgent)
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Send Broadcast to Users", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(viewModel: MarketplaceViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val activeLang by viewModel.currentLanguage.collectAsState()

    val languages = listOf(
        Pair("en", "English"),
        Pair("am", "አማርኛ (Amharic)"),
        Pair("om", "Afaan Oromoo"),
        Pair("ti", "ትግርኛ (Tigrinya)"),
        Pair("so", "Soomaali (Somali)")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 70.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Language Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🌐 " + stringResource(R.string.change_language),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                languages.forEach { (code, name) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.setLanguage(code) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = activeLang == code,
                            onClick = { viewModel.setLanguage(code) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = name, fontSize = 14.sp, fontWeight = if (activeLang == code) FontWeight.Bold else FontWeight.Normal)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Switch Role Quick Tester
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🎭 Quick Role Switcher",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current account: ${currentUser?.fullName} (${currentUser?.role?.name})",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.setRole(UserRole.WORKER) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Worker", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.setRole(UserRole.EMPLOYER) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Employer", fontSize = 12.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.setRole(UserRole.ADMIN) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Admin", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Logout & Safety Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "🛡️ Trust & Security", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• All workers and employers undergo national verification.\n• Identity documents are stored securely with zero public exposure.\n• End-to-end audit logging for labor safety compliance.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRose),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
