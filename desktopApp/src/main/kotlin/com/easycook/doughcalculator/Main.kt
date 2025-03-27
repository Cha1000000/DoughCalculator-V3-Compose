package com.easycook.doughcalculator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.easycook.doughcalculator.repository.InMemoryRecipeRepository
import com.easycook.doughcalculator.viewmodel.RecipeViewModel
import com.easycook.doughcalculator.ui.screens.CalculatorScreen

fun main() = application {
    val windowState = rememberWindowState()
    val repository = InMemoryRecipeRepository()
    val viewModel = RecipeViewModel(repository)

    Window(
        onCloseRequest = ::exitApplication,
        title = "Калькулятор теста",
        state = windowState
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                CalculatorScreen(viewModel)
            }
        }
    }
} 