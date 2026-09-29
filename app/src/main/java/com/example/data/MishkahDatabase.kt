package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        ChapterEntity::class,
        LessonEntity::class,
        QuizQuestionEntity::class,
        StudentAttemptEntity::class,
        LessonProgressEntity::class,
        SmartAlertEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MishkahDatabase : RoomDatabase() {
    abstract fun mishkahDao(): MishkahDao

    companion object {
        @Volatile
        private var INSTANCE: MishkahDatabase? = null

        fun getDatabase(context: Context): MishkahDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MishkahDatabase::class.java,
                    "mishkah_edtech.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
