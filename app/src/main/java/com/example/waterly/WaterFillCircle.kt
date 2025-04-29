import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun WaterFillCircle(waterIntake: Int, waterGoal: Int) {
    val targetProgress = (waterIntake / waterGoal.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetProgress)

    val darkWaveShift = remember { Animatable(0f) }
    val lightWaveShift = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            darkWaveShift.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(3500, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            darkWaveShift.snapTo(0f)
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            lightWaveShift.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
            lightWaveShift.snapTo(0f)
        }
    }

    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val waterLevel = height * (1f - animatedProgress)

            // First: Light wave path (under)
            val lightWavePath = Path().apply {
                moveTo(0f, waterLevel)
                val waveLength = width / 6.5f
                val amplitude = 10.dp.toPx()
                var x = 0f
                while (x <= width) {
                    val y = waterLevel + (amplitude * animatedProgress) * kotlin.math.cos((x / waveLength - lightWaveShift.value * 2f * Math.PI).toFloat())
                    lineTo(x, y)
                    x += 1f
                }
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            // Then: Dark wave path (above)
            val darkWavePath = Path().apply {
                moveTo(0f, waterLevel)
                val waveLength = width / 6f
                val amplitude = 14.dp.toPx()
                var x = 0f
                while (x <= width) {
                    val y = waterLevel + (amplitude * animatedProgress) * kotlin.math.sin((x / waveLength + darkWaveShift.value * 2f * Math.PI).toFloat())
                    lineTo(x, y)
                    x += 1f
                }
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            // Draw light wave first
            clipPath(lightWavePath) {
                drawRect(Color(0xFFAFE6FB)) // Light blue
            }

            // Draw dark wave second (on top)
            clipPath(darkWavePath) {
                drawRect(Color(0xFF00B4FC)) // Darker blue
            }
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "$waterIntake ml",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Black
            )
            Text(
                "/ $waterGoal ml",
                color = Color.Black
            )
        }
    }
}
