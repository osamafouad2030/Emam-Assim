package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole(val code: String) {
    STUDENT("STUDENT"),
    INSTRUCTOR("INSTRUCTOR"),
    ADMIN("ADMIN");

    companion object {
        fun fromCode(code: String): UserRole = entries.firstOrNull { it.code == code } ?: STUDENT
    }
}

enum class EducationLevel(val code: String, val order: Int) {
    BEGINNER("BEGINNER", 1),
    INTERMEDIATE("INTERMEDIATE", 2),
    ADVANCED("ADVANCED", 3);

    companion object {
        fun fromCode(code: String): EducationLevel = entries.firstOrNull { it.code == code } ?: BEGINNER
    }
}

enum class SkillCategory(val code: String) {
    COMPREHENSION("COMPREHENSION"),
    ANALYSIS("ANALYSIS"),
    APPLICATION("APPLICATION"),
    CRITICAL_THINKING("CRITICAL_THINKING"),
    DEDUCTION("DEDUCTION");

    companion object {
        fun fromCode(code: String): SkillCategory = entries.firstOrNull { it.code == code } ?: COMPREHENSION
    }
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val role: String, // UserRole.code
    val isEmailVerified: Boolean = true,
    val preferredLang: String = "ar",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderIndex: Int,
    val titleAr: String,
    val titleEn: String,
    val categoryAr: String,
    val categoryEn: String,
    val summaryAr: String,
    val summaryEn: String
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chapterId: Int,
    val level: String, // EducationLevel.code
    val orderIndex: Int,
    val titleAr: String,
    val titleEn: String,
    val estimatedMinutes: Int,
    val objectivesAr: String,
    val objectivesEn: String,
    val keyConceptsAr: String,
    val keyConceptsEn: String,
    val contentAr: String,
    val contentEn: String,
    val exampleTitleAr: String,
    val exampleTitleEn: String,
    val exampleBodyAr: String,
    val exampleBodyEn: String,
    val exercisePromptAr: String,
    val exercisePromptEn: String
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lessonId: Int,
    val level: String, // EducationLevel.code
    val skillTag: String, // SkillCategory.code
    val questionAr: String,
    val questionEn: String,
    val option1Ar: String,
    val option2Ar: String,
    val option3Ar: String,
    val option4Ar: String,
    val option1En: String,
    val option2En: String,
    val option3En: String,
    val option4En: String,
    val correctOptionIndex: Int, // 0..3
    val explanationAr: String,
    val explanationEn: String
) {
    fun optionsAr(): List<String> = listOf(option1Ar, option2Ar, option3Ar, option4Ar)
    fun optionsEn(): List<String> = listOf(option1En, option2En, option3En, option4En)
}

@Entity(tableName = "student_attempts")
data class StudentAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val lessonId: Int,
    val level: String,
    val scorePercent: Int,
    val correctCount: Int,
    val totalQuestions: Int,
    val timeSpentSeconds: Int,
    val attemptNumber: Int,
    val missedSkillTagsCsv: String,
    val masteredSkillTagsCsv: String,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey val id: String, // "${userId}_${lessonId}"
    val userId: Int,
    val lessonId: Int,
    val level: String,
    val completionPercent: Int,
    val totalTimeSeconds: Int,
    val bestScorePercent: Int,
    val attemptsCount: Int,
    val isCompleted: Boolean,
    val lastAccessedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "smart_alerts")
data class SmartAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val level: String,
    val severity: String, // HIGH, MEDIUM, INFO
    val titleAr: String,
    val titleEn: String,
    val messageAr: String,
    val messageEn: String,
    val recommendationAr: String,
    val recommendationEn: String,
    val isResolved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
