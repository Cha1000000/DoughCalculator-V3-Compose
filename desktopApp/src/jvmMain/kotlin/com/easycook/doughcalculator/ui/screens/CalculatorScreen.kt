package com.easycook.doughcalculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.easycook.doughcalculator.models.DoughRecipe
import com.easycook.doughcalculator.models.IngredientType
import com.easycook.doughcalculator.viewmodel.RecipeViewModel
import com.easycook.doughcalculator.resources.Strings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: RecipeViewModel,
    recipe: DoughRecipe? = null,
    onNavigateBack: () -> Unit
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var recipeName by remember { mutableStateOf(recipe?.name ?: "") }
    var recipeDescription by remember { mutableStateOf(recipe?.description ?: "") }

    val ingredients by viewModel.ingredients.collectAsState()
    val isCalculateByWeight by viewModel.isCalculateByWeight.collectAsState()
    val isWaterValidationWarn by viewModel.isWaterValidationWarn.collectAsState()
    val isSaltValidationError by viewModel.isSaltValidationError.collectAsState()

    LaunchedEffect(recipe) {
        if (recipe != null) {
            viewModel.loadRecipe(recipe)
        } else {
            viewModel.resetRecipe()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(recipe?.name ?: Strings.Navigation.NEW_RECIPE) },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                }
            }
        )

        // Переключатель режима расчёта
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Режим расчёта:")
            Row(modifier = Modifier.padding(start = 16.dp)) {
                RadioButton(
                    selected = isCalculateByWeight,
                    onClick = { viewModel.setCalculationMode(true) }
                )
                Text("По весу", modifier = Modifier.align(Alignment.CenterVertically))
                Spacer(Modifier.width(16.dp))
                RadioButton(
                    selected = !isCalculateByWeight,
                    onClick = { viewModel.setCalculationMode(false) }
                )
                Text("По процентам", modifier = Modifier.align(Alignment.CenterVertically))
            }
        }

        // Заголовки столбцов
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(Strings.Calculator.INGREDIENT, modifier = Modifier.weight(0.20f))
            Text(Strings.Calculator.GRAMS, modifier = Modifier.weight(0.25f))
            Text(Strings.Calculator.PERCENTS, modifier = Modifier.weight(0.25f))
            Text(Strings.Calculator.CORRECTION, modifier = Modifier.weight(0.25f))
        }

        // Список ингредиентов
        ingredients.forEach { ingredient ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (ingredient.ingredient) {
                        IngredientType.Flour -> Strings.Ingredients.FLOUR
                        IngredientType.Water -> Strings.Ingredients.WATER
                        IngredientType.Salt -> Strings.Ingredients.SALT
                        IngredientType.Sugar -> Strings.Ingredients.SUGAR
                        IngredientType.Butter -> Strings.Ingredients.BUTTER
                        IngredientType.Yeast -> Strings.Ingredients.YEAST
                        IngredientType.Milk -> Strings.Ingredients.MILK
                        IngredientType.Egg -> Strings.Ingredients.EGGS
                    },
                    modifier = Modifier.weight(0.20f)
                )

                OutlinedTextField(
                    value = ingredient.quantity,
                    onValueChange = { value ->
                        viewModel.updateIngredient(ingredient.ingredient, value, true)
                    },
                    enabled = isCalculateByWeight || ingredient.ingredient == IngredientType.Flour,
                    modifier = Modifier.weight(0.24f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(0.01f))

                OutlinedTextField(
                    value = ingredient.percent,
                    onValueChange = { value ->
                        viewModel.updateIngredient(ingredient.ingredient, value, false)
                    },
                    enabled = !isCalculateByWeight && ingredient.ingredient != IngredientType.Flour,
                    modifier = Modifier.weight(0.24f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.weight(0.01f))

                OutlinedTextField(
                    value = ingredient.correction,
                    onValueChange = { value ->
                        viewModel.updateCorrection(ingredient.ingredient, value)
                    },
                    enabled = ingredient.ingredient == IngredientType.Flour,
                    modifier = Modifier.weight(0.25f),
                    singleLine = true
                )
            }

            // Отображение предупреждений валидации под полями ввода
            if (ingredient.ingredient == IngredientType.Water && isWaterValidationWarn) {
                Text(
                    text = Strings.Validation.WATER_WARNING,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                )
            }
            if (ingredient.ingredient == IngredientType.Salt && isSaltValidationError) {
                Text(
                    text = Strings.Validation.SALT_ERROR,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showSaveDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (recipe == null) "Сохранить рецепт" else "Обновить рецепт")
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(if (recipe == null) "Сохранить рецепт" else "Обновить рецепт") },
            text = {
                Column {
                    OutlinedTextField(
                        value = recipeName,
                        onValueChange = { recipeName = it },
                        label = { Text("Название рецепта") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = recipeDescription,
                        onValueChange = { recipeDescription = it },
                        label = { Text("Описание (необязательно)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveRecipe(recipeName, recipeDescription)
                        showSaveDialog = false
                        onNavigateBack()
                    },
                    enabled = recipeName.isNotBlank()
                ) {
                    Text(if (recipe == null) "Сохранить" else "Обновить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
} 