package com.easycook.doughcalculator.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

interface RecipeDetailsComponent {
    val state: Value<RecipeDetailsState>
    
    fun onBackClicked()
    fun onEditClicked()
    fun onCopyToCalculatorClicked()
}

data class RecipeDetailsState(
    val recipeId: Long = 0,
    val name: String = "",
    val description: String = "",
    val hydration: Double = 0.0,
    val ingredients: List<IngredientItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class IngredientItem(
    val type: String,
    val amount: Double,
    val unit: String
)

class RecipeDetailsComponentImpl(
    componentContext: ComponentContext,
    recipeId: Long,
    private val onBackClicked: () -> Unit
) : RecipeDetailsComponent, ComponentContext by componentContext {
    
    private val _state = MutableValue(RecipeDetailsState(
        recipeId = recipeId,
        name = "Тестовый рецепт",
        description = "Описание тестового рецепта",
        hydration = 70.0,
        ingredients = listOf(
            IngredientItem("Мука", 1000.0, "г"),
            IngredientItem("Вода", 700.0, "г"),
            IngredientItem("Соль", 20.0, "г"),
            IngredientItem("Дрожжи", 10.0, "г")
        )
    ))
    
    override val state: Value<RecipeDetailsState> = _state
    
    override fun onBackClicked() {
        onBackClicked.invoke()
    }
    
    override fun onEditClicked() {
        // В реальном приложении здесь будет переход к экрану редактирования
    }
    
    override fun onCopyToCalculatorClicked() {
        // В реальном приложении здесь будет копирование в калькулятор
    }
} 