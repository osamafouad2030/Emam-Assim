package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.EducationLevel
import com.example.data.LessonEntity
import com.example.data.LessonProgressEntity
import com.example.data.SkillCategory
import com.example.data.SmartAlertEntity
import com.example.data.StudentAttemptEntity
import com.example.data.UserEntity
import com.example.data.UserRole
import com.example.domain.LevelStatSummary
import com.example.domain.ReportExporter
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppStrings
import com.example.ui.components.CompetencyRadarChart
import com.example.ui.components.ScoreProgressionLineChart
import com.example.ui.components.ThreeLevelBarChart
import com.example.ui.theme.AlertHighCoral
import com.example.ui.theme.AlertMediumAmber
import com.example.ui.theme.LevelBeginnerEmerald
import com.example.ui.theme.MishkahGoldPrimary

@Composable
fun AnalyticsDashboardScreen(
    currentUser: UserEntity?,
    allUsers: List<UserEntity>,
    lessons: List<LessonEntity>,
    allProgress: List<LessonProgressEntity>,
    allAttempts: List<StudentAttemptEntity>,
    allAlerts: List<SmartAlertEntity>,
    selectedStudentId: Int,
    onSelectStudent: (Int) -> Unit,
    onResolveAlert: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isAr = strings.language == AppLanguage.AR
    val context = LocalContext.current

    val students = remember(allUsers) {
        allUsers.filter { it.role == UserRole.STUDENT.code }.ifEmpty { allUsers }
    }
    val targetStudent = students.firstOrNull { it.id == selectedStudentId }
        ?: currentUser
        ?: UserEntity(1, "أحمد المنصور", "student@mishkah.edu", "", "", UserRole.STUDENT.code)

    val studentProgress = remember(allProgress, targetStudent.id) {
        allProgress.filter { it.userId == targetStudent.id }
    }
    val studentAttempts = remember(allAttempts, targetStudent.id) {
        allAttempts.filter { it.userId == targetStudent.id }
    }
    val studentAlerts = remember(allAlerts, targetStudent.id) {
        allAlerts.filter { it.userId == targetStudent.id && !it.isResolved }
    }

    // Compute 3-Level Breakdown
    val levelStats = remember(lessons, studentProgress, studentAttempts) {
        EducationLevel.entries.map { level ->
            val lvlLessons = lessons.filter { it.level == level.code }
            val lvlProg = studentProgress.filter { it.level == level.code }
            val lvlAttempts = studentAttempts.filter { it.level == level.code }

            val comp = if (lvlLessons.isEmpty()) 0 else {
                val sum = lvlLessons.sumOf { l ->
                    lvlProg.firstOrNull { it.lessonId == l.id }?.completionPercent ?: 0
                }
                sum / lvlLessons.size
            }
            val avgScore = if (lvlProg.isEmpty()) 0 else lvlProg.map { it.bestScorePercent }.average().toInt()
            val totalMins = (lvlProg.sumOf { it.totalTimeSeconds } / 60).coerceAtLeast(if (lvlProg.isNotEmpty()) 5 else 0)
            val attCount = lvlProg.sumOf { it.attemptsCount }.coerceAtLeast(lvlAttempts.size)

            LevelStatSummary(
                level = level,
                completionPercent = comp,
                avgScorePercent = avgScore,
                totalMinutes = totalMins,
                attemptsCount = attCount
            )
        }
    }

    // Compute 5-Axis Skill Radar
    val skillScores = remember(studentAttempts, strings) {
        val allSkills = listOf(
            SkillCategory.COMPREHENSION to strings.skillComprehension,
            SkillCategory.ANALYSIS to strings.skillAnalysis,
            SkillCategory.APPLICATION to strings.skillApplication,
            SkillCategory.CRITICAL_THINKING to strings.skillCriticalThinking,
            SkillCategory.DEDUCTION to strings.skillDeduction
        )
        allSkills.map { (cat, label) ->
            val masteredHits = studentAttempts.count { it.masteredSkillTagsCsv.contains(cat.code) }
            val missedHits = studentAttempts.count { it.missedSkillTagsCsv.contains(cat.code) }
            val score = when {
                masteredHits + missedHits == 0 -> 78
                else -> ((masteredHits.toFloat() / (masteredHits + missedHits)) * 100f).toInt().coerceIn(35, 98)
            }
            label to score
        }
    }

    val strengths = remember(skillScores) {
        skillScores.filter { it.second >= 72 }.map { "${it.first} (${it.second}%)" }
            .ifEmpty { listOf("${skillScores.first().first} (85%)") }
    }
    val weaknesses = remember(skillScores) {
        skillScores.filter { it.second < 72 }.map { "${it.first} (${it.second}%)" }
    }

    var reportPreviewText by remember { mutableStateOf<String?>(null) }

    val pdfExporterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            ReportExporter.exportToUri(context, uri, isPdf = true) { stream ->
                ReportExporter.writePdfToStream(
                    outputStream = stream,
                    student = targetStudent,
                    levelStats = levelStats,
                    strengths = strengths,
                    weaknesses = weaknesses,
                    alerts = studentAlerts
                )
            }
        }
    }

    val csvExporterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            val csv = ReportExporter.buildCsvContent(
                student = targetStudent,
                levelStats = levelStats,
                attempts = studentAttempts,
                strengths = strengths,
                weaknesses = weaknesses
            )
            ReportExporter.exportToUri(context, uri, isPdf = false) { stream ->
                stream.write(csv.toByteArray())
            }
        }
    }

    if (reportPreviewText != null) {
        AlertDialog(
            onDismissRequest = { reportPreviewText = null },
            title = { Text(strings.reportPreviewTitle) },
            text = {
                Text(
                    text = reportPreviewText.orEmpty(),
                    style = MaterialTheme.typography.bodySmall
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        reportPreviewText = null
                        pdfExporterLauncher.launch("Mishkah_Report_${targetStudent.id}.pdf")
                    }
                ) {
                    Text(strings.exportPdfBtn)
                }
            },
            dismissButton = {
                TextButton(onClick = { reportPreviewText = null }) {
                    Text(if (isAr) "إغلاق" else "Close")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Export Buttons
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = strings.dashboardHeader,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = strings.dashboardSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            reportPreviewText = ReportExporter.buildCsvContent(
                                student = targetStudent,
                                levelStats = levelStats,
                                attempts = studentAttempts,
                                strengths = strengths,
                                weaknesses = weaknesses
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_pdf_button")
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.exportPdfBtn)
                    }

                    OutlinedButton(
                        onClick = {
                            csvExporterLauncher.launch("Mishkah_Analytics_${targetStudent.id}.csv")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("export_csv_button")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.exportCsvBtn)
                    }
                }
            }
        }

        // Student Switcher (For Instructor & Admin or Comparison)
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    Text(
                        text = strings.selectStudentLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(students, key = { it.id }) { stu ->
                    FilterChip(
                        selected = stu.id == targetStudent.id,
                        onClick = { onSelectStudent(stu.id) },
                        label = { Text(stu.fullName) }
                    )
                }
            }
        }

        // KPI Overview Row
        item {
            val overallCompletion = levelStats.map { it.completionPercent }.average().toInt()
            val overallAvgScore = levelStats.filter { it.avgScorePercent > 0 }.map { it.avgScorePercent }.average().takeIf { !it.isNaN() }?.toInt() ?: 0
            val totalMinutes = levelStats.sumOf { it.totalMinutes }
            val totalAttempts = levelStats.sumOf { it.attemptsCount }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMetricCard(
                    title = strings.overallMasteryLabel,
                    value = "$overallCompletion%",
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = strings.averageScoreLabel,
                    value = "$overallAvgScore%",
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = strings.totalStudyTimeLabel,
                    value = "${totalMinutes}m",
                    modifier = Modifier.weight(1f)
                )
                KpiMetricCard(
                    title = strings.totalAttemptsLabel,
                    value = "$totalAttempts",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Smart Pedagogical Alert System Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MishkahGoldPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.smartAlertsTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    if (studentAlerts.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LevelBeginnerEmerald)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.noAlertsMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        studentAlerts.forEach { alert ->
                            val borderColor = if (alert.severity == "HIGH") AlertHighCoral else AlertMediumAmber
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(borderColor.copy(alpha = 0.12f))
                                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (isAr) alert.titleAr else alert.titleEn,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = borderColor,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isAr) alert.messageAr else alert.messageEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isAr) alert.recommendationAr else alert.recommendationEn,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { onResolveAlert(alert.id) },
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text(strings.resolveAlertBtn)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Chart 1: 3-Level Comparison Bar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = strings.levelComparisonChartTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ThreeLevelBarChart(stats = levelStats)
                }
            }
        }

        // Chart 2: Score Progression Line Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = strings.scoreProgressionChartTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ScoreProgressionLineChart(
                        scores = studentAttempts.map { it.scorePercent }
                    )
                }
            }
        }

        // Chart 3: 5-Axis Competency Radar Chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = strings.skillsRadarChartTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CompetencyRadarChart(skillScores = skillScores)
                }
            }
        }

        // Strengths & Weaknesses Diagnostic Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = LevelBeginnerEmerald)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.strengthsTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = LevelBeginnerEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        strengths.forEach { item ->
                            Text(
                                text = "• $item",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = AlertHighCoral)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.weaknessesTitle,
                                style = MaterialTheme.typography.titleSmall,
                                color = AlertHighCoral
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (weaknesses.isEmpty()) {
                            Text(
                                text = if (isAr) "• لا توجد نقاط ضعف حرجة" else "• No critical bottlenecks",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            weaknesses.forEach { item ->
                                Text(
                                    text = "• $item",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiMetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
