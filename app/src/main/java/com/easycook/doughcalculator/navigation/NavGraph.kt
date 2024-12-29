package com.easycook.doughcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.common.CALCULATION_SCREEN
import com.easycook.doughcalculator.common.RECIPES_SCREEN
import com.easycook.doughcalculator.common.SAVE_RECIPE_SCREEN
import com.easycook.doughcalculator.screens.CalculationScreen
import com.easycook.doughcalculator.screens.RecipesScreen
import com.easycook.doughcalculator.screens.SaveRecipeScreen

@Composable
fun NavGraph(navController: NavHostController, viewModel: RecipeViewModel) {
    NavHost(navController = navController, startDestination = RECIPES_SCREEN) {
        composable(RECIPES_SCREEN) {
            RecipesScreen(navController, viewModel)
        }
        composable(CALCULATION_SCREEN) {
            CalculationScreen(navController, viewModel)
        }
        composable(SAVE_RECIPE_SCREEN) {
            SaveRecipeScreen(navController, viewModel)
        }
    }
}