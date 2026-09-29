package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.ChapterEntity
import com.example.data.EducationLevel
import com.example.data.LessonEntity
import com.example.data.SkillCategory
import com.example.domain.AnalyzedBookResult
import com.example.domain.BookContentAnalyzer
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppStrings
import com.example.ui.theme.LevelAdvancedRuby
import com.example.ui.theme.LevelBeginnerEmerald
import com.example.ui.theme.LevelIntermediateGold

@Composable
fun BookAnalyzerScreen(
    chapters: List<ChapterEntity>,
    lastResult: AnalyzedBookResult?,
    onAnalyzeAndImport: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isAr = strings.language == AppLanguage.AR
    val context = LocalContext.current

    var rawBookText by remember { mutableStateOf(BookContentAnalyzer.sampleBookManuscriptText) }
    var selectedChapterId by remember(chapters) {
        mutableIntStateOf(chapters.firstOrNull()?.id ?: 1)
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
            }.getOrNull()?.let { content ->
                if (content.isNotBlank()) {
                    rawBookText = content
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = strings.analyzerTitle,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = strings.analyzerSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Pedagogical Classification Criteria Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = strings.classificationCriteriaTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CriteriaRow(
                        badge = strings.levelBeginner,
                        desc = strings.levelBeginnerDesc,
                        color = LevelBeginnerEmerald
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CriteriaRow(
                        badge = strings.levelIntermediate,
                        desc = strings.levelIntermediateDesc,
                        color = LevelIntermediateGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    CriteriaRow(
                        badge = strings.levelAdvanced,
                        desc = strings.levelAdvancedDesc,
                        color = LevelAdvancedRuby
                    )
                }
            }
        }

        // File Upload & Sample Loader Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { filePickerLauncher.launch("text/*") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("upload_book_file_button")
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.uploadBookFileBtn)
                }

                OutlinedButton(
                    onClick = { rawBookText = BookContentAnalyzer.sampleBookManuscriptText },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.loadSampleBookBtn)
                }
            }
        }

        // Raw Book Input & Analyze Trigger
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = rawBookText,
                        onValueChange = { rawBookText = it },
                        label = { Text(strings.bookRawTextLabel) },
                        minLines = 6,
                        maxLines = 10,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("book_raw_text_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onAnalyzeAndImport(rawBookText, selectedChapterId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("analyze_and_import_button")
                    ) {
                        Icon(Icons.Default.AutoAwesomeMotion, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.analyzeAndImportBtn)
                    }
                }
            }
        }

        // Analysis Result Summary
        if (lastResult != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = strings.analysisSummaryTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "• ${lastResult.detectedTitle}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• ${if (isAr) lastResult.detectedDomainAr else lastResult.detectedDomainEn}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${strings.levelBeginner}: ${lastResult.beginnerCount} | ${strings.levelIntermediate}: ${lastResult.intermediateCount} | ${strings.levelAdvanced}: ${lastResult.advancedCount}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CriteriaRow(
    badge: String,
    desc: String,
    color: androidx.compose.ui.graphics.Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.18f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = badge,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = desc,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun AdminCmsScreen(
    chapters: List<ChapterEntity>,
    lessons: List<LessonEntity>,
    onSaveLesson: (Int, Int, EducationLevel, String, String, String, String, String, String) -> Unit,
    onDeleteLesson: (Int) -> Unit,
    onSaveQuestion: (Int, EducationLevel, SkillCategory, String, String, String, String, String, String, Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isAr = strings.language == AppLanguage.AR
    var selectedSubTab by remember { mutableIntStateOf(0) }

    // Lesson form states
    var selectedLevel by remember { mutableStateOf(EducationLevel.BEGINNER) }
    var selectedChapterId by remember(chapters) { mutableIntStateOf(chapters.firstOrNull()?.id ?: 1) }
    var titleAr by remember { mutableStateOf("") }
    var titleEn by remember { mutableStateOf("") }
    var contentAr by remember { mutableStateOf("") }
    var contentEn by remember { mutableStateOf("") }
    var exampleAr by remember { mutableStateOf("") }
    var exampleEn by remember { mutableStateOf("") }

    // Question form states
    var targetLessonId by remember(lessons) { mutableIntStateOf(lessons.firstOrNull()?.id ?: 1) }
    var qAr by remember { mutableStateOf("") }
    var qEn by remember { mutableStateOf("") }
    var opt1 by remember { mutableStateOf("") }
    var opt2 by remember { mutableStateOf("") }
    var opt3 by remember { mutableStateOf("") }
    var opt4 by remember { mutableStateOf("") }
    var correctIdx by remember { mutableStateOf("1") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = strings.cmsTitle,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = strings.cmsSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            TabRow(selectedTabIndex = selectedSubTab) {
                Tab(
                    selected = selectedSubTab == 0,
                    onClick = { selectedSubTab = 0 },
                    text = { Text(strings.addLessonTab) }
                )
                Tab(
                    selected = selectedSubTab == 1,
                    onClick = { selectedSubTab = 1 },
                    text = { Text(strings.addQuestionTab) }
                )
                Tab(
                    selected = selectedSubTab == 2,
                    onClick = { selectedSubTab = 2 },
                    text = { Text(strings.manageExistingTab) }
                )
            }
        }

        if (selectedSubTab == 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                EducationLevel.BEGINNER to strings.levelBeginner,
                                EducationLevel.INTERMEDIATE to strings.levelIntermediate,
                                EducationLevel.ADVANCED to strings.levelAdvanced
                            ).forEach { (lvl, label) ->
                                FilterChip(
                                    selected = selectedLevel == lvl,
                                    onClick = { selectedLevel = lvl },
                                    label = { Text(label) }
                                )
                            }
                        }

                        OutlinedTextField(
                            value = titleAr,
                            onValueChange = { titleAr = it },
                            label = { Text(strings.lessonTitleArLabel) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("cms_lesson_title_ar")
                        )
                        OutlinedTextField(
                            value = titleEn,
                            onValueChange = { titleEn = it },
                            label = { Text(strings.lessonTitleEnLabel) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = contentAr,
                            onValueChange = { contentAr = it },
                            label = { Text(strings.lessonContentArLabel) },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = exampleAr,
                            onValueChange = { exampleAr = it },
                            label = { Text(strings.lessonExampleArLabel) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                onSaveLesson(
                                    0,
                                    selectedChapterId,
                                    selectedLevel,
                                    titleAr,
                                    titleEn,
                                    contentAr,
                                    contentEn,
                                    exampleAr,
                                    exampleEn
                                )
                                titleAr = ""
                                titleEn = ""
                                contentAr = ""
                                exampleAr = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("cms_save_lesson_button")
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strings.saveLessonBtn)
                        }
                    }
                }
            }
        } else if (selectedSubTab == 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = qAr,
                            onValueChange = { qAr = it },
                            label = { Text(strings.questionTextArLabel) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = opt1,
                            onValueChange = { opt1 = it },
                            label = { Text(strings.option1Label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = opt2,
                            onValueChange = { opt2 = it },
                            label = { Text(strings.option2Label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = opt3,
                            onValueChange = { opt3 = it },
                            label = { Text(strings.option3Label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = opt4,
                            onValueChange = { opt4 = it },
                            label = { Text(strings.option4Label) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = correctIdx,
                            onValueChange = { correctIdx = it },
                            label = { Text(strings.correctOptionIndexLabel) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val parsedIdx = (correctIdx.toIntOrNull() ?: 1) - 1
                                onSaveQuestion(
                                    targetLessonId,
                                    selectedLevel,
                                    SkillCategory.COMPREHENSION,
                                    qAr,
                                    qEn,
                                    opt1,
                                    opt2,
                                    opt3,
                                    opt4,
                                    parsedIdx,
                                    ""
                                )
                                qAr = ""
                                opt1 = ""
                                opt2 = ""
                                opt3 = ""
                                opt4 = ""
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("cms_save_question_button")
                        ) {
                            Text(strings.saveQuestionBtn)
                        }
                    }
                }
            }
        } else {
            items(lessons, key = { it.id }) { lesson ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) lesson.titleAr else lesson.titleEn,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = lesson.level,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { onDeleteLesson(lesson.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = strings.deleteBtn,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
