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
class RecipeViewModel @Inject constructor(private val database: DoughRecipesDatabase) : ViewModel() {

    val recipes = database.dao.getAllRecipes()
    //var recipeEntity: DoughRecipeEntity? = null
    var recipeEntity: DoughRecipeEntity = DoughRecipeEntity()
    //var isFlourEmpty = false
    var isWaterEmpty = false
    var isSaltEmpty = false
    var isWaterValidationWarning = false
    var isSaltValidationError = false
    var isError = false

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
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (recipeEntity.flourGram == 0) {
            isFlourEmpty.value = true
            return@launch
        }
        if (recipeEntity.waterGram == 0) {
            isWaterEmpty.value = true
            return@launch
        }
        if (recipeEntity.saltGram == 0) {
            isSaltEmpty.value = true
            return@launch
        }

    }
}