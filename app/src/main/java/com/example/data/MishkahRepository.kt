package com.example.data

import kotlinx.coroutines.flow.Flow

class MishkahRepository(private val dao: MishkahDao) {
    val allUsers: Flow<List<UserEntity>> = dao.observeAllUsers()
    val chapters: Flow<List<ChapterEntity>> = dao.observeChapters()
    val lessons: Flow<List<LessonEntity>> = dao.observeAllLessons()
    val questions: Flow<List<QuizQuestionEntity>> = dao.observeAllQuestions()
    val allProgress: Flow<List<LessonProgressEntity>> = dao.observeAllProgress()
    val allAttempts: Flow<List<StudentAttemptEntity>> = dao.observeAllAttempts()
    val allAlerts: Flow<List<SmartAlertEntity>> = dao.observeAllAlerts()

    suspend fun ensureSeeded() {
        SeedData.populateIfEmpty(dao)
    }

    suspend fun authenticate(email: String, password: String): UserEntity? {
        val user = dao.getUserByEmail(email.trim().lowercase())
            ?: dao.getUserByEmail(email.trim())
            ?: return null
        return if (SecurityUtils.verifyPassword(password, user.passwordSalt, user.passwordHash)) {
            user
        } else {
            null
        }
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        role: UserRole,
        preferredLang: String
    ): Result<UserEntity> {
        val normalizedEmail = email.trim().lowercase()
        val existing = dao.getUserByEmail(normalizedEmail)
        if (existing != null) {
            return Result.failure(IllegalArgumentException("Email already registered"))
        }
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(password, salt)
        val newUser = UserEntity(
            fullName = fullName.trim(),
            email = normalizedEmail,
            passwordHash = hash,
            passwordSalt = salt,
            role = role.code,
            isEmailVerified = false,
            preferredLang = preferredLang
        )
        val id = dao.insertUser(newUser).toInt()
        return Result.success(newUser.copy(id = id))
    }

    suspend fun resetPassword(email: String, newPassword: String): Boolean {
        val user = dao.getUserByEmail(email.trim().lowercase())
            ?: dao.getUserByEmail(email.trim())
            ?: return false
        val salt = SecurityUtils.generateSalt()
        val hash = SecurityUtils.hashPassword(newPassword, salt)
        dao.updateUser(user.copy(passwordHash = hash, passwordSalt = salt))
        return true
    }

    suspend fun verifyUserEmail(userId: Int) {
        val user = dao.getUserById(userId) ?: return
        dao.updateUser(user.copy(isEmailVerified = true))
    }

    suspend fun recordQuizSubmission(
        userId: Int,
        lesson: LessonEntity,
        scorePercent: Int,
        correctCount: Int,
        totalQuestions: Int,
        studyTimeSeconds: Int,
        missedSkills: List<String>,
        masteredSkills: List<String>
    ) {
        val progressId = "${userId}_${lesson.id}"
        val existing = dao.getProgressById(progressId)
        val newAttemptsCount = (existing?.attemptsCount ?: 0) + 1
        val bestScore = maxOf(existing?.bestScorePercent ?: 0, scorePercent)
        val totalTime = (existing?.totalTimeSeconds ?: 0) + studyTimeSeconds
        val isCompleted = bestScore >= 60

        dao.upsertProgress(
            LessonProgressEntity(
                id = progressId,
                userId = userId,
                lessonId = lesson.id,
                level = lesson.level,
                completionPercent = if (isCompleted) 100 else maxOf(existing?.completionPercent ?: 0, 60),
                totalTimeSeconds = totalTime,
                bestScorePercent = bestScore,
                attemptsCount = newAttemptsCount,
                isCompleted = isCompleted,
                lastAccessedAt = System.currentTimeMillis()
            )
        )

        dao.insertAttempt(
            StudentAttemptEntity(
                userId = userId,
                lessonId = lesson.id,
                level = lesson.level,
                scorePercent = scorePercent,
                correctCount = correctCount,
                totalQuestions = totalQuestions,
                timeSpentSeconds = studyTimeSeconds,
                attemptNumber = newAttemptsCount,
                missedSkillTagsCsv = missedSkills.distinct().joinToString(","),
                masteredSkillTagsCsv = masteredSkills.distinct().joinToString(",")
            )
        )

        // Smart Alert Trigger Logic when student struggles or regresses
        if (scorePercent < 65 || (newAttemptsCount >= 2 && scorePercent < 75)) {
            val severity = if (scorePercent < 55 || newAttemptsCount >= 3) "HIGH" else "MEDIUM"
            dao.insertAlert(
                SmartAlertEntity(
                    userId = userId,
                    level = lesson.level,
                    severity = severity,
                    titleAr = "تنبيه ذكي: تعثر في درس (${lesson.titleAr})",
                    titleEn = "Smart Alert: Difficulty in (${lesson.titleEn})",
                    messageAr = "سجّل الطالب درجة $scorePercent% في المحاولة رقم $newAttemptsCount مع أخطاء في: ${missedSkills.ifEmpty { listOf("التطبيق") }.joinToString("، ")}.",
                    messageEn = "Student scored $scorePercent% on attempt #$newAttemptsCount with errors in: ${missedSkills.ifEmpty { listOf("Application") }.joinToString(", ")}.",
                    recommendationAr = "يُوصى بمراجعة المفاهيم الأساسية والمثال المحلل في الدرس قبل المحاولة القادمة.",
                    recommendationEn = "Recommended: Review the Core Concepts and Analyzed Example before the next attempt."
                )
            )
        }
    }

    suspend fun saveLesson(lesson: LessonEntity): Int {
        return if (lesson.id == 0) {
            dao.insertLesson(lesson).toInt()
        } else {
            dao.updateLesson(lesson)
            lesson.id
        }
    }

    suspend fun deleteLesson(lessonId: Int) = dao.deleteLessonById(lessonId)

    suspend fun saveQuestion(question: QuizQuestionEntity): Int = dao.insertQuestion(question).toInt()

    suspend fun deleteQuestion(questionId: Int) = dao.deleteQuestionById(questionId)

    suspend fun saveChapter(chapter: ChapterEntity): Int = dao.insertChapter(chapter).toInt()

    suspend fun resolveAlert(alertId: Int) = dao.resolveAlert(alertId)
}
