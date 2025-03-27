package com.easycook.doughcalculator.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.easycook.doughcalculator.domain.IngredientType
import com.easycook.doughcalculator.domain.model.Recipe

interface CalculatorComponent {
    val state: Value<CalculatorState>
    
    fun onFlourValueChanged(value: String)
    fun onWaterValueChanged(value: String)
    fun onSaltValueChanged(value: String)
    fun onYeastValueChanged(value: String)
    fun onSugarValueChanged(value: String)
    fun onOilValueChanged(value: String)
    fun onWeightValueChanged(value: String)
    fun onHydrationValueChanged(value: String)
    fun onRecipeSelected(recipe: Recipe)
    fun onCalculate()
}

data class CalculatorState(
    val flour: String = "",
    val water: String = "",
    val salt: String = "",
    val yeast: String = "",
    val sugar: String = "",
    val oil: String = "",
    val weight: String = "",
    val hydration: String = "",
    val ingredients: Map<IngredientType, Double> = emptyMap()
)

class CalculatorComponentImpl(
    componentContext: ComponentContext
) : CalculatorComponent, ComponentContext by componentContext {
    
    private val _state = MutableValue(CalculatorState())
    override val state: Value<CalculatorState> = _state
    
    override fun onFlourValueChanged(value: String) {
        _state.value = _state.value.copy(flour = value)
    }
    
    override fun onWaterValueChanged(value: String) {
        _state.value = _state.value.copy(water = value)
    }
    
    override fun onSaltValueChanged(value: String) {
        _state.value = _state.value.copy(salt = value)
    }
    
    override fun onYeastValueChanged(value: String) {
        _state.value = _state.value.copy(yeast = value)
    }
    
    override fun onSugarValueChanged(value: String) {
        _state.value = _state.value.copy(sugar = value)
    }
    
    override fun onOilValueChanged(value: String) {
        _state.value = _state.value.copy(oil = value)
    }
    
    override fun onWeightValueChanged(value: String) {
        _state.value = _state.value.copy(weight = value)
    }
    
    override fun onHydrationValueChanged(value: String) {
        _state.value = _state.value.copy(hydration = value)
    }
    
    override fun onRecipeSelected(recipe: Recipe) {
        // Реализация обработки выбора рецепта
    }
    
    override fun onCalculate() {
        // Реализация расчета ингредиентов
        // В реальном приложении здесь должна быть бизнес-логика
        val calculatedIngredients = mapOf(
            IngredientType.FLOUR to 100.0,
            IngredientType.WATER to 70.0,
            IngredientType.SALT to 2.0,
            IngredientType.YEAST to 1.0
        )
        
        _state.value = _state.value.copy(ingredients = calculatedIngredients)
    }
} 