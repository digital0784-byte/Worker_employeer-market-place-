package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JobPost
import com.example.model.UserRole
import com.example.ui.components.OfflineBanner
import com.example.ui.screens.*
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryTeal
import com.example.viewmodel.MarketplaceViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MarketplaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainMarketplaceApp(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab(val label: String, val icon: ImageVector) {
    WORKER_HOME("Matches", Icons.Default.Home),
    WORKER_JOBS("Jobs", Icons.Default.WorkOutline),
    WORKER_APPLICATIONS("Applications", Icons.Default.AssignmentTurnedIn),
    MAP("Radar", Icons.Default.LocationOn),
    MESSAGES("Messages", Icons.Default.Chat),
    WORKER_PROFILE("Profile", Icons.Default.Person),

    EMPLOYER_DASHBOARD("Overview", Icons.Default.Dashboard),
    EMPLOYER_JOBS("My Jobs", Icons.Default.Work),
    EMPLOYER_WORKERS("Find Workers", Icons.Default.PersonSearch),
    EMPLOYER_APPLICANTS("Pipeline", Icons.Default.People),

    ADMIN_PANEL("Admin", Icons.Default.AdminPanelSettings),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMarketplaceApp(viewModel: MarketplaceViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val activeChatId by viewModel.activeChatId.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var currentTab by remember { mutableStateOf(NavigationTab.WORKER_HOME) }
    var selectedJobForDetail by remember { mutableStateOf<JobPost?>(null) }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Adjust tab if role changes
    LaunchedEffect(currentUser?.role) {
        currentTab = when (currentUser?.role) {
            UserRole.WORKER -> NavigationTab.WORKER_HOME
            UserRole.EMPLOYER -> NavigationTab.EMPLOYER_DASHBOARD
            UserRole.ADMIN -> NavigationTab.ADMIN_PANEL
            null -> NavigationTab.WORKER_HOME
        }
    }

    if (currentUser == null) {
        AuthScreen(viewModel = viewModel)
        return
    }

    val role = currentUser?.role ?: UserRole.WORKER

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = PrimaryTeal,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "🇪🇹 " + stringResource(R.string.app_name),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = when (role) {
                                UserRole.WORKER -> Color(0xFFDCFCE7)
                                UserRole.EMPLOYER -> Color(0xFFFEF3C7)
                                UserRole.ADMIN -> Color(0xFFF3E8FF)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = role.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (role) {
                                    UserRole.WORKER -> Color(0xFF15803D)
                                    UserRole.EMPLOYER -> Color(0xFFB45309)
                                    UserRole.ADMIN -> Color(0xFF7E22CE)
                                },
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { currentTab = NavigationTab.MESSAGES },
                        modifier = Modifier.testTag("top_messages_button")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = "Messages")
                    }
                    IconButton(
                        onClick = { currentTab = NavigationTab.SETTINGS },
                        modifier = Modifier.testTag("top_settings_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                when (role) {
                    UserRole.WORKER -> {
                        listOf(
                            NavigationTab.WORKER_HOME,
                            NavigationTab.WORKER_JOBS,
                            NavigationTab.WORKER_APPLICATIONS,
                            NavigationTab.MAP,
                            NavigationTab.WORKER_PROFILE
                        ).forEach { tab ->
                            NavigationBarItem(
                                selected = currentTab == tab,
                                onClick = { currentTab = tab },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label, fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_tab_${tab.name}")
                            )
                        }
                    }
                    UserRole.EMPLOYER -> {
                        listOf(
                            NavigationTab.EMPLOYER_DASHBOARD,
                            NavigationTab.EMPLOYER_JOBS,
                            NavigationTab.EMPLOYER_WORKERS,
                            NavigationTab.EMPLOYER_APPLICANTS,
                            NavigationTab.MAP
                        ).forEach { tab ->
                            NavigationBarItem(
                                selected = currentTab == tab,
                                onClick = { currentTab = tab },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label, fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_tab_${tab.name}")
                            )
                        }
                    }
                    UserRole.ADMIN -> {
                        listOf(
                            NavigationTab.ADMIN_PANEL,
                            NavigationTab.MAP,
                            NavigationTab.MESSAGES,
                            NavigationTab.SETTINGS
                        ).forEach { tab ->
                            NavigationBarItem(
                                selected = currentTab == tab,
                                onClick = { currentTab = tab },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label, fontSize = 10.sp) },
                                modifier = Modifier.testTag("nav_tab_${tab.name}")
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OfflineBanner(isOnline = isOnline)

            Box(modifier = Modifier.fillMaxSize()) {
                when (currentTab) {
                    NavigationTab.WORKER_HOME -> {
                        WorkerHomeScreen(
                            viewModel = viewModel,
                            onNavigateToJobs = { currentTab = NavigationTab.WORKER_JOBS },
                            onNavigateToApplications = { currentTab = NavigationTab.WORKER_APPLICATIONS },
                            onSelectJob = { selectedJobForDetail = it }
                        )
                    }
                    NavigationTab.WORKER_JOBS -> {
                        WorkerJobsScreen(
                            viewModel = viewModel,
                            onSelectJob = { selectedJobForDetail = it }
                        )
                    }
                    NavigationTab.WORKER_APPLICATIONS -> {
                        WorkerApplicationsScreen(
                            viewModel = viewModel,
                            onStartChat = { wId, eId, jId, title ->
                                viewModel.startChat(wId, eId, jId, title)
                            }
                        )
                    }
                    NavigationTab.WORKER_PROFILE -> {
                        WorkerProfileScreen(viewModel = viewModel)
                    }
                    NavigationTab.EMPLOYER_DASHBOARD -> {
                        EmployerDashboardScreen(
                            viewModel = viewModel,
                            onNavigateToPostJob = { currentTab = NavigationTab.EMPLOYER_JOBS },
                            onNavigateToApplicants = { currentTab = NavigationTab.EMPLOYER_APPLICANTS },
                            onNavigateToWorkers = { currentTab = NavigationTab.EMPLOYER_WORKERS },
                            onStartChat = { wId, eId, jId, title ->
                                viewModel.startChat(wId, eId, jId, title)
                            }
                        )
                    }
                    NavigationTab.EMPLOYER_JOBS -> {
                        EmployerJobsScreen(
                            viewModel = viewModel,
                            onNavigateToApplicants = { currentTab = NavigationTab.EMPLOYER_APPLICANTS }
                        )
                    }
                    NavigationTab.EMPLOYER_WORKERS -> {
                        EmployerWorkersScreen(
                            viewModel = viewModel,
                            onStartChat = { wId, eId, jId, title ->
                                viewModel.startChat(wId, eId, jId, title)
                            }
                        )
                    }
                    NavigationTab.EMPLOYER_APPLICANTS -> {
                        EmployerApplicantsScreen(
                            viewModel = viewModel,
                            onStartChat = { wId, eId, jId, title ->
                                viewModel.startChat(wId, eId, jId, title)
                            }
                        )
                    }
                    NavigationTab.MAP -> {
                        MapScreen(
                            viewModel = viewModel,
                            onSelectJob = { selectedJobForDetail = it }
                        )
                    }
                    NavigationTab.MESSAGES -> {
                        ConversationsScreen(
                            viewModel = viewModel,
                            onOpenChat = { viewModel.openChat(it) }
                        )
                    }
                    NavigationTab.ADMIN_PANEL -> {
                        AdminDashboardScreen(viewModel = viewModel)
                    }
                    NavigationTab.SETTINGS -> {
                        SettingsScreen(viewModel = viewModel)
                    }
                }

                // Active Chat Room Overlay
                if (activeChatId != null) {
                    ChatRoomScreen(
                        chatId = activeChatId!!,
                        viewModel = viewModel,
                        onBack = { viewModel.closeChat() }
                    )
                }

                // Job Details Bottom Sheet / Dialog
                if (selectedJobForDetail != null) {
                    JobDetailDialog(
                        job = selectedJobForDetail!!,
                        viewModel = viewModel,
                        onDismiss = { selectedJobForDetail = null },
                        onStartChat = { wId, eId, jId, title ->
                            viewModel.startChat(wId, eId, jId, title)
                        }
                    )
                }
            }
        }
    }
}
