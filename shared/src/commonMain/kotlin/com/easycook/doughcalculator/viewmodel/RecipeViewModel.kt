package com.easycook.doughcalculator.viewmodel

import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.models.IngredientType
import com.easycook.doughcalculator.models.IngredientUiModel
import com.easycook.doughcalculator.repository.DoughRecipeRepository
import com.easycook.doughcalculator.calculator.Calculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val repository: DoughRecipeRepository
) {
    private val calculator = Calculator()
    private val scope = CoroutineScope(Dispatchers.Main)
    private val _ingredients = MutableStateFlow(createDefaultIngredients())
    val ingredients: StateFlow<List<IngredientUiModel>> = _ingredients.asStateFlow()

    private val _recipes = MutableStateFlow<List<DoughRecipe>>(emptyList())
    val recipes: StateFlow<List<DoughRecipe>> = _recipes.asStateFlow()

    private val _isCalculateByWeight = MutableStateFlow(true)
    val isCalculateByWeight: StateFlow<Boolean> = _isCalculateByWeight.asStateFlow()

    private val _isWaterValidationWarn = MutableStateFlow(false)
    val isWaterValidationWarn: StateFlow<Boolean> = _isWaterValidationWarn.asStateFlow()
    
    private val _isSaltValidationError = MutableStateFlow(false)
    val isSaltValidationError: StateFlow<Boolean> = _isSaltValidationError.asStateFlow()

    private var currentRecipe: DoughRecipe? = null

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        scope.launch {
            repository.getAllRecipes().collectLatest { recipes ->
                _recipes.value = recipes
            }
        }
    }

    fun loadRecipe(recipe: DoughRecipe) {
        currentRecipe = recipe
        _ingredients.value = createIngredientsFromRecipe(recipe)
    }

    fun resetRecipe() {
        currentRecipe = null
        _ingredients.value = createDefaultIngredients()
    }

    private fun createDefaultIngredients(): List<IngredientUiModel> {
        return IngredientType.entries.map { type ->
            IngredientUiModel(
                ingredient = type,
                quantity = "",
                percent = if (type == IngredientType.Flour) "100" else "",
                correction = ""
            )
        }
    }

    private fun createIngredientsFromRecipe(recipe: DoughRecipe): List<IngredientUiModel> {
        return listOf(
            IngredientUiModel(
                ingredient = IngredientType.Flour,
                quantity = recipe.flourGram.toString(),
                percent = "100",
                correction = recipe.flourGramCorrection.toString()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Water,
                quantity = recipe.waterGram.toString(),
                percent = recipe.waterPercent.toStringOrEmpty(),
                correction = recipe.waterGramCorrection.toString()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Salt,
                quantity = recipe.saltGram.toString(),
                percent = recipe.saltPercent.toStringOrEmpty(),
                correction = recipe.saltGramCorrection.toString()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Sugar,
                quantity = recipe.sugarGram.toString(),
                percent = recipe.sugarPercent.toStringOrEmpty(),
                correction = recipe.sugarGramCorrection.toString()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Butter,
                quantity = recipe.butterGram.toString(),
                percent = recipe.butterPercent.toStringOrEmpty(),
                correction = recipe.butterGramCorrection.toString()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Yeast,
                quantity = recipe.yeastGram.toString(),
                percent = recipe.yeastPercent.toStringOrEmpty(),
                correction = recipe.yeastGramCorrection.toString()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Milk,
                quantity = recipe.milkGram.toString(),
                percent = recipe.milkPercent.toStringOrEmpty(),
                correction = recipe.milkGramCorrection.toString()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Egg,
                quantity = recipe.eggGram.toString(),
                percent = recipe.eggPercent.toStringOrEmpty(),
                correction = recipe.eggGramCorrection.toString()
            )
        )
    }

    private fun Double.toStringOrEmpty(): String = if (this > 0.0) this.toString() else ""

    fun setCalculationMode(isWeight: Boolean) {
        _isCalculateByWeight.value = isWeight
    }

    fun updateIngredient(type: IngredientType, value: String, isWeight: Boolean) {
        val updatedIngredients = _ingredients.value.map { ingredient ->
            if (ingredient.ingredient == type) {
                if (isWeight) {
                    ingredient.copy(quantity = value)
                } else {
                    ingredient.copy(percent = value)
                }
            } else {
                ingredient
            }
        }
        _ingredients.value = updatedIngredients
        calculate()
    }

    fun updateCorrection(type: IngredientType, value: String) {
        val updatedIngredients = _ingredients.value.map { ingredient ->
            if (ingredient.ingredient == type) {
                ingredient.copy(correction = value)
            } else {
                ingredient
            }
        }
        _ingredients.value = updatedIngredients
        calculate()
    }

    private fun calculate() {
        if (_isCalculateByWeight.value) {
            calculatePercentages()
        } else {
            calculateWeights()
        }
        applyCorrections()
        
        // Валидация процентов воды и соли
        val water = _ingredients.value.find { it.ingredient == IngredientType.Water }
        val salt = _ingredients.value.find { it.ingredient == IngredientType.Salt }

        val waterPercent = water?.percent?.toDoubleOrNull()
        val saltPercent = salt?.percent?.toDoubleOrNull()

        _isWaterValidationWarn.value = waterPercent?.let { !calculator.isWaterPercentValid(it) } == true
        _isSaltValidationError.value = saltPercent?.let { !calculator.isSaltPercentValid(it) } == true
    }

    private fun calculatePercentages() {
        val flour = _ingredients.value.find { it.ingredient == IngredientType.Flour }
        val flourWeight = flour?.quantity?.toDoubleOrNull() ?: return

        val updatedIngredients = _ingredients.value.map { ingredient ->
            if (ingredient.ingredient != IngredientType.Flour) {
                val weight = ingredient.quantity.toDoubleOrNull() ?: 0.0
                ingredient.copy(percent = ((weight / flourWeight) * 100).toStringOrEmpty())
            } else {
                ingredient
            }
        }
        _ingredients.value = updatedIngredients
    }

    private fun calculateWeights() {
        val flour = _ingredients.value.find { it.ingredient == IngredientType.Flour }
        val flourWeight = flour?.quantity?.toDoubleOrNull() ?: return

        val updatedIngredients = _ingredients.value.map { ingredient ->
            if (ingredient.ingredient != IngredientType.Flour) {
                val percent = ingredient.percent.toDoubleOrNull() ?: 0.0
                ingredient.copy(quantity = ((flourWeight * percent) / 100).toString())
            } else {
                ingredient
            }
        }
        _ingredients.value = updatedIngredients
    }

    private fun applyCorrections() {
        val flour = _ingredients.value.find { it.ingredient == IngredientType.Flour }
        val correction = flour?.correction?.toDoubleOrNull() ?: return

        val updatedIngredients = _ingredients.value.map { ingredient ->
            if (ingredient.ingredient != IngredientType.Flour) {
                val percent = ingredient.percent.toDoubleOrNull() ?: 0.0
                ingredient.copy(
                    correction = ((correction * percent) / 100).toString()
                )
            } else {
                ingredient
            }
        }
        _ingredients.value = updatedIngredients
    }

    fun saveRecipe(name: String, description: String = "") {
        val recipe = DoughRecipe(
            id = currentRecipe?.id,
            name = name,
            description = description,
            isFavorite = currentRecipe?.isFavorite == false,
            flourGram = _ingredients.value.find { it.ingredient == IngredientType.Flour }?.quantity?.toIntOrNull() ?: 0,
            waterGram = _ingredients.value.find { it.ingredient == IngredientType.Water }?.quantity?.toIntOrNull() ?: 0,
            saltGram = _ingredients.value.find { it.ingredient == IngredientType.Salt }?.quantity?.toIntOrNull() ?: 0,
            sugarGram = _ingredients.value.find { it.ingredient == IngredientType.Sugar }?.quantity?.toIntOrNull() ?: 0,
            butterGram = _ingredients.value.find { it.ingredient == IngredientType.Butter }?.quantity?.toIntOrNull() ?: 0,
            yeastGram = _ingredients.value.find { it.ingredient == IngredientType.Yeast }?.quantity?.toIntOrNull() ?: 0,
            milkGram = _ingredients.value.find { it.ingredient == IngredientType.Milk }?.quantity?.toIntOrNull() ?: 0,
            eggGram = _ingredients.value.find { it.ingredient == IngredientType.Egg }?.quantity?.toIntOrNull() ?: 0,
            waterPercent = _ingredients.value.find { it.ingredient == IngredientType.Water }?.percent?.toDoubleOrNull() ?: 0.0,
            saltPercent = _ingredients.value.find { it.ingredient == IngredientType.Salt }?.percent?.toDoubleOrNull() ?: 0.0,
            sugarPercent = _ingredients.value.find { it.ingredient == IngredientType.Sugar }?.percent?.toDoubleOrNull() ?: 0.0,
            butterPercent = _ingredients.value.find { it.ingredient == IngredientType.Butter }?.percent?.toDoubleOrNull() ?: 0.0,
            yeastPercent = _ingredients.value.find { it.ingredient == IngredientType.Yeast }?.percent?.toDoubleOrNull() ?: 0.0,
            milkPercent = _ingredients.value.find { it.ingredient == IngredientType.Milk }?.percent?.toDoubleOrNull() ?: 0.0,
            eggPercent = _ingredients.value.find { it.ingredient == IngredientType.Egg }?.percent?.toDoubleOrNull() ?: 0.0,
            flourGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Flour }?.correction?.toIntOrNull() ?: 0,
            waterGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Water }?.correction?.toIntOrNull() ?: 0,
            saltGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Salt }?.correction?.toIntOrNull() ?: 0,
            sugarGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Sugar }?.correction?.toIntOrNull() ?: 0,
            butterGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Butter }?.correction?.toIntOrNull() ?: 0,
            yeastGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Yeast }?.correction?.toIntOrNull() ?: 0,
            milkGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Milk }?.correction?.toIntOrNull() ?: 0,
            eggGramCorrection = _ingredients.value.find { it.ingredient == IngredientType.Egg }?.correction?.toIntOrNull() ?: 0
        )

        if (currentRecipe == null) {
            repository.saveRecipe(recipe)
        } else {
            repository.updateRecipe(recipe)
        }
        loadRecipes()
    }

    fun updateRecipe(recipe: DoughRecipe) {
        repository.updateRecipe(recipe)
        loadRecipes()
    }

    fun deleteRecipe(id: Int) {
        repository.deleteRecipe(id)
        loadRecipes()
    }
} 