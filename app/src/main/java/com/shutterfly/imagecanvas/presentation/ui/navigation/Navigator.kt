package com.shutterfly.imagecanvas.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shutterfly.imagecanvas.presentation.ui.screens.CanvasScreen

@Composable
fun Navigator(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "canvas") {
        composable("canvas") {
            CanvasScreen(modifier)
        }
    }
}