package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ChapterEntity
import com.example.data.EducationLevel
import com.example.data.LessonEntity
import com.example.data.LessonProgressEntity
import com.example.data.MishkahRepository
import com.example.data.QuizQuestionEntity
import com.example.data.SecurityUtils
import com.example.data.SkillCategory
import com.example.data.SmartAlertEntity
import com.example.data.StudentAttemptEntity
import com.example.data.UserEntity
import com.example.data.UserRole
import com.example.domain.AnalyzedBookResult
import com.example.domain.BookContentAnalyzer
import com.example.domain.LevelStatSummary
import com.example.i18n.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    CURRICULUM,
    ANALYTICS,
    BOOK_ANALYZER,
    ADMIN_CMS
}

data class QuizSubmissionSummary(
    val scorePercent: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val timeSpentSeconds: Int,
    val selectedAnswers: Map<Int, Int>
)

data class MishkahUiState(
    val language: AppLanguage = AppLanguage.AR,
    val isDarkTheme: Boolean = true,
    val currentUser: UserEntity? = null,
    val sessionToken: String? = null,
    val authStatusMessage: String? = null,
    val isAuthError: Boolean = false,
    val currentTab: MainTab = MainTab.CURRICULUM,
    val selectedLevelFilter: EducationLevel? = null, // null = All levels
    val activeLessonId: Int? = null,
    val isTakingQuiz: Boolean = false,
    val lastQuizSummary: QuizSubmissionSummary? = null,
    val selectedStudentIdForAnalytics: Int = 1,
    val lastAnalyzedBookResult: AnalyzedBookResult? = null,
    val statusBannerMessage: String? = null
)

