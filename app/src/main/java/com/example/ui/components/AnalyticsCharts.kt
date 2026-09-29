package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.EducationLevel
import com.example.domain.LevelStatSummary
import com.example.i18n.LocalAppStrings
import com.example.ui.theme.LevelAdvancedRuby
import com.example.ui.theme.LevelBeginnerEmerald
import com.example.ui.theme.LevelIntermediateGold
import com.example.ui.theme.MishkahEmeraldLight
import com.example.ui.theme.MishkahGoldPrimary
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ThreeLevelBarChart(
    stats: List<LevelStatSummary>,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.28f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendDot(color = MishkahGoldPrimary, label = strings.bestScoreLabel)
            LegendDot(color = MishkahEmeraldLight, label = strings.completionLabel)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height
            val chartBottom = height - 16f
            val chartTop = 12f
            val usableHeight = (chartBottom - chartTop).coerceAtLeast(1f)

            // Draw 4 horizontal reference lines (25%, 50%, 75%, 100%)
            for (i in 0..4) {
                val y = chartBottom - (usableHeight * (i / 4f))
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.5f
                )
            }

            val groupCount = stats.size.coerceAtLeast(1)
            val groupWidth = width / groupCount
            val barWidth = (groupWidth * 0.26f).coerceAtMost(48f)

            stats.forEachIndexed { index, stat ->
                val centerX = groupWidth * index + groupWidth / 2f
                val scoreRatio = (stat.avgScorePercent.coerceIn(0, 100)) / 100f
                val completionRatio = (stat.completionPercent.coerceIn(0, 100)) / 100f

                val scoreBarHeight = usableHeight * scoreRatio
                val compBarHeight = usableHeight * completionRatio

                // Score Bar (Gold)
                drawRoundRect(
                    color = MishkahGoldPrimary,
                    topLeft = Offset(centerX - barWidth - 4f, chartBottom - scoreBarHeight),
                    size = Size(barWidth, scoreBarHeight.coerceAtLeast(6f)),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Completion Bar (Emerald)
                drawRoundRect(
                    color = MishkahEmeraldLight,
                    topLeft = Offset(centerX + 4f, chartBottom - compBarHeight),
                    size = Size(barWidth, compBarHeight.coerceAtLeast(6f)),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            stats.forEach { stat ->
                val label = when (stat.level) {
                    EducationLevel.BEGINNER -> strings.levelBeginner
                    EducationLevel.INTERMEDIATE -> strings.levelIntermediate
                    EducationLevel.ADVANCED -> strings.levelAdvanced
                }
                val badgeColor = when (stat.level) {
                    EducationLevel.BEGINNER -> LevelBeginnerEmerald
                    EducationLevel.INTERMEDIATE -> LevelIntermediateGold
                    EducationLevel.ADVANCED -> LevelAdvancedRuby
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor
                    )
                    Text(
                        text = "${stat.avgScorePercent}% • ${stat.totalMinutes}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ScoreProgressionLineChart(
    scores: List<Int>,
    modifier: Modifier = Modifier
) {
    val effectiveScores = if (scores.isEmpty()) listOf(65, 75, 82) else scores
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            val w = size.width
            val h = size.height
            val padX = 24f
            val padY = 20f
            val usableW = (w - padX * 2).coerceAtLeast(1f)
            val usableH = (h - padY * 2).coerceAtLeast(1f)

            for (i in 0..3) {
                val y = padY + usableH * (i / 3f)
                drawLine(
                    color = gridColor,
                    start = Offset(padX, y),
                    end = Offset(w - padX, y),
                    strokeWidth = 1.2f
                )
            }

            val points = effectiveScores.mapIndexed { idx, score ->
                val x = if (effectiveScores.size == 1) {
                    w / 2f
                } else {
                    padX + usableW * (idx.toFloat() / (effectiveScores.size - 1))
                }
                val y = padY + usableH * (1f - (score.coerceIn(0, 100) / 100f))
                Offset(x, y)
            }

            if (points.size >= 2) {
                val fillPath = Path().apply {
                    moveTo(points.first().x, h - padY)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, h - padY)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MishkahGoldPrimary.copy(alpha = 0.38f),
                            MishkahEmeraldLight.copy(alpha = 0.04f)
                        )
                    )
                )

                val linePath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    points.drop(1).forEach { lineTo(it.x, it.y) }
                }
                drawPath(
                    path = linePath,
                    color = MishkahGoldPrimary,
                    style = Stroke(width = 5f, cap = StrokeCap.Round)
                )
            }

            points.forEach { pt ->
                drawCircle(
                    color = MishkahEmeraldLight,
                    radius = 9f,
                    center = pt
                )
                drawCircle(
                    color = MishkahGoldPrimary,
                    radius = 5f,
                    center = pt
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            effectiveScores.takeLast(6).forEachIndexed { idx, sc ->
                Text(
                    text = "#${idx + 1}: $sc%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CompetencyRadarChart(
    skillScores: List<Pair<String, Int>>, // 5 skills -> 0..100
    modifier: Modifier = Modifier
) {
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.32f)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val radius = min(centerX, centerY) * 0.78f
            val count = skillScores.size.coerceAtLeast(3)

            // Draw concentric pentagons
            for (ring in 1..4) {
                val r = radius * (ring / 4f)
                val ringPath = Path()
                for (i in 0 until count) {
                    val angle = Math.toRadians((-90.0 + (360.0 / count) * i))
                    val x = centerX + (r * cos(angle)).toFloat()
                    val y = centerY + (r * sin(angle)).toFloat()
                    if (i == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
                }
                ringPath.close()
                drawPath(ringPath, color = gridColor, style = Stroke(width = 1.5f))
            }

            // Draw spokes
            for (i in 0 until count) {
                val angle = Math.toRadians((-90.0 + (360.0 / count) * i))
                val x = centerX + (radius * cos(angle)).toFloat()
                val y = centerY + (radius * sin(angle)).toFloat()
                drawLine(
                    color = gridColor,
                    start = Offset(centerX, centerY),
                    end = Offset(x, y),
                    strokeWidth = 1.2f
                )
            }

            // Draw student competency polygon
            val dataPath = Path()
            val vertices = mutableListOf<Offset>()
            skillScores.forEachIndexed { i, (_, value) ->
                val ratio = (value.coerceIn(15, 100)) / 100f
                val r = radius * ratio
                val angle = Math.toRadians((-90.0 + (360.0 / count) * i))
                val x = centerX + (r * cos(angle)).toFloat()
                val y = centerY + (r * sin(angle)).toFloat()
                vertices.add(Offset(x, y))
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            drawPath(
                path = dataPath,
                color = MishkahEmeraldLight.copy(alpha = 0.32f)
            )
            drawPath(
                path = dataPath,
                color = MishkahGoldPrimary,
                style = Stroke(width = 4f)
            )
            vertices.forEach { pt ->
                drawCircle(color = MishkahGoldPrimary, radius = 6f, center = pt)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        // Skill pills below radar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            skillScores.take(3).forEach { (label, score) ->
                SkillScoreChip(label = label, score = score)
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            skillScores.drop(3).forEach { (label, score) ->
                SkillScoreChip(label = label, score = score)
            }
        }
    }
}

@Composable
private fun SkillScoreChip(label: String, score: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$label: $score%",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
