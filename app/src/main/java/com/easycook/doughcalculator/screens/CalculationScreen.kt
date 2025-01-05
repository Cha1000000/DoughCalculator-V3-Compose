package com.easycook.doughcalculator.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.R.color.dark_gray
import com.easycook.doughcalculator.R.color.light_gray
import com.easycook.doughcalculator.R.color.semi_white
import com.easycook.doughcalculator.R.color.orange_700
import com.easycook.doughcalculator.R.color.orange_900
import com.easycook.doughcalculator.R.color.text_orange
import com.easycook.doughcalculator.R.color.text_red
import com.easycook.doughcalculator.R.color.validation_text_color
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.common.SAVE_RECIPE_SCREEN
import com.easycook.doughcalculator.database.DoughRecipeEntity
import com.easycook.doughcalculator.models.IngredientType
import com.easycook.doughcalculator.models.IngredientUiItemModel
import com.easycook.doughcalculator.ui.theme.Background
import com.easycook.doughcalculator.ui.theme.FontFamilyDefault

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculationScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        focusManager.clearFocus()
                    }
                )
            },
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(50.dp),
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        text = stringResource(id = R.string.screen_title_calculation),
                        textAlign = TextAlign.Center,
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
                                        modifier = Modifier.padding(bottom = 6.dp),
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
                                        painter = painterResource(id = R.drawable.ic_my_recipes),
                                        modifier = Modifier.padding(bottom = 4.dp),
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
                                        modifier = Modifier.padding(bottom = 6.dp),
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.primary,
                )
            )
        },
    ) { paddingValues ->
        IngredientsTable(
            viewModel = viewModel,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Background)
        )
    }
}

@SuppressLint("DefaultLocale", "UnrememberedMutableState")
@Composable
fun IngredientsTable(viewModel: RecipeViewModel, modifier: Modifier) {
    val tableRows = viewModel.tableIngredientRows
    val recipe = viewModel.recipeEntity
    val isNewRecipe = recipe.recipeId == null
    val isCalculateByWeight = viewModel.isCalculateByWeight
    val showEmptyFlourError = viewModel.isFlourEmpty
    val showEmptySaltError = viewModel.isSaltEmpty
    val showEmptyWaterError = viewModel.isWaterEmpty
    val showWaterValidationWarn = viewModel.isWaterValidationWarn
    val showSaltValidationError = viewModel.isSaltValidationError

    Column(modifier = modifier) {
        if (!isNewRecipe) {
            Text(
                text = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = colorResource(orange_900)
            )
        }
        CalculateByWeightOrPercentTableRow(isCalculateByWeight)
        TableTitle()
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .offset(y = (-12).dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(tableRows, key = { it.ingredient }) { row ->
                Column {
                    IngredientRow(
                        ingredientItem = row,
                        isCalculateByWeight = isCalculateByWeight,
                        recipe = recipe,
                    ) { viewModel.onCalculationClick() }
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
                onClick = { viewModel.onCalculationClick() },
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
fun CalculateByWeightOrPercentTableRow(isCalculateByWeight: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.title_calculate_by),
            modifier = Modifier
                .weight(1.25f)
                .align(Alignment.CenterVertically),
            style = typography.titleMedium,
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
fun TableTitle() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 8.dp)
            .offset(y = (-8).dp),
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
            modifier = Modifier
                .weight(1f)
                .offset(y = (-10).dp),
            textAlign = TextAlign.Start,
            style = typography.titleMedium
        )
    }
}

@Composable
fun IngredientRow(
    ingredientItem: IngredientUiItemModel,
    isCalculateByWeight: MutableState<Boolean>,
    recipe: DoughRecipeEntity,
    onLastRowDone: () -> Unit,
) {
    val gram = ingredientItem.quantity.value.text.toIntOrNull() ?: 0
    val prc = ingredientItem.percent.value.text.toDoubleOrNull() ?: 0.0
    val cor = ingredientItem.correction.value.text.toIntOrNull() ?: 0
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
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(ingredientItem.ingredient.title),
            modifier = Modifier
                .weight(1.15f)
                .align(Alignment.CenterVertically),
            fontSize = 24.sp
        )
        ValueInput(
            modifier = Modifier.weight(1f),
            inputValue = ingredientItem.quantity,
            isEnabled = if (ingredientItem.ingredient == IngredientType.Flour) true else isCalculateByWeight.value,
            isWeight = true,
            isLastRow = ingredientItem.ingredient == IngredientType.Egg,
            onDoneClick = onLastRowDone,
        )
        ValueInput(
            modifier = Modifier.weight(0.8f),
            inputValue = ingredientItem.percent,
            isEnabled = if (ingredientItem.ingredient == IngredientType.Flour) false else !isCalculateByWeight.value,
            isWeight = false,
            isLastRow = ingredientItem.ingredient == IngredientType.Egg,
            onDoneClick = onLastRowDone,
        )
        ValueInput(
            modifier = Modifier.weight(1f),
            inputValue = ingredientItem.correction,
            isEnabled = ingredientItem.ingredient == IngredientType.Flour/* && isCalculateByWeight.value*/,
            isWeight = true,
            isLastRow = recipe.waterGram != 0 && recipe.saltGram != 0,
            onDoneClick = onLastRowDone,
        )
    }
}

@Composable
fun ValueInput(
    modifier: Modifier = Modifier,
    inputValue: MutableState<TextFieldValue>,
    isEnabled: Boolean,
    isWeight: Boolean,
    isLastRow: Boolean,
    onDoneClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val maxLength = if (isWeight) 4 else 2
    OutlinedTextField(
        value = inputValue.value,
        onValueChange = { newValue ->
            val filteredText = newValue.text.filter { it.isDigit() }.take(maxLength)
            inputValue.value = newValue.copy(
                text = filteredText,
                selection = TextRange(filteredText.length)
            )
        },
        modifier = modifier.height(48.dp),
        enabled = isEnabled,
        textStyle = TextStyle(
            fontFamily = FontFamilyDefault,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = if (isLastRow) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = {
                focusManager.clearFocus()
                onDoneClick.invoke()
            },
            onGo = {
                focusManager.clearFocus()
                onDoneClick.invoke()
            },
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorResource(orange_700),
            disabledTextColor = colorResource(dark_gray),
            focusedContainerColor = colorResource(light_gray),
            unfocusedContainerColor = colorResource(light_gray),
            disabledContainerColor = if (isEnabled) colorResource(light_gray) else Transparent,
            cursorColor = colorResource(orange_700),
            selectionColors = LocalTextSelectionColors.current,
            focusedBorderColor = colorResource(orange_700),
            unfocusedBorderColor = colorResource(semi_white),
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
                style = typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = if (messageId != null) stringResource(messageId) else message
                    ?: stringResource(R.string.alert_description_try_again),
                style = typography.labelLarge,
                fontSize = 20.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.alert_button_ok), fontSize = 20.sp)
            }
        },
    )
}