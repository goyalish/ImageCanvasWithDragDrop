package com.shutterfly.imagecanvas.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.dimensionResource
import com.shutterfly.imagecanvas.R

/**
 * Composable for displaying a circular progress bar.
 */
@Composable
fun CircularProgressbar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(dimensionResource(R.dimen.x_large_100))
                .padding(dimensionResource(R.dimen.small_16)),
            color = MaterialTheme.colorScheme.outline,
            strokeWidth = dimensionResource(R.dimen.small_8),
            trackColor = MaterialTheme.colorScheme.primary,
            strokeCap = StrokeCap.Round
        )
    }
}