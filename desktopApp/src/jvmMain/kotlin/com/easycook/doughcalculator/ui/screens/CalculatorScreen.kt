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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: RecipeViewModel,
    recipe: DoughRecipe? = null,
    onNavigateBack: () -> Unit
) {
    // Функция для фильтрации числовых значений (неотрицательные числа с точностью до 2 знаков)
    fun filterNumericInput(input: String): String {
        // Удаляем точку или запятую, если это первый символ
        val sanitizedInput = if (input.startsWith(".") || input.startsWith(",")) {
            input.substring(1)
        } else {
            input
        }
        
        return sanitizedInput.replace(",", ".")  // Заменяем запятые на точки
            .replace(Regex("[^0-9.]"), "")  // Удаляем все символы кроме цифр и точки
            .let {
                if (it.count { c -> c == '.' } > 1) {  // Если больше одной точки
                    val firstDotIndex = it.indexOf('.')
                    it.substring(0, firstDotIndex + 1) + it.substring(firstDotIndex + 1).replace(".", "")
                } else {
                    it
                }
            }
            .let {
                if (it.contains(".")) {  // Ограничиваем до 2 знаков после точки
                    val parts = it.split(".")
                    parts[0] + "." + parts[1].take(2)
                } else {
                    it
                }
            }
    }

    var showSaveDialog by remember { mutableStateOf(false) }
    var recipeName by remember { mutableStateOf(recipe?.name ?: "") }
    var recipeDescription by remember { mutableStateOf(recipe?.description ?: "") }

    val ingredients by viewModel.ingredients.collectAsState()
    val isCalculateByWeight by viewModel.isCalculateByWeight.collectAsState()
    val isWaterValidationWarn by viewModel.isWaterValidationWarn.collectAsState()
    val isSaltValidationError by viewModel.isSaltValidationError.collectAsState()

    val scrollState = rememberScrollState()

    LaunchedEffect(recipe) {
        if (recipe != null) {
            viewModel.loadRecipe(recipe)
        } else {
            viewModel.resetRecipe()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .verticalScroll(scrollState),
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
                Text(Strings.Calculator.MODE)
                Row(modifier = Modifier.padding(start = 16.dp)) {
                    RadioButton(
                        selected = isCalculateByWeight,
                        onClick = { viewModel.setCalculationMode(true) }
                    )
                    Text(Strings.Calculator.BY_WEIGHT, modifier = Modifier.align(Alignment.CenterVertically))
                    Spacer(Modifier.width(16.dp))
                    RadioButton(
                        selected = !isCalculateByWeight,
                        onClick = { viewModel.setCalculationMode(false) }
                    )
                    Text(Strings.Calculator.BY_PERCENTS, modifier = Modifier.align(Alignment.CenterVertically))
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
                            viewModel.updateIngredient(ingredient.ingredient, filterNumericInput(value), true)
                        },
                        enabled = isCalculateByWeight || ingredient.ingredient == IngredientType.Flour,
                        modifier = Modifier.weight(0.24f),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.weight(0.01f))

                    OutlinedTextField(
                        value = ingredient.percent,
                        onValueChange = { value ->
                            viewModel.updateIngredient(ingredient.ingredient, filterNumericInput(value), false)
                        },
                        enabled = !isCalculateByWeight && ingredient.ingredient != IngredientType.Flour,
                        modifier = Modifier.weight(0.24f),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.weight(0.01f))

                    OutlinedTextField(
                        value = ingredient.correction,
                        onValueChange = { value ->
                            viewModel.updateCorrection(ingredient.ingredient, filterNumericInput(value))
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

            // Отображение описания рецепта
            recipe?.description?.takeIf { it.isNotBlank() }?.let { description ->
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = Strings.Recipe.DESCRIPTION,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Кнопка сохранения/обновления всегда внизу
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(top = 12.dp),
            shadowElevation = 6.dp,
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface
        ) {
            Button(
                onClick = { showSaveDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .height(52.dp)
            ) {
                Text(
                    text = if (recipe == null) Strings.Recipe.SAVE_RECIPE else Strings.Recipe.UPDATE_RECIPE,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(if (recipe == null) Strings.Recipe.SAVE_RECIPE else Strings.Recipe.UPDATE_RECIPE) },
            text = {
                Column {
                    OutlinedTextField(
                        value = recipeName,
                        onValueChange = { recipeName = it },
                        label = { Text(Strings.Recipe.NAME) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = recipeDescription,
                        onValueChange = { recipeDescription = it },
                        label = { Text(Strings.Recipe.DESCRIPTION) },
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
                    Text(if (recipe == null) Strings.Recipe.SAVE else Strings.Recipe.UPDATE)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text(Strings.Buttons.CANCEL)
                }
            }
        )
    }
} 