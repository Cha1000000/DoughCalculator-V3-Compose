package com.easycook.doughcalculator.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.R.color.dark_gray
import com.easycook.doughcalculator.R.color.light_gray
import com.easycook.doughcalculator.R.color.orange_700
import com.easycook.doughcalculator.R.color.text_orange
import com.easycook.doughcalculator.R.color.text_red
import com.easycook.doughcalculator.R.color.validation_text_color
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.database.DoughRecipeEntity
import com.easycook.doughcalculator.models.IngredientUiItemModel

@Composable
fun CalculationScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel
) {
    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.screen_title_calculation),
                        style = typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to recipes list",
                            tint = Color.White,
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Show options",
                                tint = Color.White
                            )
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.menu_item_new)) },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_create),
                                        contentDescription = "New",
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    viewModel.recipeEntity = DoughRecipeEntity()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.menu_item_open)) },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_open),
                                        contentDescription = "Open",
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    navController.navigate(navController.graph.findStartDestination().route!!) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    }
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.menu_item_save)) },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_save),
                                        contentDescription = "Save",
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                },
                backgroundColor = colorScheme.primary,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_main),
                contentDescription = "Background image",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )
            IngredientsTable(viewModel)
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun IngredientsTable(viewModel: RecipeViewModel) {
    val recipe by remember { mutableStateOf(viewModel.recipeEntity) }
    val isNewRecipe = recipe.recipeId == null
    val isCalculateByWeight = rememberSaveable { mutableStateOf(true) }
    val showEmptyFlourError = rememberSaveable { mutableStateOf(false) }
    val showEmptySaltError = rememberSaveable { mutableStateOf(false) }
    val showEmptyWaterError = rememberSaveable { mutableStateOf(false) }
    val showWaterValidationWarn = rememberSaveable { mutableStateOf(false) }
    val showSaltValidationError = rememberSaveable { mutableStateOf(false) }
    val showErrorAlert = rememberSaveable { mutableStateOf(false) }
    val tableRows = listOf(
        // Мука
        IngredientUiItemModel(
            name = stringResource(id = R.string.flour),
            quantity = if (isNewRecipe) "" else recipe.flourGram.toString(),
            percent = "100",
            correction = if (isNewRecipe || recipe.flourGramCorrection == 0) "" else recipe.flourGramCorrection.toString(),
        ),
        // Вода
        IngredientUiItemModel(
            name = stringResource(id = R.string.water),
            quantity = if (isNewRecipe) "" else recipe.waterGram.toString(),
            percent = if (isNewRecipe) "" else String.format("%.0f", recipe.waterPercent),
            correction = if (isNewRecipe || recipe.waterGramCorrection == 0) "" else recipe.waterGramCorrection.toString(),
        ),
        // Соль
        IngredientUiItemModel(
            name = stringResource(id = R.string.salt),
            quantity = if (isNewRecipe) "" else recipe.saltGram.toString(),
            percent = if (isNewRecipe) "" else String.format("%.0f", recipe.saltPercent),
            correction = if (isNewRecipe || recipe.saltGramCorrection == 0) "" else recipe.saltGramCorrection.toString(),
        ),
        // Сахар
        IngredientUiItemModel(
            name = stringResource(id = R.string.sugar),
            quantity = if (isNewRecipe || recipe.sugarGram == 0) "" else recipe.sugarGram.toString(),
            percent = if (isNewRecipe || recipe.sugarPercent == 0.0) "" else String.format(
                "%.0f",
                recipe.sugarPercent
            ),
            correction = if (isNewRecipe || recipe.sugarGramCorrection == 0) "" else recipe.sugarGramCorrection.toString(),
        ),
        // Масло
        IngredientUiItemModel(
            name = stringResource(id = R.string.butter),
            quantity = if (isNewRecipe || recipe.butterGram == 0) "" else recipe.butterGram.toString(),
            percent = if (isNewRecipe || recipe.butterPercent == 0.0) "" else String.format(
                "%.0f",
                recipe.butterPercent
            ),
            correction = if (isNewRecipe || recipe.butterGramCorrection == 0) "" else recipe.butterGramCorrection.toString(),
        ),
        // Яйцо
        IngredientUiItemModel(
            name = stringResource(id = R.string.egg),
            quantity = if (isNewRecipe || recipe.eggGram == 0) "" else recipe.eggGram.toString(),
            percent = if (isNewRecipe || recipe.eggPercent == 0.0) "" else String.format(
                "%.0f",
                recipe.eggPercent
            ),
            correction = if (isNewRecipe || recipe.eggGramCorrection == 0) "" else recipe.eggGramCorrection.toString(),
        ),
        // Дрожжи
        IngredientUiItemModel(
            name = stringResource(id = R.string.yeast),
            quantity = if (isNewRecipe || recipe.yeastGram == 0) "" else recipe.yeastGram.toString(),
            percent = if (isNewRecipe || recipe.yeastPercent == 0.0) "" else String.format(
                "%.0f",
                recipe.yeastPercent
            ),
            correction = if (isNewRecipe || recipe.yeastGramCorrection == 0) "" else recipe.yeastGramCorrection.toString(),
        ),
        // Молоко
        IngredientUiItemModel(
            name = stringResource(id = R.string.milk),
            quantity = if (isNewRecipe || recipe.milkGram == 0) "" else recipe.milkGram.toString(),
            percent = if (isNewRecipe || recipe.milkPercent == 0.0) "" else String.format(
                "%.0f",
                recipe.milkPercent
            ),
            correction = if (isNewRecipe || recipe.milkGramCorrection == 0) "" else recipe.milkGramCorrection.toString(),
        ),
    )
    val tableRowsState = rememberSaveable { mutableStateOf(tableRows) }
    Column(modifier = Modifier.fillMaxSize()) {
        if (!isNewRecipe) {
            Text(
                text = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
        TableTitle()
        CalculateByWeightOrPercentTableRow(isCalculateByWeight)
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.fillMaxWidth(),
            //contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(tableRowsState.value) { row ->
                Column {
                    IngredientRow(row, isCalculateByWeight, recipe)
                    if (row.name == stringResource(R.string.water) && showWaterValidationWarn.value) {
                        ValidationRow(R.string.validation_water_range_recommended, false)
                    }
                    if (row.name == stringResource(R.string.salt) && showSaltValidationError.value) {
                        ValidationRow(R.string.validation_salt_invalid_range, true)
                    }
                }
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = {
                    viewModel.onCalculationClicked(
                        /*recipe,*/
                        isCalculateByWeight = isCalculateByWeight.value,
                        isFlourEmpty = showEmptyFlourError,
                        isWaterEmpty = showEmptyWaterError,
                        isSaltEmpty = showEmptySaltError,
                        isWaterValidationWarning = showWaterValidationWarn,
                        isSaltValidationError = showSaltValidationError
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.button_calculate_text),
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily(Font(R.font.halogen)),
                    letterSpacing = 4.sp
                )
            }
        }

        if (showEmptyFlourError.value) {
            ShowAlertDialog(
                isOpen = showEmptyFlourError,
                titleId = R.string.alert_title_error,
                messageId = R.string.error_invalid_flour_input
            )
        }
        if (showEmptyWaterError.value) {
            ShowAlertDialog(
                isOpen = showEmptyWaterError,
                titleId = R.string.alert_title_error,
                messageId = R.string.error_invalid_water_input
            )
        }
        if (showEmptySaltError.value) {
            ShowAlertDialog(
                isOpen = showEmptySaltError,
                titleId = R.string.alert_title_error,
                messageId = R.string.error_invalid_salt_input
            )
        }
    }
}

