package com.easycook.doughcalculator.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.easycook.doughcalculator.R.color.orange_900
import com.easycook.doughcalculator.R.color.text_orange
import com.easycook.doughcalculator.R.color.text_red
import com.easycook.doughcalculator.R.color.validation_text_color
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.common.SAVE_RECIPE_SCREEN
import com.easycook.doughcalculator.database.DoughRecipeEntity
import com.easycook.doughcalculator.models.IngredientType
import com.easycook.doughcalculator.models.IngredientUiItemModel
import com.easycook.doughcalculator.ui.theme.FontFamilyDefault

@Composable
fun CalculationScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel
) {
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
                    var menuExpanded by rememberSaveable { mutableStateOf(false) }
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
                                    viewModel.resetRecipe()
                                    viewModel.resetIngredientTableRows()
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
                                    navController.navigate(SAVE_RECIPE_SCREEN)
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

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun IngredientsTable(viewModel: RecipeViewModel) {
    val tableRows = viewModel.tableIngredientRows
    val recipe = viewModel.recipeEntity
    val isNewRecipe = recipe.recipeId == null
    val isCalculateByWeight = viewModel.isCalculateByWeight
    val showEmptyFlourError = viewModel.isFlourEmpty
    val showEmptySaltError = viewModel.isSaltEmpty
    val showEmptyWaterError = viewModel.isWaterEmpty
    val showWaterValidationWarn = viewModel.isWaterValidationWarn
    val showSaltValidationError = viewModel.isSaltValidationError

    Column(modifier = Modifier.fillMaxSize()) {
        if (!isNewRecipe) {
            Text(
                text = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 6.dp),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = colorResource(orange_900)
            )
        }
        TableTitle()
        CalculateByWeightOrPercentTableRow(isCalculateByWeight)
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            //contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(tableRows, key = { it.ingredient }) { row ->
                Column {
                    IngredientRow(row, isCalculateByWeight, recipe)
                    if (row.ingredient == IngredientType.Water && showWaterValidationWarn.value) {
                        ValidationRow(R.string.validation_water_range_recommended, false)
                    }
                    if (row.ingredient == IngredientType.Salt && showSaltValidationError.value) {
                        ValidationRow(R.string.validation_salt_invalid_range, true)
                    }
                }
            }
        }
        if (!isNewRecipe) {
            Text(
                text = recipe.description,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                style = typography.bodyLarge,
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = {
                    viewModel.onCalculationClick()
                    if (showEmptyWaterError.value || showEmptyFlourError.value || showEmptySaltError.value) {
                        return@Button
                    }
                    viewModel.updateIngredientTableRows()
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
                    modifier = Modifier.align(Alignment.CenterVertically),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamilyDefault,
                    letterSpacing = 4.sp
                )
            }
        }

        if (showEmptyFlourError.value) {
            ShowAlertDialog(
                isOpen = showEmptyFlourError,
                titleId = R.string.alert_title_error,
                messageId = R.string.error_invalid_flour_gram_input
            )
        }
        if (showEmptyWaterError.value) {
            ShowAlertDialog(
                isOpen = showEmptyWaterError,
                titleId = R.string.alert_title_error,
                messageId = if (isCalculateByWeight.value) {
                    R.string.error_invalid_water_gram_input
                } else {
                    R.string.error_invalid_water_percent_input
                }
            )
        }
        if (showEmptySaltError.value) {
            ShowAlertDialog(
                isOpen = showEmptySaltError,
                titleId = R.string.alert_title_error,
                messageId = if (isCalculateByWeight.value) {
                    R.string.error_invalid_salt_gram_input
                } else {
                    R.string.error_invalid_salt_percent_input
                }
            )
        }
    }
}

@Composable
fun TableTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
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
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 8.dp, vertical = 0.dp)
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
    ingredientItem: IngredientUiItemModel,
    isCalculateByWeight: MutableState<Boolean>,
    recipe: DoughRecipeEntity
) {
    val gram = if (ingredientItem.quantity.value == "") 0 else ingredientItem.quantity.value.toInt()
    val prc = if (ingredientItem.percent.value == "") 0.0 else ingredientItem.percent.value.toDouble()
    val cor = if (ingredientItem.correction.value == "") 0 else ingredientItem.correction.value.toInt()
    when (ingredientItem.ingredient) {
        IngredientType.Flour -> {
            recipe.flourGram = gram
            recipe.flourGramCorrection = cor
        }
        IngredientType.Water -> {
            recipe.waterGram = gram
            recipe.waterPercent = prc
            recipe.waterGramCorrection = cor
        }
        IngredientType.Salt -> {
            recipe.saltGram = gram
            recipe.saltPercent = prc
            recipe.saltGramCorrection = cor
        }
        IngredientType.Sugar -> {
            recipe.sugarGram = gram
            recipe.sugarPercent = prc
            recipe.sugarGramCorrection = cor
        }
        IngredientType.Butter -> {
            recipe.butterGram = gram
            recipe.butterPercent = prc
            recipe.butterGramCorrection = cor
        }
        IngredientType.Yeast -> {
            recipe.yeastGram = gram
            recipe.yeastPercent = prc
            recipe.yeastGramCorrection = cor
        }
        IngredientType.Milk -> {
            recipe.milkGram = gram
            recipe.milkPercent = prc
            recipe.milkGramCorrection = cor
        }
        IngredientType.Egg -> {
            recipe.eggGram = gram
            recipe.eggPercent = prc
            recipe.eggGramCorrection = cor
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(ingredientItem.ingredient.title),
            modifier = Modifier
                .weight(1.25f)
                .align(Alignment.CenterVertically),
            fontSize = 28.sp
        )
        ValueInput(
            modifier = Modifier.weight(1f),
            inputValue = ingredientItem.quantity,
            isEnabled = if (ingredientItem.ingredient == IngredientType.Flour) true else isCalculateByWeight.value,
            isWeight = true
        )
        ValueInput(
            modifier = Modifier.weight(0.8f),
            inputValue = ingredientItem.percent,
            isEnabled = if (ingredientItem.ingredient == IngredientType.Flour) false else !isCalculateByWeight.value,
            isWeight = false
        )
        ValueInput(
            modifier = Modifier.weight(1f),
            inputValue = ingredientItem.correction,
            isEnabled = ingredientItem.ingredient == IngredientType.Flour && isCalculateByWeight.value,
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
            inputValue.value = newText.filter { it.isDigit() }.take(maxLength)
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
            focusedTextColor = colorResource(orange_900),
            disabledTextColor = colorResource(dark_gray),
            focusedContainerColor = colorResource(light_gray),
            unfocusedContainerColor = colorResource(light_gray),
            disabledContainerColor = if (isEnabled) colorResource(light_gray) else Transparent,
            cursorColor = colorResource(orange_900),
            selectionColors = LocalTextSelectionColors.current,
            focusedBorderColor = colorResource(orange_900),
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
                text = if (titleId != null) stringResource(titleId) else title
                    ?: stringResource(R.string.alert_title_info),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (messageId != null) stringResource(messageId) else message
                    ?: stringResource(R.string.alert_description_try_again),
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