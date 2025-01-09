package com.easycook.doughcalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlin.math.roundToInt

@HiltViewModel
class RecipeViewModel @Inject constructor(private val database: DoughRecipesDatabase) :
    ViewModel() {

    var recipeEntity by mutableStateOf(DoughRecipeEntity())
    private var savedRecipeOriginal by mutableStateOf(DoughRecipeEntity())

    val isCalculateByWeight = mutableStateOf(true)
    val isFlourEmpty = mutableStateOf(false)
    val isSaltEmpty = mutableStateOf(false)
    val isWaterEmpty = mutableStateOf(false)
    val isWaterValidationWarn = mutableStateOf(false)
    val isSaltValidationError = mutableStateOf(false)

    private val _tableIngredientRows = mutableStateOf(createIngredientTableRows())
    val tableIngredientRows: List<IngredientUiItemModel> get() = _tableIngredientRows.value

    private val _recipes = MutableStateFlow<List<DoughRecipeEntity>>(emptyList())
    val recipes: StateFlow<List<DoughRecipeEntity>> = _recipes.asStateFlow()

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
        recipeEntity = DoughRecipeEntity()
        savedRecipeOriginal = recipeEntity.clone()
    }

    fun resetIngredientTableRows() {
        _tableIngredientRows.value = createIngredientTableRows()
    }

    private fun createIngredientTableRows(): List<IngredientUiItemModel> {
        val recipe = recipeEntity
        val isNewRecipe = recipeEntity.recipeId == null

        return listOf(
            IngredientUiItemModel(
                ingredient = IngredientType.Flour,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.flourGram.toString())),
                percent = mutableStateOf(TextFieldValue("100", TextRange(3))), // "100"
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.flourGramCorrection == 0)
                            ""
                        else
                            recipe.flourGramCorrection.toString()
                    )
                )
            ),
            IngredientUiItemModel(
                ingredient = IngredientType.Water,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.waterGram.toString())),
                percent = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe)
                            ""
                        else
                            String.format(Locale.getDefault(), "%.0f", recipe.waterPercent)
                    )
                ),
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.waterGramCorrection == 0)
                            ""
                        else
                            recipe.waterGramCorrection.toString()
                    )
                ),
            ),
            IngredientUiItemModel(
                ingredient = IngredientType.Salt,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.saltGram.toString())),
                percent = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe)
                            ""
                        else
                            String.format(Locale.getDefault(), "%.0f", recipe.saltPercent)
                    )
                ),
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.saltGramCorrection == 0)
                            ""
                        else
                            recipe.saltGramCorrection.toString()
                    )
                ),
            ),
            IngredientUiItemModel(
                ingredient = IngredientType.Sugar,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.sugarGram.toString())),
                percent = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe)
                            ""
                        else
                            String.format(Locale.getDefault(), "%.0f", recipe.sugarPercent)
                    )
                ),
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.sugarGramCorrection == 0)
                            ""
                        else
                            recipe.sugarGramCorrection.toString()
                    )
                ),
            ),
            IngredientUiItemModel(
                ingredient = IngredientType.Butter,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.butterGram.toString())),
                percent = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe)
                            ""
                        else
                            String.format(Locale.getDefault(), "%.0f", recipe.butterPercent)
                    )
                ),
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.butterGramCorrection == 0)
                            ""
                        else
                            recipe.butterGramCorrection.toString()
                    )
                ),
            ),
            IngredientUiItemModel(
                ingredient = IngredientType.Yeast,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.yeastGram.toString())),
                percent = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe)
                            ""
                        else
                            String.format(Locale.getDefault(), "%.0f", recipe.yeastPercent)
                    )
                ),
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.yeastGramCorrection == 0)
                            ""
                        else
                            recipe.yeastGramCorrection.toString()
                    )
                ),
            ),
            IngredientUiItemModel(
                ingredient = IngredientType.Milk,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.milkGram.toString())),
                percent = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe)
                            ""
                        else
                            String.format(Locale.getDefault(), "%.0f", recipe.milkPercent)
                    )
                ),
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.milkGramCorrection == 0)
                            ""
                        else
                            recipe.milkGramCorrection.toString()
                    )
                ),
            ),
            IngredientUiItemModel(
                ingredient = IngredientType.Egg,
                quantity = mutableStateOf(toTextFieldValue(if (isNewRecipe) "" else recipe.eggGram.toString())),
                percent = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe)
                            ""
                        else
                            String.format(Locale.getDefault(), "%.0f", recipe.eggPercent)
                    )
                ),
                correction = mutableStateOf(
                    toTextFieldValue(
                        if (isNewRecipe || recipe.eggGramCorrection == 0)
                            ""
                        else
                            recipe.eggGramCorrection.toString()
                    )
                ),
            ),
        )
    }

    private fun updateIngredientTableRows() {
        val recipe = recipeEntity
        val newRows = listOf(
            Triple(recipe.waterGram, recipe.waterPercent, recipe.waterGramCorrection),
            Triple(recipe.saltGram, recipe.saltPercent, recipe.saltGramCorrection),
            Triple(recipe.sugarGram, recipe.sugarPercent, recipe.sugarGramCorrection),
            Triple(recipe.butterGram, recipe.butterPercent, recipe.butterGramCorrection),
            Triple(recipe.yeastGram, recipe.yeastPercent, recipe.yeastGramCorrection),
            Triple(recipe.milkGram, recipe.milkPercent, recipe.milkGramCorrection),
            Triple(recipe.eggGram, recipe.eggPercent, recipe.eggGramCorrection),
        ).mapIndexed { index, (gram, percent, correction) ->
            val oldRow = _tableIngredientRows.value[index + 1]
            oldRow.copy(
                quantity = mutableStateOf(toTextFieldValue(gram.toStringOrBlank())),
                percent = mutableStateOf(toTextFieldValue(percent.formatToStringOrBlank())),
                correction = mutableStateOf(toTextFieldValue(correction.toStringOrBlank()))
            )
        }
        _tableIngredientRows.value = _tableIngredientRows.value
            .toMutableList()
            .apply {
                for (i in newRows.indices) {
                    this[i + 1] = newRows[i]
                }
            }
    }

    // Хелпер, чтобы создать TextFieldValue c курсором в конце
    private fun toTextFieldValue(text: String): TextFieldValue {
        val length = text.length
        return TextFieldValue(text, TextRange(length))
    }

    fun onCalculationClick() {
        if (recipeEntity.flourGram == 0) {
            isFlourEmpty.value = true
            return
        }

        if (isCalculateByWeight.value) {
            if (recipeEntity.waterGram == 0) {
                isWaterEmpty.value = true
                return
            }
            if (recipeEntity.saltGram == 0) {
                isSaltEmpty.value = true
                return
            }

            calculateWaterPercent()
            calculateSaltPercent()
            calculateSugarPercent()
            calculateButterPercent()
            calculateYeastPercent()
            calculateMilkPercent()
            calculateEggPercent()
        } else {
            if (recipeEntity.waterPercent == 0.0) {
                isWaterEmpty.value = true
                return
            }
            if (recipeEntity.saltPercent == 0.0) {
                isSaltEmpty.value = true
                return
            }

            calculateWaterGram()
            calculateSaltGram()
            calculateSugarGram()
            calculateButterGram()
            calculateYeastGram()
            calculateMilkGram()
            calculateEggGram()
        }

        recalculateGrams()

        isWaterValidationWarn.value = recipeEntity.waterPercent !in 59.5..80.0
        isSaltValidationError.value = recipeEntity.saltPercent > 2.5

        updateIngredientTableRows()
    }

    private fun calculateWaterPercent() {
        recipeEntity.waterPercent = calculateIngredientPercent(recipeEntity.waterGram)
    }

    private fun calculateSaltPercent() {
        recipeEntity.saltPercent = calculateIngredientPercent(recipeEntity.saltGram)
    }

    private fun calculateSugarPercent() {
        if (recipeEntity.sugarGram == 0) {
            recipeEntity.sugarPercent = 0.0
            return
        }
        recipeEntity.sugarPercent = calculateIngredientPercent(recipeEntity.sugarGram)
    }

    private fun calculateButterPercent() {
        if (recipeEntity.butterGram == 0) {
            recipeEntity.butterPercent = 0.0
            return
        }
        recipeEntity.butterPercent = calculateIngredientPercent(recipeEntity.butterGram)
    }

    private fun calculateEggPercent() {
        if (recipeEntity.eggGram == 0) {
            recipeEntity.eggPercent = 0.0
            return
        }
        recipeEntity.eggPercent = calculateIngredientPercent(recipeEntity.eggGram)
    }

    private fun calculateYeastPercent() {
        if (recipeEntity.yeastGram == 0) {
            recipeEntity.yeastPercent = 0.0
            return
        }
        recipeEntity.yeastPercent = calculateIngredientPercent(recipeEntity.yeastGram)
    }

    private fun calculateMilkPercent() {
        if (recipeEntity.milkGram == 0) {
            recipeEntity.milkPercent = 0.0
            return
        }
        recipeEntity.milkPercent = calculateIngredientPercent(recipeEntity.milkGram)
    }

    private fun calculateIngredientPercent(gram: Int): Double =
        gram * 100.00 / recipeEntity.flourGram

    private fun calculateWaterGram() {
        recipeEntity.waterGram = calculateIngredientGram(recipeEntity.waterPercent)
    }

    private fun calculateSaltGram() {
        recipeEntity.saltGram = calculateIngredientGram(recipeEntity.saltPercent)
    }

    private fun calculateSugarGram() {
        recipeEntity.sugarGram = calculateIngredientGram(recipeEntity.sugarPercent)
    }

    private fun calculateButterGram() {
        recipeEntity.butterGram = calculateIngredientGram(recipeEntity.butterPercent)
    }

    private fun calculateEggGram() {
        recipeEntity.eggGram = calculateIngredientGram(recipeEntity.eggPercent)
    }

    private fun calculateYeastGram() {
        recipeEntity.yeastGram = calculateIngredientGram(recipeEntity.yeastPercent)
    }

    private fun calculateMilkGram() {
        recipeEntity.milkGram = calculateIngredientGram(recipeEntity.milkPercent)
    }

    private fun calculateIngredientGram(prc: Double): Int =
        (recipeEntity.flourGram * prc / 100).roundToInt()

    private fun recalculateGrams() {
        if (recipeEntity.flourGramCorrection > 0) {
            recalculateWaterGram()
            recalculateSaltGram()
            recalculateSugarGram()
            recalculateButterGram()
            recalculateEggGram()
            recalculateYeastGram()
            recalculateMilkGram()
        } else {
            if (recipeEntity.waterGramCorrection != 0) recipeEntity.waterGramCorrection = 0
            if (recipeEntity.saltGramCorrection != 0) recipeEntity.saltGramCorrection = 0
            if (recipeEntity.sugarGramCorrection != 0) recipeEntity.sugarGramCorrection = 0
            if (recipeEntity.butterGramCorrection != 0) recipeEntity.butterGramCorrection = 0
            if (recipeEntity.eggGramCorrection != 0) recipeEntity.eggGramCorrection = 0
            if (recipeEntity.yeastGramCorrection != 0) recipeEntity.yeastGramCorrection = 0
            if (recipeEntity.milkGramCorrection != 0) recipeEntity.milkGramCorrection = 0
        }
    }

    private fun recalculateWaterGram() {
        val prc = recipeEntity.waterPercent
        val newGram = recalculateIngredientGram(prc, recipeEntity.flourGramCorrection)
        recipeEntity.waterGramCorrection = newGram
    }

    private fun recalculateSaltGram() {
        val prc = recipeEntity.saltPercent
        val newGram = recalculateIngredientGram(prc, recipeEntity.flourGramCorrection)
        recipeEntity.saltGramCorrection = newGram
    }

    private fun recalculateSugarGram() {
        val prc = recipeEntity.sugarPercent
        val newGram = recalculateIngredientGram(prc, recipeEntity.flourGramCorrection)
        recipeEntity.sugarGramCorrection = newGram
    }

    private fun recalculateButterGram() {
        val prc = recipeEntity.butterPercent
        val newGram = recalculateIngredientGram(prc, recipeEntity.flourGramCorrection)
        recipeEntity.butterGramCorrection = newGram
    }

    private fun recalculateEggGram() {
        val prc = recipeEntity.eggPercent
        val newGram = recalculateIngredientGram(prc, recipeEntity.flourGramCorrection)
        recipeEntity.eggGramCorrection = newGram
    }

    private fun recalculateYeastGram() {
        val prc = recipeEntity.yeastPercent
        val newGram = recalculateIngredientGram(prc, recipeEntity.flourGramCorrection)
        recipeEntity.yeastGramCorrection = newGram
    }

    private fun recalculateMilkGram() {
        val prc = recipeEntity.milkPercent
        val newGram = recalculateIngredientGram(prc, recipeEntity.flourGramCorrection)
        recipeEntity.milkGramCorrection = newGram
    }

    private fun recalculateIngredientGram(percent: Double, newFlourWeight: Int): Int {
        return (newFlourWeight * percent / 100.00).toInt()
    }

    fun onSaveClick(title: String, description: String) {
        if (title.isEmpty()) return
        val recipe = recipeEntity.copy(title = title, description = description)
        val isNewRecipe = recipe.recipeId == null
        viewModelScope.launch(Dispatchers.IO) {
            if (isNewRecipe) database.dao.insert(recipe) else database.dao.update(recipe)
        }
        viewModelScope.launch(Dispatchers.Main) {
            recipes.collectLatest { updatedRecipes ->
                if (updatedRecipes.isEmpty()) return@collectLatest
                val currentRecipe = if (isNewRecipe) {
                    updatedRecipes.last()
                } else {
                    updatedRecipes.find { it.recipeId == recipe.recipeId }
                }
                currentRecipe?.let {
                    recipeEntity = it
                    savedRecipeOriginal = it.clone()
                }
            }
        }
    }

    fun hasUnsavedChanges(): Boolean {
        return recipeEntity != savedRecipeOriginal
    }

    fun refreshSavedRecipeOriginalState() {
        savedRecipeOriginal = recipeEntity.clone()
    }

    private fun DoughRecipeEntity.clone(): DoughRecipeEntity {
        val clone = this::class.java.getDeclaredConstructor().newInstance()
        this::class.java.declaredFields.forEach { field ->
            field.isAccessible = true
            field.set(clone, field.get(this))
        }
        return clone as DoughRecipeEntity
    }
}