class MishkahViewModel(
    private val repository: MishkahRepository,
    context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("mishkah_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(
        MishkahUiState(
            language = if (prefs.getString("lang", "ar") == "en") AppLanguage.EN else AppLanguage.AR,
            isDarkTheme = prefs.getBoolean("dark_theme", true)
        )
    )
    val uiState: StateFlow<MishkahUiState> = _uiState.asStateFlow()

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chapters: StateFlow<List<ChapterEntity>> = repository.chapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lessons: StateFlow<List<LessonEntity>> = repository.lessons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val questions: StateFlow<List<QuizQuestionEntity>> = repository.questions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProgress: StateFlow<List<LessonProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<StudentAttemptEntity>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAlerts: StateFlow<List<SmartAlertEntity>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
            // Restore active session or auto-select student@mishkah.edu on first launch if saved
            val savedEmail = prefs.getString("session_email", null)
            if (savedEmail != null) {
                val users = repository.allUsers
                // Will be matched via quickLogin or login
            }
        }
    }

    fun toggleLanguage() {
        val next = _uiState.value.language.toggle()
        prefs.edit().putString("lang", next.code).apply()
        _uiState.value = _uiState.value.copy(language = next)
    }

    fun toggleTheme() {
        val next = !_uiState.value.isDarkTheme
        prefs.edit().putBoolean("dark_theme", next).apply()
        _uiState.value = _uiState.value.copy(isDarkTheme = next)
    }

    fun clearBanner() {
        _uiState.value = _uiState.value.copy(statusBannerMessage = null, authStatusMessage = null)
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.authenticate(email, password)
            if (user != null) {
                val token = SecurityUtils.generateSessionToken(user.id, user.email, user.role)
                prefs.edit().putString("session_email", user.email).apply()
                val targetStudentId = if (user.role == UserRole.STUDENT.code) user.id else 1
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    sessionToken = token,
                    selectedStudentIdForAnalytics = targetStudentId,
                    authStatusMessage = null,
                    isAuthError = false,
                    currentTab = MainTab.CURRICULUM
                )
            } else {
                val msg = if (_uiState.value.language == AppLanguage.AR) {
                    "بيانات الدخول غير صحيحة. تأكد من البريد الإلكتروني وكلمة المرور."
                } else {
                    "Invalid email or password. Please verify your credentials."
                }
                _uiState.value = _uiState.value.copy(authStatusMessage = msg, isAuthError = true)
            }
        }
    }

    fun quickRoleLogin(user: UserEntity) {
        val token = SecurityUtils.generateSessionToken(user.id, user.email, user.role)
        prefs.edit().putString("session_email", user.email).apply()
        val targetStudentId = if (user.role == UserRole.STUDENT.code) user.id else 1
        _uiState.value = _uiState.value.copy(
            currentUser = user,
            sessionToken = token,
            selectedStudentIdForAnalytics = targetStudentId,
            authStatusMessage = null,
            isAuthError = false
        )
    }

    fun register(fullName: String, email: String, password: String, role: UserRole) {
        if (fullName.isBlank() || !email.contains("@") || password.length < 6) {
            val msg = if (_uiState.value.language == AppLanguage.AR) {
                "يرجى إدخال اسم صحيح وبريد إلكتروني صالح وكلمة مرور من ٦ أحرف على الأقل."
            } else {
                "Please enter a valid name, email address, and password (6+ chars)."
            }
            _uiState.value = _uiState.value.copy(authStatusMessage = msg, isAuthError = true)
            return
        }
        viewModelScope.launch {
            val result = repository.registerUser(
                fullName = fullName,
                email = email,
                password = password,
                role = role,
                preferredLang = _uiState.value.language.code
            )
            result.onSuccess { newUser ->
                val token = SecurityUtils.generateSessionToken(newUser.id, newUser.email, newUser.role)
                _uiState.value = _uiState.value.copy(
                    currentUser = newUser,
                    sessionToken = token,
                    selectedStudentIdForAnalytics = newUser.id,
                    authStatusMessage = null,
                    isAuthError = false
                )
            }.onFailure {
                val msg = if (_uiState.value.language == AppLanguage.AR) {
                    "هذا البريد الإلكتروني مسجل مسبقاً."
                } else {
                    "This email address is already registered."
                }
                _uiState.value = _uiState.value.copy(authStatusMessage = msg, isAuthError = true)
            }
        }
    }

    fun resetPassword(email: String, newPassword: String) {
        if (newPassword.length < 6) {
            val msg = if (_uiState.value.language == AppLanguage.AR) {
                "يجب أن تتكون كلمة المرور الجديدة من ٦ أحرف على الأقل."
            } else {
                "New password must be at least 6 characters."
            }
            _uiState.value = _uiState.value.copy(authStatusMessage = msg, isAuthError = true)
            return
        }
        viewModelScope.launch {
            val ok = repository.resetPassword(email, newPassword)
            val msg = if (ok) {
                if (_uiState.value.language == AppLanguage.AR) "تم تحديث كلمة المرور بنجاح. يمكنك تسجيل الدخول الآن."
                else "Password updated successfully. You may now sign in."
            } else {
                if (_uiState.value.language == AppLanguage.AR) "لم يتم العثور على حساب بهذا البريد الإلكتروني."
                else "No account found matching this email address."
            }
            _uiState.value = _uiState.value.copy(authStatusMessage = msg, isAuthError = !ok)
        }
    }

    fun verifyCurrentEmail() {
        val current = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.verifyUserEmail(current.id)
            _uiState.value = _uiState.value.copy(
                currentUser = current.copy(isEmailVerified = true),
                statusBannerMessage = if (_uiState.value.language == AppLanguage.AR) "تم توثيق البريد الإلكتروني بنجاح" else "Email verified successfully"
            )
        }
    }

    fun logout() {
        prefs.edit().remove("session_email").apply()
        _uiState.value = _uiState.value.copy(
            currentUser = null,
            sessionToken = null,
            activeLessonId = null,
            isTakingQuiz = false,
            lastQuizSummary = null
        )
    }

    fun selectTab(tab: MainTab) {
        _uiState.value = _uiState.value.copy(
            currentTab = tab,
            activeLessonId = null,
            isTakingQuiz = false,
            lastQuizSummary = null
        )
    }

    fun selectLevelFilter(level: EducationLevel?) {
        _uiState.value = _uiState.value.copy(selectedLevelFilter = level)
    }

    fun openLesson(lessonId: Int) {
        _uiState.value = _uiState.value.copy(
            activeLessonId = lessonId,
            isTakingQuiz = false,
            lastQuizSummary = null
        )
    }

    fun closeLesson() {
        _uiState.value = _uiState.value.copy(
            activeLessonId = null,
            isTakingQuiz = false,
            lastQuizSummary = null
        )
    }

    fun startLessonQuiz() {
        _uiState.value = _uiState.value.copy(isTakingQuiz = true, lastQuizSummary = null)
    }

    fun submitQuiz(
        lesson: LessonEntity,
        lessonQuestions: List<QuizQuestionEntity>,
        selectedAnswers: Map<Int, Int>,
        studyTimeSeconds: Int
    ) {
        val userId = _uiState.value.currentUser?.id ?: 1
        val total = lessonQuestions.size.coerceAtLeast(1)
        var correct = 0
        val missedSkills = mutableListOf<String>()
        val masteredSkills = mutableListOf<String>()

        lessonQuestions.forEach { q ->
            val picked = selectedAnswers[q.id]
            if (picked != null && picked == q.correctOptionIndex) {
                correct++
                masteredSkills.add(q.skillTag)
            } else {
                missedSkills.add(q.skillTag)
            }
        }

        val scorePercent = ((correct.toFloat() / total.toFloat()) * 100f).toInt()
        viewModelScope.launch {
            repository.recordQuizSubmission(
                userId = userId,
                lesson = lesson,
                scorePercent = scorePercent,
                correctCount = correct,
                totalQuestions = lessonQuestions.size,
                studyTimeSeconds = studyTimeSeconds.coerceAtLeast(45),
                missedSkills = missedSkills,
                masteredSkills = masteredSkills
            )
            _uiState.value = _uiState.value.copy(
                lastQuizSummary = QuizSubmissionSummary(
                    scorePercent = scorePercent,
                    correctCount = correct,
                    totalQuestions = lessonQuestions.size,
                    timeSpentSeconds = studyTimeSeconds.coerceAtLeast(45),
                    selectedAnswers = selectedAnswers
                )
            )
        }
    }

    fun selectStudentForAnalytics(studentId: Int) {
        _uiState.value = _uiState.value.copy(selectedStudentIdForAnalytics = studentId)
    }

    fun resolveAlert(alertId: Int) {
        viewModelScope.launch {
            repository.resolveAlert(alertId)
        }
    }

    fun analyzeAndImportBookText(rawText: String, targetChapterId: Int) {
        viewModelScope.launch {
            val currentCount = lessons.value.size
            val result = BookContentAnalyzer.analyzeAndClassifyText(
                rawText = rawText,
                targetChapterId = targetChapterId,
                startingOrderIndex = currentCount + 1
            )
            result.generatedLessons.forEachIndexed { idx, lesson ->
                val newLessonId = repository.saveLesson(lesson)
                result.generatedQuestions.getOrNull(idx)?.let { q ->
                    repository.saveQuestion(q.copy(lessonId = newLessonId))
                }
            }
            val msg = if (_uiState.value.language == AppLanguage.AR) {
                "تم تحليل المحتوى وتوليد ${result.generatedLessons.size} دروس تفاعلية موزعة على ٣ مستويات بنجاح!"
            } else {
                "Analyzed & imported ${result.generatedLessons.size} interactive lessons across 3 levels!"
            }
            _uiState.value = _uiState.value.copy(
                lastAnalyzedBookResult = result,
                statusBannerMessage = msg
            )
        }
    }

    fun saveCustomLesson(
        id: Int = 0,
        chapterId: Int,
        level: EducationLevel,
        titleAr: String,
        titleEn: String,
        contentAr: String,
        contentEn: String,
        exampleAr: String,
        exampleEn: String
    ) {
        if (titleAr.isBlank() && titleEn.isBlank()) return
        viewModelScope.launch {
            val effectiveTitleAr = titleAr.ifBlank { titleEn }
            val effectiveTitleEn = titleEn.ifBlank { titleAr }
            val effectiveContentAr = contentAr.ifBlank { contentEn }
            val effectiveContentEn = contentEn.ifBlank { contentAr }
            val lesson = LessonEntity(
                id = id,
                chapterId = chapterId,
                level = level.code,
                orderIndex = lessons.value.size + 1,
                titleAr = effectiveTitleAr,
                titleEn = effectiveTitleEn,
                estimatedMinutes = 20,
                objectivesAr = "• إتقان مفاهيم: $effectiveTitleAr\n• التطبيق المنهجي",
                objectivesEn = "• Master concepts of: $effectiveTitleEn\n• Systematic application",
                keyConceptsAr = effectiveContentAr.take(140),
                keyConceptsEn = effectiveContentEn.take(140),
                contentAr = effectiveContentAr,
                contentEn = effectiveContentEn,
                exampleTitleAr = "مثال تطبيقي",
                exampleTitleEn = "Applied Example",
                exampleBodyAr = exampleAr.ifBlank { effectiveContentAr.take(120) },
                exampleBodyEn = exampleEn.ifBlank { effectiveContentEn.take(120) },
                exercisePromptAr = "طبّق القاعدة المستفادة من هذا الدرس على مثال من واقعك.",
                exercisePromptEn = "Apply the rule learned in this lesson to a practical example."
            )
            repository.saveLesson(lesson)
            val msg = if (_uiState.value.language == AppLanguage.AR) "تم حفظ الدرس بنجاح في قاعدة البيانات" else "Lesson saved to database"
            _uiState.value = _uiState.value.copy(statusBannerMessage = msg)
        }
    }

    fun deleteLesson(lessonId: Int) {
        viewModelScope.launch {
            repository.deleteLesson(lessonId)
            val msg = if (_uiState.value.language == AppLanguage.AR) "تم حذف الدرس" else "Lesson deleted"
            _uiState.value = _uiState.value.copy(statusBannerMessage = msg)
        }
    }

    fun saveCustomQuestion(
        lessonId: Int,
        level: EducationLevel,
        skill: SkillCategory,
        questionAr: String,
        questionEn: String,
        opt1: String,
        opt2: String,
        opt3: String,
        opt4: String,
        correctIndex: Int,
        explanation: String
    ) {
        if (questionAr.isBlank() && questionEn.isBlank()) return
        viewModelScope.launch {
            val qAr = questionAr.ifBlank { questionEn }
            val qEn = questionEn.ifBlank { questionAr }
            repository.saveQuestion(
                QuizQuestionEntity(
                    lessonId = lessonId,
                    level = level.code,
                    skillTag = skill.code,
                    questionAr = qAr,
                    questionEn = qEn,
                    option1Ar = opt1,
                    option2Ar = opt2,
                    option3Ar = opt3,
                    option4Ar = opt4,
                    option1En = opt1,
                    option2En = opt2,
                    option3En = opt3,
                    option4En = opt4,
                    correctOptionIndex = correctIndex.coerceIn(0, 3),
                    explanationAr = explanation.ifBlank { "راجع الشرح المنهجي في الدرس." },
                    explanationEn = explanation.ifBlank { "Refer to the systematic explanation in the lesson." }
                )
            )
            val msg = if (_uiState.value.language == AppLanguage.AR) "تمت إضافة السؤال التفاعلي للدرس" else "Interactive question added to lesson"
            _uiState.value = _uiState.value.copy(statusBannerMessage = msg)
        }
    }

    companion object {
        fun provideFactory(repository: MishkahRepository, context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MishkahViewModel(repository, context.applicationContext) as T
                }
            }
    }
}
