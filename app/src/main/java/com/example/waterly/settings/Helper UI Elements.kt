package com.example.waterly.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.waterly.ui.theme.WaterlyTypography

@Composable
fun SectionLabel(text: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(modifier = Modifier.padding(start = 10.dp), text = text, style = WaterlyTypography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun SectionSpacer() {
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
fun SaveButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.padding(start = 10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B6FF), contentColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text, style = WaterlyTypography.bodyMedium)
    }
}

@Composable
fun goalInputColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0x2700B4FC),
    unfocusedContainerColor = Color(0x2700B4FC),
    disabledContainerColor = Color(0x2700B4FC),
    errorContainerColor = Color(0x2700B4FC),
    focusedIndicatorColor = Color(0xFF00B4FC),
    unfocusedIndicatorColor = Color.Transparent,
    cursorColor = Color(0xFF00B4FC)
)

@Composable
fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = Color(0xFF00B4FC),
    checkedTrackColor = Color(0x2700B4FC),
    uncheckedThumbColor = Color.LightGray,
    uncheckedTrackColor = Color.Gray
)

@Composable
fun IntervalSelection(selected: Int, options: List<Int>, onSelect: (Int) -> Unit) {
    SectionSpacer()
    SectionLabel("Reminder Interval")
    Row(
        modifier = Modifier.padding(start = 10.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { hours ->
            TextButton(onClick = { onSelect(hours) }) {
                Text(
                    text = "$hours hr",
                    color = if (selected == hours) Color(0xFF00B4FC) else Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuietHoursDropdown(
    label: String,
    value: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    onSelect: (String) -> Unit,
    options: List<String>
) {
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Text("$label", style = WaterlyTypography.bodyLarge, modifier = Modifier.padding(bottom = 10.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { onToggle() }
        ) {
            TextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color(0xFF00B4FC),
                    unfocusedIndicatorColor = Color.Gray,
                    cursorColor = Color(0xFF00B4FC),
                    focusedLabelColor = Color(0xFF00B4FC),
                    focusedContainerColor = Color(0x2700B4FC),
                    unfocusedContainerColor = Color(0x2700B4FC),
                    disabledContainerColor = Color(0x2700B4FC),
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onToggle() }
            ) {
                options.forEach { hour ->
                    DropdownMenuItem(
                        text = { Text(hour) },
                        onClick = {
                            onSelect(hour)
                            onToggle() // <<<<<<<<<<<<<< THIS LINE closes it
                        }
                    )
                }
            }
        }
    }
}

