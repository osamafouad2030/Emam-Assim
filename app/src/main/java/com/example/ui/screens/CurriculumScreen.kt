package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ChapterEntity
import com.example.data.EducationLevel
import com.example.data.LessonEntity
import com.example.data.LessonProgressEntity
import com.example.data.QuizQuestionEntity
import com.example.i18n.AppLanguage
import com.example.i18n.LocalAppStrings
import com.example.ui.QuizSubmissionSummary
import com.example.ui.theme.LevelAdvancedRuby
import com.example.ui.theme.LevelBeginnerEmerald
import com.example.ui.theme.LevelIntermediateGold
import com.example.ui.theme.MishkahGoldPrimary
import com.example.ui.theme.MishkahObsidian
import kotlinx.coroutines.delay

@Composable
fun CurriculumScreen(
    chapters: List<ChapterEntity>,
    lessons: List<LessonEntity>,
    questions: List<QuizQuestionEntity>,
    userProgress: List<LessonProgressEntity>,
    selectedLevel: EducationLevel?,
    activeLessonId: Int?,
    isTakingQuiz: Boolean,
    lastQuizSummary: QuizSubmissionSummary?,
    onSelectLevel: (EducationLevel?) -> Unit,
    onOpenLesson: (Int) -> Unit,
    onCloseLesson: () -> Unit,
    onStartQuiz: () -> Unit,
    onSubmitQuiz: (LessonEntity, List<QuizQuestionEntity>, Map<Int, Int>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeLesson = lessons.firstOrNull { it.id == activeLessonId }

    if (activeLesson != null) {
        val lessonQuestions = questions.filter { it.lessonId == activeLesson.id }
        InteractiveLessonDetailScreen(
            lesson = activeLesson,
            questions = lessonQuestions,
            isTakingQuiz = isTakingQuiz,
            lastQuizSummary = lastQuizSummary,
            onBack = onCloseLesson,
            onStartQuiz = onStartQuiz,
            onSubmitQuiz = { answers, elapsedSec ->
                onSubmitQuiz(activeLesson, lessonQuestions, answers, elapsedSec)
            },
            modifier = modifier
        )
    } else {
        CurriculumOverviewList(
            chapters = chapters,
            lessons = lessons,
            userProgress = userProgress,
            selectedLevel = selectedLevel,
            onSelectLevel = onSelectLevel,
            onOpenLesson = onOpenLesson,
            modifier = modifier
        )
    }
}

@Composable
private fun CurriculumOverviewList(
    chapters: List<ChapterEntity>,
    lessons: List<LessonEntity>,
    userProgress: List<LessonProgressEntity>,
    selectedLevel: EducationLevel?,
    onSelectLevel: (EducationLevel?) -> Unit,
    onOpenLesson: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val isAr = strings.language == AppLanguage.AR
    val progressByLessonId = remember(userProgress) { userProgress.associateBy { it.lessonId } }

    val filteredLessons = remember(lessons, selectedLevel) {
        if (selectedLevel == null) lessons
        else lessons.filter { it.level == selectedLevel.code }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Book Banner Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_mishkah),
                        contentDescription = strings.bookTitle,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MishkahObsidian.copy(alpha = 0.35f),
                                        MishkahObsidian.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(18.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MishkahGoldPrimary.copy(alpha = 0.22f))
                                .border(1.dp, MishkahGoldPrimary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = strings.bookCategoryBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = MishkahGoldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = strings.bookTitle,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color(0xFFF6F1E3)
                        )
                    }
                }
            }
        }

        // 2. Three Educational Levels Summary Cards
        item {
            val levelConfigs = listOf(
                Triple(EducationLevel.BEGINNER, strings.levelBeginner, LevelBeginnerEmerald),
                Triple(EducationLevel.INTERMEDIATE, strings.levelIntermediate, LevelIntermediateGold),
                Triple(EducationLevel.ADVANCED, strings.levelAdvanced, LevelAdvancedRuby)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                levelConfigs.forEach { (level, title, accentColor) ->
                    val levelLessons = lessons.filter { it.level == level.code }
                    val completedCount = levelLessons.count { progressByLessonId[it.id]?.isCompleted == true }
                    val ratio = if (levelLessons.isEmpty()) 0f else completedCount.toFloat() / levelLessons.size
                    val isSelected = selectedLevel == level

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onSelectLevel(if (isSelected) null else level)
                            }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) accentColor else accentColor.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .testTag("level_card_${level.code.lowercase()}"),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "$completedCount / ${levelLessons.size}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = accentColor,
                                trackColor = accentColor.copy(alpha = 0.18f)
                            )
                        }
                    }
                }
            }
        }

        // 3. Level Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedLevel == null,
                        onClick = { onSelectLevel(null) },
                        label = { Text(strings.allLevelsFilter) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedLevel == EducationLevel.BEGINNER,
                        onClick = { onSelectLevel(EducationLevel.BEGINNER) },
                        label = { Text(strings.levelBeginner) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedLevel == EducationLevel.INTERMEDIATE,
                        onClick = { onSelectLevel(EducationLevel.INTERMEDIATE) },
                        label = { Text(strings.levelIntermediate) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedLevel == EducationLevel.ADVANCED,
                        onClick = { onSelectLevel(EducationLevel.ADVANCED) },
                        label = { Text(strings.levelAdvanced) }
                    )
                }
            }
        }

        // 4. Chapters and Interactive Lesson Cards
        items(chapters, key = { it.id }) { chapter ->
            val chapterLessons = filteredLessons.filter { it.chapterId == chapter.id }
            if (chapterLessons.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (isAr) chapter.titleAr else chapter.titleEn,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isAr) chapter.summaryAr else chapter.summaryEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    chapterLessons.forEach { lesson ->
                        val prog = progressByLessonId[lesson.id]
                        LessonItemCard(
                            lesson = lesson,
                            progress = prog,
                            isAr = isAr,
                            onClick = { onOpenLesson(lesson.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonItemCard(
    lesson: LessonEntity,
    progress: LessonProgressEntity?,
    isAr: Boolean,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val levelEnum = EducationLevel.fromCode(lesson.level)
    val levelColor = when (levelEnum) {
        EducationLevel.BEGINNER -> LevelBeginnerEmerald
        EducationLevel.INTERMEDIATE -> LevelIntermediateGold
        EducationLevel.ADVANCED -> LevelAdvancedRuby
    }
    val levelLabel = when (levelEnum) {
        EducationLevel.BEGINNER -> strings.levelBeginner
        EducationLevel.INTERMEDIATE -> strings.levelIntermediate
        EducationLevel.ADVANCED -> strings.levelAdvanced
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("lesson_card_${lesson.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(levelColor.copy(alpha = 0.16f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = levelLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = levelColor,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(strings.minutesFormat, lesson.estimatedMinutes),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAr) lesson.titleAr else lesson.titleEn,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = (if (isAr) lesson.contentAr else lesson.contentEn).take(120) + "...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (progress != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = if (progress.isCompleted) LevelBeginnerEmerald else LevelIntermediateGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${strings.bestScoreLabel}: ${progress.bestScorePercent}% • ${strings.attemptsLabel}: ${progress.attemptsCount}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Text(
                        text = strings.startLessonBtn,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = strings.startLessonBtn,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun InteractiveLessonDetailScreen(
    lesson: LessonEntity,
    questions: List<QuizQuestionEntity>,
    isTakingQuiz: Boolean,
    lastQuizSummary: QuizSubmissionSummary?,
    onBack: () -> Unit,
    onStartQuiz: () -> Unit,
    onSubmitQuiz: (Map<Int, Int>, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val strings = LocalAppStrings.current
    val isAr = strings.language == AppLanguage.AR
    var elapsedSeconds by remember(lesson.id) { mutableIntStateOf(0) }
    val selectedAnswers = remember(lesson.id, lastQuizSummary) { mutableStateMapOf<Int, Int>() }

    LaunchedEffect(lesson.id) {
        while (true) {
            delay(1000L)
            elapsedSeconds++
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header with Back Button and Active Study Timer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("lesson_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.backToCurriculumBtn)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(strings.backToCurriculumBtn)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val mins = elapsedSeconds / 60
                    val secs = elapsedSeconds % 60
                    Text(
                        text = "${strings.studyTimerLabel}: %02d:%02d".format(mins, secs),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Lesson Title & Level Badge
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isAr) lesson.titleAr else lesson.titleEn,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = strings.objectivesHeader,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr) lesson.objectivesAr else lesson.objectivesEn,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (!isTakingQuiz) {
            // Core Concepts Card
            item {
                LessonSectionCard(
                    icon = Icons.Default.Stars,
                    title = strings.coreConceptsHeader,
                    body = if (isAr) lesson.keyConceptsAr else lesson.keyConceptsEn,
                    accentColor = MishkahGoldPrimary
                )
            }

            // Systematic Explanation Card
            item {
                LessonSectionCard(
                    icon = Icons.Default.AutoStories,
                    title = strings.lessonContentHeader,
                    body = if (isAr) lesson.contentAr else lesson.contentEn,
                    accentColor = LevelBeginnerEmerald
                )
            }

            // Practical Book Example Card
            item {
                LessonSectionCard(
                    icon = Icons.Default.Lightbulb,
                    title = "${strings.practicalExampleHeader}: ${if (isAr) lesson.exampleTitleAr else lesson.exampleTitleEn}",
                    body = if (isAr) lesson.exampleBodyAr else lesson.exampleBodyEn,
                    accentColor = LevelIntermediateGold
                )
            }

            // Reflective Exercise Prompt Card
            item {
                LessonSectionCard(
                    icon = Icons.Default.Quiz,
                    title = strings.interactiveExerciseHeader,
                    body = if (isAr) lesson.exercisePromptAr else lesson.exercisePromptEn,
                    accentColor = LevelAdvancedRuby
                )
            }

            // Start Assessment Quiz Button
            item {
                Button(
                    onClick = onStartQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_lesson_quiz_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Quiz, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(strings.startQuizBtn, style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            // Quiz Result Summary if submitted
            if (lastQuizSummary != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, MishkahGoldPrimary, RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MishkahGoldPrimary,
                                modifier = Modifier.size(42.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = strings.quizResultTitle,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${lastQuizSummary.scorePercent}% (${lastQuizSummary.correctCount}/${lastQuizSummary.totalQuestions})",
                                style = MaterialTheme.typography.displayMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = onStartQuiz,
                                modifier = Modifier.testTag("retry_quiz_button")
                            ) {
                                Icon(Icons.Default.Replay, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.tryAgainBtn)
                            }
                        }
                    }
                }
            }

            // Interactive Questions List
            items(questions, key = { it.id }) { q ->
                val options = if (isAr) q.optionsAr() else q.optionsEn()
                val picked = lastQuizSummary?.selectedAnswers?.get(q.id) ?: selectedAnswers[q.id]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isAr) q.questionAr else q.questionEn,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        options.forEachIndexed { idx, optText ->
                            val isSelected = picked == idx
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable(enabled = lastQuizSummary == null) {
                                        selectedAnswers[q.id] = idx
                                    }
                                    .padding(vertical = 6.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        if (lastQuizSummary == null) selectedAnswers[q.id] = idx
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = optText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        AnimatedVisibility(visible = lastQuizSummary != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = strings.explanationLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = if (isAr) q.explanationAr else q.explanationEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            if (lastQuizSummary == null) {
                item {
                    Button(
                        onClick = { onSubmitQuiz(selectedAnswers.toMap(), elapsedSeconds) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_quiz_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(strings.submitQuizBtn, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    body: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
