import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.waterly.ui.theme.WaterlyTypography

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
            .border(2.dp, Color.Gray, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val waterLevel = height * (1f - animatedProgress)

            // Light wave path
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

            // Dark wave path
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

            // Draw light wave
            clipPath(lightWavePath) {
                drawRect(Color(0xFFAFE6FB)) // Light blue
            }

            // Draw dark wave
            clipPath(darkWavePath) {
                drawRect(Color(0xFF00B4FC)) // Darker blue
            }
        }

        // Center text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$waterIntake ml",
                style = WaterlyTypography.displayLarge,
                color = Color.Black
            )
            Text(
                text = "/ $waterGoal ml",
                style = WaterlyTypography.headlineMedium,
                color = Color.Black
            )
        }
    }
}

@Preview
@Composable
fun WaterFillCirclePreview() {
    WaterFillCircle(waterIntake = 1000, waterGoal = 2000)
}
