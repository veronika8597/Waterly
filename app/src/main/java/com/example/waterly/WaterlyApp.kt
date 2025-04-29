package com.example.waterly

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.waterly.ui.theme.WaterlyTheme

@Composable
fun WaterlyApp() {

    WaterlyTheme{
        WaterlyNavGraph()
    }

}

