package com.example.domain

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.data.EducationLevel
import com.example.data.LessonProgressEntity
import com.example.data.SmartAlertEntity
import com.example.data.StudentAttemptEntity
import com.example.data.UserEntity
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LevelStatSummary(
    val level: EducationLevel,
    val completionPercent: Int,
    val avgScorePercent: Int,
    val totalMinutes: Int,
    val attemptsCount: Int
)

object ReportExporter {

    fun buildCsvContent(
        student: UserEntity,
        levelStats: List<LevelStatSummary>,
        attempts: List<StudentAttemptEntity>,
        strengths: List<String>,
        weaknesses: List<String>
    ): String {
        val sb = StringBuilder()
        sb.appendLine("Mishkah Multi-Level Student Analytics Report")
        sb.appendLine("Student Name,${student.fullName}")
        sb.appendLine("Email,${student.email}")
        sb.appendLine("Generated At,${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())}")
        sb.appendLine()
        sb.appendLine("Level,Completion %,Average Score %,Time Spent (Min),Total Attempts")
        levelStats.forEach { stat ->
            sb.appendLine("${stat.level.code},${stat.completionPercent}%,${stat.avgScorePercent}%,${stat.totalMinutes},${stat.attemptsCount}")
        }
        sb.appendLine()
        sb.appendLine("Mastered Strengths,${strengths.joinToString(" | ").ifEmpty { "Comprehension" }}")
        sb.appendLine("Areas for Improvement,${weaknesses.joinToString(" | ").ifEmpty { "None" }}")
        sb.appendLine()
        sb.appendLine("Attempt #,Lesson ID,Level,Score %,Correct,Total,Time (Sec)")
        attempts.forEachIndexed { idx, att ->
            sb.appendLine("${idx + 1},${att.lessonId},${att.level},${att.scorePercent}%,${att.correctCount},${att.totalQuestions},${att.timeSpentSeconds}")
        }
        return sb.toString()
    }

    fun writePdfToStream(
        outputStream: OutputStream,
        student: UserEntity,
        levelStats: List<LevelStatSummary>,
        strengths: List<String>,
        weaknesses: List<String>,
        alerts: List<SmartAlertEntity>
    ): Boolean {
        val document = PdfDocument()
        return try {
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val headerPaint = Paint().apply {
                color = Color.rgb(18, 21, 23)
                style = Paint.Style.FILL
            }
            canvas.drawRect(0f, 0f, 595f, 110f, headerPaint)

            val goldTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(212, 175, 55)
                textSize = 22f
                isFakeBoldText = true
            }
            val subHeaderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(246, 241, 227)
                textSize = 12f
            }
            canvas.drawText("MISHKAH — Student Performance Analytics Report", 36f, 48f, goldTitlePaint)
            canvas.drawText("Student: ${student.fullName} (${student.email})", 36f, 74f, subHeaderPaint)
            canvas.drawText("Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())}", 36f, 94f, subHeaderPaint)

            val sectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(18, 110, 81)
                textSize = 15f
                isFakeBoldText = true
            }
            val bodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.rgb(30, 35, 40)
                textSize = 12f
            }

            var y = 150f
            canvas.drawText("1. Three-Level Performance Breakdown (Beginner / Intermediate / Advanced)", 36f, y, sectionPaint)
            y += 26f
            levelStats.forEach { stat ->
                val line = "• ${stat.level.code}: Completion ${stat.completionPercent}% | Avg Score ${stat.avgScorePercent}% | Time ${stat.totalMinutes} min | Attempts ${stat.attemptsCount}"
                canvas.drawText(line, 48f, y, bodyPaint)
                y += 22f
            }

            y += 20f
            canvas.drawText("2. Competency Strengths & Areas for Reinforcement", 36f, y, sectionPaint)
            y += 24f
            canvas.drawText("• Strengths: ${strengths.joinToString(", ").ifEmpty { "Comprehension, Application" }}", 48f, y, bodyPaint)
            y += 22f
            canvas.drawText("• Focus Areas: ${weaknesses.joinToString(", ").ifEmpty { "None detected" }}", 48f, y, bodyPaint)

            y += 30f
            canvas.drawText("3. Smart Pedagogical Alerts & Recommendations", 36f, y, sectionPaint)
            y += 24f
            if (alerts.isEmpty()) {
                canvas.drawText("• All performance indicators are steady and on track.", 48f, y, bodyPaint)
            } else {
                alerts.take(4).forEach { alert ->
                    canvas.drawText("• [${alert.severity}] ${alert.titleEn}", 48f, y, bodyPaint)
                    y += 18f
                    canvas.drawText("  Action: ${alert.recommendationEn.take(85)}", 56f, y, bodyPaint)
                    y += 24f
                }
            }

            document.finishPage(page)
            document.writeTo(outputStream)
            true
        } catch (e: Exception) {
            false
        } finally {
            document.close()
        }
    }

    fun exportToUri(context: Context, uri: Uri, isPdf: Boolean, writeBlock: (OutputStream) -> Unit): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { stream ->
                writeBlock(stream)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
