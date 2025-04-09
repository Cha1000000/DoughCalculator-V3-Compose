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
                _recipes.value = recipes.sortedByDescending { it.isFavorite }
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
                quantity = recipe.flourGram.toStringOrEmpty(),
                percent = "100",
                correction = recipe.flourGramCorrection.toStringOrEmpty()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Water,
                quantity = recipe.waterGram.toStringOrEmpty(),
                percent = recipe.waterPercent.toStringOrEmpty(),
                correction = recipe.waterGramCorrection.toStringOrEmpty()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Salt,
                quantity = recipe.saltGram.toStringOrEmpty(),
                percent = recipe.saltPercent.toStringOrEmpty(),
                correction = recipe.saltGramCorrection.toStringOrEmpty()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Sugar,
                quantity = recipe.sugarGram.toStringOrEmpty(),
                percent = recipe.sugarPercent.toStringOrEmpty(),
                correction = recipe.sugarGramCorrection.toStringOrEmpty()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Butter,
                quantity = recipe.butterGram.toStringOrEmpty(),
                percent = recipe.butterPercent.toStringOrEmpty(),
                correction = recipe.butterGramCorrection.toStringOrEmpty()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Yeast,
                quantity = recipe.yeastGram.toStringOrEmpty(),
                percent = recipe.yeastPercent.toStringOrEmpty(),
                correction = recipe.yeastGramCorrection.toStringOrEmpty()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Milk,
                quantity = recipe.milkGram.toStringOrEmpty(),
                percent = recipe.milkPercent.toStringOrEmpty(),
                correction = recipe.milkGramCorrection.toStringOrEmpty()
            ),
            IngredientUiModel(
                ingredient = IngredientType.Egg,
                quantity = recipe.eggGram.toStringOrEmpty(),
                percent = recipe.eggPercent.toStringOrEmpty(),
                correction = recipe.eggGramCorrection.toStringOrEmpty()
            )
        )
    }

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
                ingredient.copy(quantity = ((flourWeight * percent) / 100).toStringOrEmpty())
            } else {
                ingredient
            }
        }
        _ingredients.value = updatedIngredients
    }

    fun applyCorrections() {
        val flourIndex = _ingredients.value.indexOfFirst { it.ingredient == IngredientType.Flour }
        if (flourIndex == -1) return
        
        val ingredients = _ingredients.value
        val flourCorrection = ingredients[flourIndex].correction
        val flourCorrectionValue = flourCorrection.toDoubleOrNull()
        if (flourCorrectionValue == null || flourCorrectionValue <= 0) {
            val updatedIngredients = ingredients.map { it.copy(correction = "") }
            _ingredients.value = updatedIngredients
            return
        }
        
        // Рассчитываем коррекции для других ингредиентов на основе коррекции муки
        val updatedIngredients = ingredients.map { ingredient ->
            if (ingredient.ingredient != IngredientType.Flour) {
                val percent = ingredient.percent.toDoubleOrNull() ?: 0.0
                if (percent > 0) {
                    val correctionValue = (flourCorrectionValue * percent / 100)
                    ingredient.copy(correction = correctionValue.toStringOrEmpty())
                } else {
                    ingredient.copy(correction = "")
                }
            } else {
                ingredient
            }
        }
        
        _ingredients.value = updatedIngredients
    }

    private fun Double.toStringOrEmpty(): String = if (this > 0.0) this.formatToTwoDecimalPlaces() else ""
    
    private fun Double.formatToTwoDecimalPlaces(): String = "%.2f".format(this).replace(",", ".").replace(".00", "")

    fun saveRecipe(name: String, description: String = "") {
        val ingredients = _ingredients.value
        val flour = ingredients.find { it.ingredient == IngredientType.Flour }
        
        val recipe = DoughRecipe(
            id = currentRecipe?.id,
            name = name,
            description = description,
            isFavorite = currentRecipe?.isFavorite == true,
            flourGram = flour?.quantity?.toDoubleOrNull() ?: 0.0,
            waterGram = ingredients.find { it.ingredient == IngredientType.Water }?.quantity?.toDoubleOrNull() ?: 0.0,
            saltGram = ingredients.find { it.ingredient == IngredientType.Salt }?.quantity?.toDoubleOrNull() ?: 0.0,
            sugarGram = ingredients.find { it.ingredient == IngredientType.Sugar }?.quantity?.toDoubleOrNull() ?: 0.0,
            butterGram = ingredients.find { it.ingredient == IngredientType.Butter }?.quantity?.toDoubleOrNull() ?: 0.0,
            yeastGram = ingredients.find { it.ingredient == IngredientType.Yeast }?.quantity?.toDoubleOrNull() ?: 0.0,
            milkGram = ingredients.find { it.ingredient == IngredientType.Milk }?.quantity?.toDoubleOrNull() ?: 0.0,
            eggGram = ingredients.find { it.ingredient == IngredientType.Egg }?.quantity?.toDoubleOrNull() ?: 0.0,
            waterPercent = ingredients.find { it.ingredient == IngredientType.Water }?.percent?.toDoubleOrNull() ?: 0.0,
            saltPercent = ingredients.find { it.ingredient == IngredientType.Salt }?.percent?.toDoubleOrNull() ?: 0.0,
            sugarPercent = ingredients.find { it.ingredient == IngredientType.Sugar }?.percent?.toDoubleOrNull() ?: 0.0,
            butterPercent = ingredients.find { it.ingredient == IngredientType.Butter }?.percent?.toDoubleOrNull() ?: 0.0,
            yeastPercent = ingredients.find { it.ingredient == IngredientType.Yeast }?.percent?.toDoubleOrNull() ?: 0.0,
            milkPercent = ingredients.find { it.ingredient == IngredientType.Milk }?.percent?.toDoubleOrNull() ?: 0.0,
            eggPercent = ingredients.find { it.ingredient == IngredientType.Egg }?.percent?.toDoubleOrNull() ?: 0.0,
            flourGramCorrection = flour?.correction?.toDoubleOrNull() ?: 0.0,
            waterGramCorrection = ingredients.find { it.ingredient == IngredientType.Water }?.correction?.toDoubleOrNull() ?: 0.0,
            saltGramCorrection = ingredients.find { it.ingredient == IngredientType.Salt }?.correction?.toDoubleOrNull() ?: 0.0,
            sugarGramCorrection = ingredients.find { it.ingredient == IngredientType.Sugar }?.correction?.toDoubleOrNull() ?: 0.0,
            butterGramCorrection = ingredients.find { it.ingredient == IngredientType.Butter }?.correction?.toDoubleOrNull() ?: 0.0,
            yeastGramCorrection = ingredients.find { it.ingredient == IngredientType.Yeast }?.correction?.toDoubleOrNull() ?: 0.0,
            milkGramCorrection = ingredients.find { it.ingredient == IngredientType.Milk }?.correction?.toDoubleOrNull() ?: 0.0,
            eggGramCorrection = ingredients.find { it.ingredient == IngredientType.Egg }?.correction?.toDoubleOrNull() ?: 0.0
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