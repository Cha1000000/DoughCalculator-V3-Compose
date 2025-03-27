package com.easycook.doughcalculator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.repository.InMemoryRecipeRepository
import com.easycook.doughcalculator.ui.screens.CalculatorScreen
import com.easycook.doughcalculator.ui.screens.RecipeListScreen
import com.easycook.doughcalculator.viewmodel.RecipeViewModel

sealed class Screen {
    object RecipeList : Screen()
    object NewCalculator : Screen()
    data class EditRecipe(val recipe: DoughRecipe) : Screen()
}

fun main() = application {
    val windowState = rememberWindowState()
    val repository = InMemoryRecipeRepository()
    val viewModel = RecipeViewModel(repository)
    var currentScreen by remember { mutableStateOf<Screen>(Screen.RecipeList) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Калькулятор теста",
        state = windowState
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier,
                color = MaterialTheme.colorScheme.background
            ) {
                when (val screen = currentScreen) {
                    is Screen.RecipeList -> RecipeListScreen(
                        viewModel = viewModel,
                        onNavigateToCalculator = { currentScreen = Screen.NewCalculator },
                        onNavigateToRecipe = { recipe -> currentScreen = Screen.EditRecipe(recipe) }
                    )
                    is Screen.NewCalculator -> CalculatorScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = Screen.RecipeList }
                    )
                    is Screen.EditRecipe -> CalculatorScreen(
                        viewModel = viewModel,
                        recipe = screen.recipe,
                        onNavigateBack = { currentScreen = Screen.RecipeList }
                    )
                }
            }
        }
    }
} 