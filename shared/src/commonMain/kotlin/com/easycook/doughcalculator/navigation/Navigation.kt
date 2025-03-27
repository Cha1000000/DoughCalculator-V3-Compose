package com.easycook.doughcalculator.navigation

// Простая модель навигации
sealed class Screen {
    object Calculator : Screen()
    object RecipeList : Screen()
    data class RecipeDetails(val recipeId: Long) : Screen()
}

class Navigator {
    private var _currentScreen: Screen = Screen.Calculator
    val currentScreen: Screen get() = _currentScreen
    
    private val _backStack = mutableListOf<Screen>()
    
    fun navigateTo(screen: Screen) {
        val current = _currentScreen
        if (current != screen) {
            _backStack.add(current)
            _currentScreen = screen
        }
    }
    
    fun navigateBack(): Boolean {
        return if (_backStack.isNotEmpty()) {
            _currentScreen = _backStack.removeAt(_backStack.size - 1)
            true
        } else {
            false
        }
    }
    
    // Вспомогательные функции для навигации
    fun navigateToCalculator() {
        navigateTo(Screen.Calculator)
    }
    
    fun navigateToRecipeList() {
        navigateTo(Screen.RecipeList)
    }
    
    fun navigateToRecipeDetails(recipeId: Long) {
        navigateTo(Screen.RecipeDetails(recipeId))
    }
} 