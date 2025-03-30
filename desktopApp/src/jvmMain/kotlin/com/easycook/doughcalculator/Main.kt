package com.easycook.doughcalculator

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowPosition
import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.repository.InMemoryRecipeRepository
import com.easycook.doughcalculator.resources.Strings
import com.easycook.doughcalculator.ui.screens.CalculatorScreen
import com.easycook.doughcalculator.ui.screens.RecipeListScreen
import com.easycook.doughcalculator.viewmodel.RecipeViewModel
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

sealed class Screen {
    object RecipeList : Screen()
    object NewCalculator : Screen()
    data class EditRecipe(val recipe: DoughRecipe) : Screen()
}

fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(800.dp, 800.dp),
        position = WindowPosition.Aligned(Alignment.Center),
        isMinimized = false
    )
    val repository = InMemoryRecipeRepository()
    val viewModel = RecipeViewModel(repository)
    var currentScreen by remember { mutableStateOf<Screen>(Screen.RecipeList) }

    Window(
        onCloseRequest = ::exitApplication,
        title = Strings.APP_NAME,
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