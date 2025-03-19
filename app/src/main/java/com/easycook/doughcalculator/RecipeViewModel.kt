package com.easycook.doughcalculator

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easycook.doughcalculator.common.Calculator
import com.easycook.doughcalculator.common.formatToStringOrBlank
import com.easycook.doughcalculator.common.toStringOrBlank
import com.easycook.doughcalculator.database.DoughRecipeEntity
import com.easycook.doughcalculator.database.DoughRecipesDatabase
import com.easycook.doughcalculator.models.IngredientType
import com.easycook.doughcalculator.models.IngredientUiItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class RecipeViewModel @Inject constructor(private val database: DoughRecipesDatabase) :
    ViewModel() {

    private val calculator = Calculator()

    private val _recipeEntity = MutableStateFlow(DoughRecipeEntity())
    val recipeEntity: StateFlow<DoughRecipeEntity> = _recipeEntity.asStateFlow()
    private var savedRecipeOriginal = DoughRecipeEntity()
    private var previousRecipeCalculation = _recipeEntity.value.copy()

    private val _isCalculateByWeight = MutableStateFlow(true)
    val isCalculateByWeight: StateFlow<Boolean> = _isCalculateByWeight.asStateFlow()

    val isFlourEmpty = mutableStateOf(false)
    val isSaltEmpty = mutableStateOf(false)
    val isWaterEmpty = mutableStateOf(false)

    private val _isWaterValidationWarn = MutableStateFlow(false)
    val isWaterValidationWarn: StateFlow<Boolean> = _isWaterValidationWarn.asStateFlow()

    private val _isSaltValidationError = MutableStateFlow(false)
    val isSaltValidationError: StateFlow<Boolean> = _isSaltValidationError.asStateFlow()

    private val _tableIngredientRows = MutableStateFlow(createIngredientTableRows())
    val tableIngredientRows: StateFlow<List<IngredientUiItemModel>> = _tableIngredientRows.asStateFlow()

    private val _recipes = MutableStateFlow<List<DoughRecipeEntity>>(emptyList())
    val recipes: StateFlow<List<DoughRecipeEntity>> = _recipes.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        viewModelScope.launch {
            database.dao.getAllRecipes().collect { recipes ->
                _recipes.value = recipes
            }
        }
    }

    fun updateRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.update(recipe)
    }

    fun deleteRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.delete(recipe)
    }

    fun resetRecipe() {
        _recipeEntity.value = DoughRecipeEntity()
        savedRecipeOriginal = _recipeEntity.value.copy()
    }

    fun resetIngredientTableRows() {
        _tableIngredientRows.value = createIngredientTableRows()
    }

    private fun createIngredientTableRows(): List<IngredientUiItemModel> {
        val recipe = _recipeEntity.value
        val isNewRecipe = recipe.recipeId == null

        // Функция для создания IngredientUiItemModel с общей логикой
        fun createIngredientRow(
            ingredientType: IngredientType,
            gram: Int,
            percent: Double,
            correction: Int
        ): IngredientUiItemModel = IngredientUiItemModel(
            ingredient = ingredientType,
            quantity = mutableStateOf(createTextFieldValue(if (isNewRecipe) "" else gram.toString())),
            percent = mutableStateOf(
                createTextFieldValue(
                    if (isNewRecipe && ingredientType != IngredientType.Flour)
                        ""
                    else if (ingredientType == IngredientType.Flour)
                        "100"
                    else
                        String.format(Locale.getDefault(), "%.0f", percent)
                )
            ),
            correction = mutableStateOf(
                createTextFieldValue(
                    if (isNewRecipe || correction == 0)
                        ""
                    else
                        correction.toString()
                )
            )
        )

        return listOf(
            createIngredientRow(IngredientType.Flour, recipe.flourGram, 100.0, recipe.flourGramCorrection),
            createIngredientRow(IngredientType.Water, recipe.waterGram, recipe.waterPercent, recipe.waterGramCorrection),
            createIngredientRow(IngredientType.Salt, recipe.saltGram, recipe.saltPercent, recipe.saltGramCorrection),
            createIngredientRow(IngredientType.Sugar, recipe.sugarGram, recipe.sugarPercent, recipe.sugarGramCorrection),
            createIngredientRow(IngredientType.Butter, recipe.butterGram, recipe.butterPercent, recipe.butterGramCorrection),
            createIngredientRow(IngredientType.Yeast, recipe.yeastGram, recipe.yeastPercent, recipe.yeastGramCorrection),
            createIngredientRow(IngredientType.Milk, recipe.milkGram, recipe.milkPercent, recipe.milkGramCorrection),
            createIngredientRow(IngredientType.Egg, recipe.eggGram, recipe.eggPercent, recipe.eggGramCorrection)
        )
    }

    private fun updateIngredientTableRows() {
        val recipe = _recipeEntity.value
        val ingredientData = listOf(
            Triple(recipe.waterGram, recipe.waterPercent, recipe.waterGramCorrection),
            Triple(recipe.saltGram, recipe.saltPercent, recipe.saltGramCorrection),
            Triple(recipe.sugarGram, recipe.sugarPercent, recipe.sugarGramCorrection),
            Triple(recipe.butterGram, recipe.butterPercent, recipe.butterGramCorrection),
            Triple(recipe.yeastGram, recipe.yeastPercent, recipe.yeastGramCorrection),
            Triple(recipe.milkGram, recipe.milkPercent, recipe.milkGramCorrection),
            Triple(recipe.eggGram, recipe.eggPercent, recipe.eggGramCorrection)
        )
        
        // Обновляем строки таблицы
        val updatedRows = _tableIngredientRows.value.toMutableList()
        ingredientData.forEachIndexed { index, (gram, percent, correction) ->
            val oldRow = updatedRows[index + 1]
            updatedRows[index + 1] = oldRow.copy(
                quantity = mutableStateOf(createTextFieldValue(gram.toStringOrBlank())),
                percent = mutableStateOf(createTextFieldValue(percent.formatToStringOrBlank())),
                correction = mutableStateOf(createTextFieldValue(correction.toStringOrBlank()))
            )
        }
        
        _tableIngredientRows.value = updatedRows
    }

    // Хелпер, чтобы создать TextFieldValue c курсором в конце
    private fun createTextFieldValue(text: String): TextFieldValue {
        val length = text.length
        return TextFieldValue(text, TextRange(length))
    }

    fun onCalculationClick() {
        if (_recipeEntity.value.flourGram == 0) {
            isFlourEmpty.value = true
            return
        }

        if (_isCalculateByWeight.value) {
            if (_recipeEntity.value.waterGram == 0) {
                isWaterEmpty.value = true
                return
            }
            if (_recipeEntity.value.saltGram == 0) {
                isSaltEmpty.value = true
                return
            }

            // Расчет процентов ингредиентов
            with(_recipeEntity.value) {
                waterPercent = calculator.calculateIngredientPercent(waterGram, flourGram)
                saltPercent = calculator.calculateIngredientPercent(saltGram, flourGram)
                sugarPercent = if (sugarGram == 0) 0.0 else calculator.calculateIngredientPercent(sugarGram, flourGram)
                butterPercent = if (butterGram == 0) 0.0 else calculator.calculateIngredientPercent(butterGram, flourGram)
                yeastPercent = if (yeastGram == 0) 0.0 else calculator.calculateIngredientPercent(yeastGram, flourGram)
                milkPercent = if (milkGram == 0) 0.0 else calculator.calculateIngredientPercent(milkGram, flourGram)
                eggPercent = if (eggGram == 0) 0.0 else calculator.calculateIngredientPercent(eggGram, flourGram)
            }
        } else {
            if (_recipeEntity.value.waterPercent == 0.0) {
                isWaterEmpty.value = true
                return
            }
            if (_recipeEntity.value.saltPercent == 0.0) {
                isSaltEmpty.value = true
                return
            }

            // Расчёт веса в граммах ингредиентов
            with(_recipeEntity.value) {
                if (waterPercent != previousRecipeCalculation.waterPercent) {
                    waterGram = calculator.calculateIngredientGram(waterPercent, flourGram)
                }
                if (saltPercent != previousRecipeCalculation.saltPercent) {
                    saltGram = calculator.calculateIngredientGram(saltPercent, flourGram)
                }
                if (sugarPercent != previousRecipeCalculation.sugarPercent) {
                    sugarGram = if (sugarPercent == 0.0) 0 else calculator.calculateIngredientGram(sugarPercent, flourGram)
                }
                if (butterPercent != previousRecipeCalculation.butterPercent) {
                    butterGram = if (butterPercent == 0.0) 0 else calculator.calculateIngredientGram(butterPercent, flourGram)
                }
                if (yeastPercent != previousRecipeCalculation.yeastPercent) {
                    yeastGram = if (yeastPercent == 0.0) 0 else calculator.calculateIngredientGram(yeastPercent, flourGram)
                }
                if (milkPercent != previousRecipeCalculation.milkPercent) {
                    milkGram = if (milkPercent == 0.0) 0 else calculator.calculateIngredientGram(milkPercent, flourGram)
                }
                if (eggPercent != previousRecipeCalculation.eggPercent) {
                    eggGram = if (eggPercent == 0.0) 0 else calculator.calculateIngredientGram(eggPercent, flourGram)
                }
            }
        }

        recalculateGrams()

        // Используем Calculator для валидации
        _isWaterValidationWarn.value = !calculator.isWaterPercentValid(_recipeEntity.value.waterPercent)
        _isSaltValidationError.value = !calculator.isSaltPercentValid(_recipeEntity.value.saltPercent)

        // Обновляем UI после всех изменений
        updateIngredientTableRows()
        saveCalculationState()
    }

    private fun recalculateGrams() {
        if (_recipeEntity.value.flourGramCorrection > 0) {
            val flourGramCorrection = _recipeEntity.value.flourGramCorrection
            val ingredients = with(_recipeEntity.value) {
                val ingredientsList = listOf(
                    Triple(IngredientType.Water, waterPercent, ::waterGramCorrection),
                    Triple(IngredientType.Salt, saltPercent, ::saltGramCorrection),
                    Triple(IngredientType.Sugar, sugarPercent, ::sugarGramCorrection),
                    Triple(IngredientType.Butter, butterPercent, ::butterGramCorrection),
                    Triple(IngredientType.Egg, eggPercent, ::eggGramCorrection),
                    Triple(IngredientType.Yeast, yeastPercent, ::yeastGramCorrection),
                    Triple(IngredientType.Milk, milkPercent, ::milkGramCorrection)
                )
                listOfNotNull(
                    *ingredientsList.map { (_, percent, correction) ->
                        if (correction.get() == 0 || 
                            percent != previousRecipeCalculation.run { 
                                when (correction) {
                                    ::waterGramCorrection -> waterPercent
                                    ::saltGramCorrection -> saltPercent
                                    ::sugarGramCorrection -> sugarPercent
                                    ::butterGramCorrection -> butterPercent
                                    ::eggGramCorrection -> eggPercent
                                    ::yeastGramCorrection -> yeastPercent
                                    ::milkGramCorrection -> milkPercent
                                    else -> 0.0
                                }
                            } || 
                            flourGramCorrection != previousRecipeCalculation.flourGramCorrection
                        ) {
                            calculator.recalculateIngredientGram(percent, flourGramCorrection) to { v: Int -> correction.set(v) }
                        } else null
                    }.toTypedArray()
                )
            }
            
            ingredients.forEach { pair -> 
                val (value, setter) = pair
                setter(value) 
            }
        } else {
            with(_recipeEntity.value) {
                if (waterGramCorrection != 0) waterGramCorrection = 0
                if (saltGramCorrection != 0) saltGramCorrection = 0
                if (sugarGramCorrection != 0) sugarGramCorrection = 0
                if (butterGramCorrection != 0) butterGramCorrection = 0
                if (eggGramCorrection != 0) eggGramCorrection = 0
                if (yeastGramCorrection != 0) yeastGramCorrection = 0
                if (milkGramCorrection != 0) milkGramCorrection = 0
            }
        }
    }

    /**
     * Сохранение состояния последних расчётов
     */
    fun saveCalculationState() {
        previousRecipeCalculation = _recipeEntity.value.copy()
    }

    /**
     * Переключение режима расчётов (вес/процент)
     */
    fun toggleCalculationMode() {
        _isCalculateByWeight.value = !_isCalculateByWeight.value
    }

    fun onSaveClick(title: String, description: String) {
        val recipe = _recipeEntity.value.copy(title = title, description = description)
        val isNewRecipe = recipe.recipeId == null
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (isNewRecipe) database.dao.insert(recipe) else database.dao.update(recipe)
                // Очищаем сообщение об ошибке при успешном сохранении
                _errorMessage.value = null
            } catch (e: Exception) {
                Timber.e(e, "Ошибка при ${if (isNewRecipe) "создании" else "обновлении"} рецепта")
                _errorMessage.value = "Не удалось сохранить рецепт: ${e.localizedMessage ?: e.message ?: "Неизвестная ошибка"}"
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            recipes.collectLatest { updatedRecipes ->
                if (updatedRecipes.isEmpty()) return@collectLatest
                val currentRecipe = if (isNewRecipe) {
                    updatedRecipes.lastOrNull()
                } else {
                    updatedRecipes.find { it.recipeId == recipe.recipeId }
                }
                currentRecipe?.let {
                    _recipeEntity.value = it
                    savedRecipeOriginal = it.copy()
                }
            }
        }
    }

    fun hasUnsavedChanges(): Boolean {
        return _recipeEntity.value != savedRecipeOriginal
    }

    fun refreshSavedRecipeOriginalState() {
        savedRecipeOriginal = _recipeEntity.value.copy()
    }

    fun setRecipeEntity(recipe: DoughRecipeEntity) {
        _recipeEntity.value = recipe
    }
}