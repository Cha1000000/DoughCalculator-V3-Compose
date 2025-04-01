package com.easycook.doughcalculator

import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowPlacement
import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.repository.FileRecipeRepository
import com.easycook.doughcalculator.resources.Strings
import com.easycook.doughcalculator.resources.StringsDesktop
import com.easycook.doughcalculator.ui.screens.CalculatorScreen
import com.easycook.doughcalculator.ui.screens.RecipeListScreen
import com.easycook.doughcalculator.viewmodel.RecipeViewModel
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import java.io.File

sealed class Screen {
    object RecipeList : Screen()
    object NewCalculator : Screen()
    data class EditRecipe(val recipe: DoughRecipe) : Screen()
}

fun main() = application {
    // Создаем файл для хранения рецептов в директории приложения
    val appDir = File("recipes")
    val dataFile = File(appDir, "recipes.json")
    
    // Создаем директорию, если ее нет
    appDir.mkdirs()
    
    println(StringsDesktop.Console.SEPARATOR)
    println(StringsDesktop.Console.RECIPES_FILE_PATH.format(dataFile.absolutePath))
    println(StringsDesktop.Console.SEPARATOR)

    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(600.dp, 820.dp)
    )
    
    val repository = FileRecipeRepository(dataFile)
    val viewModel = RecipeViewModel(repository)
    var currentScreen by remember { mutableStateOf<Screen>(Screen.RecipeList) }

    Window(
        onCloseRequest = ::exitApplication,
        title = Strings.APP_NAME,
        state = windowState,
        resizable = true,
    ) {
        MaterialTheme {
            Surface(
                modifier = Modifier.sizeIn(minWidth = 600.dp, minHeight = 800.dp),
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