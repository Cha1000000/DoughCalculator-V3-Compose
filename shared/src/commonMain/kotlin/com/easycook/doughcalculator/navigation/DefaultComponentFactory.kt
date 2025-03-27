package com.easycook.doughcalculator.navigation

import com.arkivanov.decompose.ComponentContext
import org.koin.core.component.KoinComponent

class DefaultComponentFactory : ComponentFactory, KoinComponent {

    override fun createCalculatorComponent(
        componentContext: ComponentContext
    ): CalculatorComponent {
        return CalculatorComponentImpl(componentContext)
    }

    override fun createRecipeListComponent(
        componentContext: ComponentContext,
        onRecipeSelected: (Long) -> Unit
    ): RecipeListComponent {
        return RecipeListComponentImpl(
            componentContext = componentContext,
            onNavigateToCalculator = { /* По умолчанию ничего не делаем */ },
            onRecipeSelected = onRecipeSelected
        )
    }

    override fun createRecipeDetailsComponent(
        componentContext: ComponentContext,
        recipeId: Long,
        onBackClicked: () -> Unit
    ): RecipeDetailsComponent {
        return RecipeDetailsComponentImpl(
            componentContext = componentContext,
            recipeId = recipeId,
            onBackClicked = onBackClicked
        )
    }
} 