@Composable
fun TableTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.title_ingredient),
            modifier = Modifier.weight(1.2f),
            style = typography.titleMedium
        )
        Text(
            text = stringResource(R.string.title_grams),
            modifier = Modifier.weight(0.9f),
            style = typography.titleMedium
        )
        Text(
            text = stringResource(R.string.title_percent),
            modifier = Modifier.weight(0.7f),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.title_correction),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            fontSize = 20.sp,
            style = typography.titleMedium
        )
    }
}

@Composable
fun CalculateByWeightOrPercentTableRow(isCalculateByWeight: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth().wrapContentHeight()
            .padding(horizontal = 6.dp, vertical = 0.dp)
            .offset(y = (-8).dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.title_calculate_by),
            modifier = Modifier
                .weight(1.25f)
                .align(Alignment.CenterVertically),
            style = typography.titleMedium,
            fontSize = 20.sp,
            color = colorResource(text_orange)
        )
        RadioButton(
            selected = isCalculateByWeight.value,
            onClick = { isCalculateByWeight.value = true },
            modifier = Modifier.weight(1f),
            colors = RadioButtonColors(
                selectedColor = colorScheme.primary,
                unselectedColor = colorResource(text_orange),
                disabledSelectedColor = colorResource(light_gray),
                disabledUnselectedColor = colorResource(light_gray)
            )
        )
        RadioButton(
            selected = !isCalculateByWeight.value,
            onClick = { isCalculateByWeight.value = false },
            modifier = Modifier.weight(1f),
            colors = RadioButtonColors(
                selectedColor = colorScheme.primary,
                unselectedColor = colorResource(text_orange),
                disabledSelectedColor = colorResource(light_gray),
                disabledUnselectedColor = colorResource(light_gray)
            )
        )
        Box(modifier = Modifier.weight(1f))
    }
}

