package com.easycook.doughcalculator.navigation

import com.arkivanov.decompose.ComponentContext

/**
 * Базовый интерфейс для всех компонентов навигации
 */
interface Component

/**
 * Фабрика для создания компонентов навигации
 */
interface ComponentFactory {
    fun createCalculatorComponent(
        componentContext: ComponentContext
    ): CalculatorComponent

    fun createRecipeListComponent(
        componentContext: ComponentContext,
        onRecipeSelected: (Long) -> Unit
    ): RecipeListComponent

    fun createRecipeDetailsComponent(
        componentContext: ComponentContext,
        recipeId: Long,
        onBackClicked: () -> Unit
    ): RecipeDetailsComponent
} 