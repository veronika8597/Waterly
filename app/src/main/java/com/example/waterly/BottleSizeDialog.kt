package com.example.waterly

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.waterly.ui.theme.WaterlyTypography

@Composable
fun BottleSizeDialog(
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFFEEF5FF),
        title = {
            Text(
                text = "Select Bottle Size",
                style = WaterlyTypography.bodyLarge, // ✅ Make title BIG but not too crazy
                color = Color.Black
            )
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                listOf(500, 750, 1000).forEach { size ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onSelect(size) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.water_bottle),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "$size ml",
                            style = WaterlyTypography.bodyMedium, // ✅ Better than labelSmall (bigger for 500ml, 750ml)
                            color = Color.Black,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}


