package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MishkahDao {
    // Users & Auth
    @Query("SELECT * FROM users ORDER BY id ASC")
    fun observeAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    // Chapters
    @Query("SELECT * FROM chapters ORDER BY orderIndex ASC")
    fun observeChapters(): Flow<List<ChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    // Lessons
    @Query("SELECT * FROM lessons ORDER BY level ASC, orderIndex ASC")
    fun observeAllLessons(): Flow<List<LessonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity): Long

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("DELETE FROM lessons WHERE id = :lessonId")
    suspend fun deleteLessonById(lessonId: Int)

    // Quiz Questions
    @Query("SELECT * FROM quiz_questions ORDER BY lessonId ASC, id ASC")
    fun observeAllQuestions(): Flow<List<QuizQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestionEntity): Long

    @Query("DELETE FROM quiz_questions WHERE id = :questionId")
    suspend fun deleteQuestionById(questionId: Int)

    // Progress & Attempts
    @Query("SELECT * FROM lesson_progress ORDER BY lastAccessedAt DESC")
    fun observeAllProgress(): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE id = :progressId LIMIT 1")
    suspend fun getProgressById(progressId: String): LessonProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: LessonProgressEntity)

    @Query("SELECT * FROM student_attempts ORDER BY completedAt ASC")
    fun observeAllAttempts(): Flow<List<StudentAttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: StudentAttemptEntity): Long

    // Smart Alerts
    @Query("SELECT * FROM smart_alerts ORDER BY isResolved ASC, createdAt DESC")
    fun observeAllAlerts(): Flow<List<SmartAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SmartAlertEntity): Long

    @Query("UPDATE smart_alerts SET isResolved = 1 WHERE id = :alertId")
    suspend fun resolveAlert(alertId: Int)
}
