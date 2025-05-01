package com.example.financetracker.ui.components


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PieChart(
    data: List<PieChart.Slice>,
    modifier: Modifier = Modifier,
    strokeWidth: Float = 40f,
    animDuration: Int = 1000,
) {
    val totalValue = data.sumOf { it.value }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(key1 = data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animDuration)
        )
    }

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(200.dp)
        ) {
            val canvasSize = size.minDimension
            val radius = canvasSize / 2 - strokeWidth
            val center = size / 2f

            var startAngle = -90f // Start from top (12 o'clock position)

            data.forEach { slice ->
                val sweepAngle = (slice.value / totalValue * 360f) * animatedProgress.value

//                drawArc(
//                    color = slice.color,
//                    startAngle = startAngle,
//                    sweepAngle = sweepAngle,
//                    useCenter = false,
//                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
//                )

                drawArc(
                    color = slice.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle.toFloat(),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                startAngle += sweepAngle.toFloat()
            }
        }

        if (data.isEmpty()) {
            Text(
                text = "Tidak ada data",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        } else {
            Text(
                text = "${(animatedProgress.value * 100).toInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

object PieChart {
    data class Slice(
        val value: Double,
        val color: Color,
        val name: String = ""
    )
}
