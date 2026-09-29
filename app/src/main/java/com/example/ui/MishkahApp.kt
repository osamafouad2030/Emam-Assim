package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.UserRole
import com.example.i18n.AppLanguage
import com.example.i18n.ArabicStrings
import com.example.i18n.EnglishStrings
import com.example.i18n.LocalAppStrings
import com.example.ui.screens.AdminCmsScreen
import com.example.ui.screens.AnalyticsDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BookAnalyzerScreen
import com.example.ui.screens.CurriculumScreen
import com.example.ui.theme.LevelBeginnerEmerald
import com.example.ui.theme.MishkahGoldPrimary
import com.example.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MishkahApp(viewModel: MishkahViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val users by viewModel.allUsers.collectAsStateWithLifecycle()
    val chapters by viewModel.chapters.collectAsStateWithLifecycle()
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val questions by viewModel.questions.collectAsStateWithLifecycle()
    val allProgress by viewModel.allProgress.collectAsStateWithLifecycle()
    val allAttempts by viewModel.allAttempts.collectAsStateWithLifecycle()
    val allAlerts by viewModel.allAlerts.collectAsStateWithLifecycle()

    val strings = if (uiState.language == AppLanguage.AR) ArabicStrings else EnglishStrings

    CompositionLocalProvider(
        LocalLayoutDirection provides uiState.language.layoutDirection,
        LocalAppStrings provides strings
    ) {
        MyApplicationTheme(darkTheme = uiState.isDarkTheme) {
            val currentUser = uiState.currentUser

            if (currentUser == null) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    AuthScreen(
                        users = users,
                        authMessage = uiState.authStatusMessage,
                        isAuthError = uiState.isAuthError,
                        isDarkTheme = uiState.isDarkTheme,
                        onToggleLanguage = viewModel::toggleLanguage,
                        onToggleTheme = viewModel::toggleTheme,
                        onSignIn = viewModel::signIn,
                        onRegister = viewModel::register,
                        onResetPassword = viewModel::resetPassword,
                        onQuickLogin = viewModel::quickRoleLogin,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            } else {
                // Handle back navigation from secondary tabs to Curriculum home
                BackHandler(enabled = uiState.currentTab != MainTab.CURRICULUM && uiState.activeLessonId == null) {
                    viewModel.selectTab(MainTab.CURRICULUM)
                }

                val roleLabel = when (UserRole.fromCode(currentUser.role)) {
                    UserRole.STUDENT -> strings.roleStudent
                    UserRole.INSTRUCTOR -> strings.roleInstructor
                    UserRole.ADMIN -> strings.roleAdmin
                }

                val navItems = listOf(
                    Triple(MainTab.CURRICULUM, strings.navCurriculum, Icons.Default.MenuBook),
                    Triple(MainTab.ANALYTICS, strings.navAnalytics, Icons.Default.Analytics),
                    Triple(MainTab.BOOK_ANALYZER, strings.navBookAnalyzer, Icons.Default.AutoAwesomeMotion),
                    Triple(MainTab.ADMIN_CMS, strings.navAdminCms, Icons.Default.AdminPanelSettings)
                )

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWideScreen = maxWidth >= 640.dp

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        contentWindowInsets = WindowInsets.safeDrawing,
                        topBar = {
                            TopAppBar(
                                title = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_app_icon),
                                            contentDescription = strings.appName,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .border(1.dp, MishkahGoldPrimary, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = strings.appName,
                                                    style = MaterialTheme.typography.titleLarge,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = roleLabel,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = currentUser.fullName,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                if (currentUser.isEmailVerified) {
                                                    Icon(
                                                        imageVector = Icons.Default.Verified,
                                                        contentDescription = strings.emailVerifiedBadge,
                                                        tint = LevelBeginnerEmerald,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                } else {
                                                    Text(
                                                        text = strings.verifyEmailAction,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.clickable { viewModel.verifyCurrentEmail() }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                },
                                actions = {
                                    // Instant AR/EN Language Toggle
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { viewModel.toggleLanguage() }
                                            .testTag("top_language_toggle_button"),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = "Switch Language",
                                                modifier = Modifier.size(16.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (uiState.language == AppLanguage.AR) "EN" else "عربي",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = viewModel::toggleTheme,
                                        modifier = Modifier.testTag("top_theme_toggle_button")
                                    ) {
                                        Icon(
                                            imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                            contentDescription = "Toggle Theme",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    IconButton(
                                        onClick = viewModel::logout,
                                        modifier = Modifier.testTag("logout_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Logout,
                                            contentDescription = strings.logoutLabel,
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            )
                        },
                        bottomBar = {
                            if (!isWideScreen) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    navItems.forEach { (tab, label, icon) ->
                                        NavigationBarItem(
                                            selected = uiState.currentTab == tab,
                                            onClick = { viewModel.selectTab(tab) },
                                            icon = { Icon(icon, contentDescription = label) },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isWideScreen) {
                                NavigationRail(
                                    modifier = Modifier.fillMaxHeight(),
                                    containerColor = MaterialTheme.colorScheme.surface
                                ) {
                                    navItems.forEach { (tab, label, icon) ->
                                        NavigationRailItem(
                                            selected = uiState.currentTab == tab,
                                            onClick = { viewModel.selectTab(tab) },
                                            icon = { Icon(icon, contentDescription = label) },
                                            label = { Text(label) },
                                            modifier = Modifier.testTag("rail_tab_${tab.name.lowercase()}")
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.fillMaxSize()) {
                                AnimatedVisibility(visible = uiState.statusBannerMessage != null) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                            .clickable { viewModel.clearBanner() },
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = uiState.statusBannerMessage.orEmpty(),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer
                                            )
                                        }
                                    }
                                }

                                val currentUserProgress = allProgress.filter { it.userId == currentUser.id }

                                when (uiState.currentTab) {
                                    MainTab.CURRICULUM -> CurriculumScreen(
                                        chapters = chapters,
                                        lessons = lessons,
                                        questions = questions,
                                        userProgress = currentUserProgress,
                                        selectedLevel = uiState.selectedLevelFilter,
                                        activeLessonId = uiState.activeLessonId,
                                        isTakingQuiz = uiState.isTakingQuiz,
                                        lastQuizSummary = uiState.lastQuizSummary,
                                        onSelectLevel = viewModel::selectLevelFilter,
                                        onOpenLesson = viewModel::openLesson,
                                        onCloseLesson = viewModel::closeLesson,
                                        onStartQuiz = viewModel::startLessonQuiz,
                                        onSubmitQuiz = viewModel::submitQuiz
                                    )

                                    MainTab.ANALYTICS -> AnalyticsDashboardScreen(
                                        currentUser = currentUser,
                                        allUsers = users,
                                        lessons = lessons,
                                        allProgress = allProgress,
                                        allAttempts = allAttempts,
                                        allAlerts = allAlerts,
                                        selectedStudentId = uiState.selectedStudentIdForAnalytics,
                                        onSelectStudent = viewModel::selectStudentForAnalytics,
                                        onResolveAlert = viewModel::resolveAlert
                                    )

                                    MainTab.BOOK_ANALYZER -> BookAnalyzerScreen(
                                        chapters = chapters,
                                        lastResult = uiState.lastAnalyzedBookResult,
                                        onAnalyzeAndImport = viewModel::analyzeAndImportBookText
                                    )

                                    MainTab.ADMIN_CMS -> AdminCmsScreen(
                                        chapters = chapters,
                                        lessons = lessons,
                                        onSaveLesson = viewModel::saveCustomLesson,
                                        onDeleteLesson = viewModel::deleteLesson,
                                        onSaveQuestion = viewModel::saveCustomQuestion
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
