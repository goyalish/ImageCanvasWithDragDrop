package com.shutterfly.imagecanvas.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(1f),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Failed to fetch the data", style = MaterialTheme.typography.headlineMedium)
    }
}