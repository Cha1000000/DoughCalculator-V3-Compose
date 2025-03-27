package com.easycook.doughcalculator.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

interface RecipeListComponent {
    val state: Value<RecipeListState>
    
    fun onRecipeClicked(recipeId: Long)
    fun onNewRecipeClicked()
    fun onCalculatorTabClicked()
}

data class RecipeListState(
    val recipes: List<RecipeItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class RecipeItem(
    val id: Long,
    val name: String,
    val description: String,
    val hydration: Double
)

class RecipeListComponentImpl(
    componentContext: ComponentContext,
    private val onNavigateToCalculator: () -> Unit,
    private val onRecipeSelected: (Long) -> Unit
) : RecipeListComponent, ComponentContext by componentContext {
    
    private val _state = MutableValue(RecipeListState(
        recipes = listOf(
            RecipeItem(1, "Багет", "Классический французский багет", 75.0),
            RecipeItem(2, "Чиабатта", "Итальянский хлеб с высокой гидратацией", 85.0),
            RecipeItem(3, "Пицца", "Тесто для пиццы", 65.0)
        )
    ))
    
    override val state: Value<RecipeListState> = _state
    
    override fun onRecipeClicked(recipeId: Long) {
        onRecipeSelected(recipeId)
    }
    
    override fun onNewRecipeClicked() {
        // В реальном приложении здесь должен быть переход к экрану создания рецепта
    }
    
    override fun onCalculatorTabClicked() {
        onNavigateToCalculator()
    }
} 