@Composable
fun IngredientRow(
    ingredient: IngredientUiItemModel,
    isCalculateByWeight: MutableState<Boolean>,
    recipe: DoughRecipeEntity
) {
    val quantity = rememberSaveable { mutableStateOf(ingredient.quantity) }
    val percent = rememberSaveable { mutableStateOf(ingredient.percent) }
    val correction = rememberSaveable { mutableStateOf(ingredient.correction) }
    LaunchedEffect(quantity.value) {
        val gram = if (quantity.value == "") 0 else quantity.value.toInt()
        when (ingredient.name) {
            "Мука" -> recipe.flourGram = gram
            "Вода" -> recipe.waterGram = gram
            "Соль" -> recipe.saltGram = gram
            "Сахар" -> recipe.sugarGram = gram
            "Масло" -> recipe.butterGram = gram
            "Яйцо" -> recipe.eggGram = gram
            "Дрожжи" -> recipe.yeastGram = gram
            "Молоко" -> recipe.milkGram = gram
        }
    }
    LaunchedEffect(percent.value) {
        val prc = if (percent.value == "") 0.0 else percent.value.toDouble()
        when (ingredient.name) {
            "Вода" -> recipe.waterPercent = prc
            "Соль" -> recipe.saltPercent = prc
            "Сахар" -> recipe.sugarPercent = prc
            "Масло" -> recipe.butterPercent = prc
            "Яйцо" -> recipe.eggPercent = prc
            "Дрожжи" -> recipe.yeastPercent = prc
            "Молоко" -> recipe.milkPercent = prc
        }
    }
    LaunchedEffect(correction.value) {
        val cor = if (correction.value == "") 0 else correction.value.toInt()
        when (ingredient.name) {
            "Мука" -> recipe.flourGramCorrection = cor
            "Вода" -> recipe.waterGramCorrection = cor
            "Соль" -> recipe.saltGramCorrection = cor
            "Сахар" -> recipe.sugarGramCorrection = cor
            "Масло" -> recipe.butterGramCorrection = cor
            "Яйцо" -> recipe.eggGramCorrection = cor
            "Дрожжи" -> recipe.yeastGramCorrection = cor
            "Молоко" -> recipe.milkGramCorrection = cor
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = ingredient.name,
            modifier = Modifier
                .weight(1.25f)
                .align(Alignment.CenterVertically),
            fontSize = 28.sp
        )
        ValueInput(
            modifier = Modifier.weight(1f),
            inputValue = quantity,
            isEnabled = if (ingredient.name == stringResource(R.string.flour)) true else isCalculateByWeight.value,
            isWeight = true
        )
        ValueInput(
            modifier = Modifier.weight(0.8f),
            inputValue = percent,
            isEnabled = if (ingredient.name == stringResource(R.string.flour)) false else !isCalculateByWeight.value,
            isWeight = false
        )
        ValueInput(
            modifier = Modifier.weight(1f),
            inputValue = correction,
            isEnabled = ingredient.name == stringResource(R.string.flour) && isCalculateByWeight.value,
            isWeight = true
        )
    }
}

@Composable
fun ValueInput(
    modifier: Modifier = Modifier,
    inputValue: MutableState<String>,
    isEnabled: Boolean,
    isWeight: Boolean
) {
    val maxLength = if (isWeight) 4 else 2
    OutlinedTextField(
        value = inputValue.value,
        onValueChange = { newText ->
            inputValue.value = newText.take(maxLength)
        },
        modifier = modifier,
        enabled = isEnabled,
        textStyle = TextStyle(
            fontFamily = FontFamily(Font(R.font.halogen)),
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            .copy(imeAction = ImeAction.Next),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(orange_700),
            disabledTextColor = colorResource(dark_gray),
            focusedContainerColor = colorResource(light_gray),
            unfocusedContainerColor = colorResource(light_gray),
            disabledContainerColor = if (isEnabled) colorResource(light_gray) else Transparent,
            cursorColor = colorResource(text_orange),
            selectionColors = LocalTextSelectionColors.current,
            focusedBorderColor = colorResource(text_orange),
            unfocusedBorderColor = colorResource(light_gray),
            disabledBorderColor = if (isEnabled) colorResource(light_gray) else Transparent,
            errorTextColor = colorResource(text_red),
            errorContainerColor = Transparent,
            errorCursorColor = colorResource(text_red),
            errorBorderColor = colorResource(text_red),
        )
    )
}

@Composable
fun ValidationRow(validationTextResId: Int, isBlocking: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(validationTextResId),
            color = if (isBlocking) colorResource(text_red) else colorResource(validation_text_color),
            fontSize = 16.sp
        )
    }
}

@Composable
fun ShowAlertDialog(
    isOpen: MutableState<Boolean>,
    title: String? = null,
    message: String? = null,
    titleId: Int? = null,
    messageId: Int? = null,
    onConfirm: () -> Unit = { isOpen.value = false }
) {
    AlertDialog(
        onDismissRequest = { isOpen.value = false },
        title = {
            Text(
                color = colorScheme.tertiary,
                text = if (titleId != null) stringResource(titleId) else title ?: stringResource(R.string.alert_title_info),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (messageId != null) stringResource(messageId) else message ?: stringResource(R.string.alert_description_try_again),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.alert_button_ok), fontSize = 18.sp)
            }
        },
    )
}