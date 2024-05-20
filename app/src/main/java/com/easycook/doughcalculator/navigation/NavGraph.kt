package com.easycook.doughcalculator.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.easycook.doughcalculator.common.CALCULATION_SCREEN
import com.easycook.doughcalculator.common.RECIPES_SCREEN
import com.easycook.doughcalculator.common.SAVE_RECIPE_SCREEN
import com.easycook.doughcalculator.screens.RecipesScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    //paddingValues: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = RECIPES_SCREEN
    ) {
        composable(RECIPES_SCREEN) {
            RecipesScreen(navController)
        }
        composable(CALCULATION_SCREEN) {
            //CalculationScreen(navController)
        }
        composable(SAVE_RECIPE_SCREEN) {
            //SaveRecipeScreen(navController)
        }
    }
}