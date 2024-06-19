package com.easycook.doughcalculator

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easycook.doughcalculator.database.DoughRecipeEntity
import com.easycook.doughcalculator.database.DoughRecipesDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(private val database: DoughRecipesDatabase) :
    ViewModel() {

    val recipes = database.dao.getAllRecipes()

    //var recipeEntity: DoughRecipeEntity? = null
    var recipeEntity: DoughRecipeEntity = DoughRecipeEntity()

    fun insertRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.insert(recipe)
    }

    fun updateRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.update(recipe)
    }

    fun deleteRecipe(recipe: DoughRecipeEntity) = viewModelScope.launch(Dispatchers.IO) {
        database.dao.delete(recipe)
    }

    fun onCalculationClicked(
        /*recipe: DoughRecipeEntity,*/
        isCalculateByWeight: Boolean,
        isFlourEmpty: MutableState<Boolean>,
        isWaterEmpty: MutableState<Boolean>,
        isSaltEmpty: MutableState<Boolean>,
        isWaterValidationWarning: MutableState<Boolean>,
        isSaltValidationError: MutableState<Boolean>,
        //isError: MutableState<Boolean>
    ) {
        if (recipeEntity.flourGram == 0) {
            isFlourEmpty.value = true
            return
        }
        if (recipeEntity.waterGram == 0) {
            isWaterEmpty.value = true
            return
        }
        if (recipeEntity.saltGram == 0) {
            isSaltEmpty.value = true
            return
        }

        if (isCalculateByWeight) {
            calculateWaterPercent(isWaterValidationWarning)
            calculateSaltPercent(isSaltValidationError)
            calculateSugarPercent()
            calculateButterPercent()
            calculateEggPercent()
            calculateYeastPercent()
            calculateMilkPercent()

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
        } else {

        }
    }

    private fun calculateWaterPercent(isWaterValidationWarning: MutableState<Boolean>) {
        recipeEntity.waterPercent = calculateIngredientPercent(recipeEntity.waterGram)
        isWaterValidationWarning.value = recipeEntity.waterPercent !in 59.5..80.0
    }

    private fun calculateSaltPercent(isSaltValidationError: MutableState<Boolean>) {
        recipeEntity.saltPercent = calculateIngredientPercent(recipeEntity.saltGram)
        isSaltValidationError.value = recipeEntity.saltPercent > 2.5
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

    private fun calculateIngredientPercent(gram: Int): Double {
        return (gram * 100.00 / recipeEntity.flourGram)
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
}