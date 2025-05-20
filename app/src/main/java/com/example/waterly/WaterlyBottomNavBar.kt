package com.example.waterly

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun WaterlyBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(Icons.Default.History, Icons.Default.Home, Icons.Default.Settings)
    val labels = listOf("Statistics", "Home", "Settings")

    val barHeight = 70.dp
    val dipDepth = 32.dp

    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val itemWidth = screenWidthDp / items.size
    val dipX by animateDpAsState(
        targetValue = (selectedIndex + 0.5f) * (screenWidthDp / items.size.toFloat()).dp,
        label = ""
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight)
        ) {
            val width = size.width
            val height = size.height
            val dipCenter = dipX.toPx()
            val dipRadius = 170f
            val dipDepthPx = dipDepth.toPx()

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(dipCenter - dipRadius, 0f)
                cubicTo(
                    dipCenter - dipRadius / 2, 0f,
                    dipCenter - dipRadius / 2, dipDepthPx,
                    dipCenter, dipDepthPx
                )
                cubicTo(
                    dipCenter + dipRadius / 2, dipDepthPx,
                    dipCenter + dipRadius / 2, 0f,
                    dipCenter + dipRadius, 0f
                )
                lineTo(width, 0f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = path,
                color = Color(0xFFAFE6FB)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            items.forEachIndexed { index, icon ->
                val isSelected = index == selectedIndex

                val iconSize by animateDpAsState(
                    if (isSelected) 48.dp else 30.dp, label = ""
                )
                val offsetY by animateDpAsState(
                    if (isSelected) (-36).dp else 0.dp, label = ""
                )
                val bubbleColor by animateColorAsState(
                    if (isSelected) Color(0xFF00B4FC) else Color.Transparent, label = ""
                )
                val iconColor by animateColorAsState(
                    if (isSelected) Color.White else Color.Gray, label = ""
                )
                val bubbleScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f, label = ""
                )

                Box(
                    modifier = Modifier
                        .width(itemWidth.dp)
                        .fillMaxHeight()
                        .clickable { onItemSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(iconSize * bubbleScale)
                                .offset(y = offsetY),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(bubbleColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = labels[index],
                                    tint = iconColor
                                )
                            }
                        }

                        if (!isSelected) {
                            Text(
                                text = labels[index],
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 100)
@Composable
fun WaterlyBottomBarPreview() {
    var selected by remember { mutableStateOf(1) }

    WaterlyBottomNavBar(
        selectedIndex = selected,
        onItemSelected = { selected = it }
    )
}
