package com.easycook.doughcalculator.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.R.color.light_gray
import com.easycook.doughcalculator.R.color.orange_900
import com.easycook.doughcalculator.RecipeViewModel
import com.easycook.doughcalculator.common.CALCULATION_SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveRecipeScreen(
    navController: NavHostController,
    viewModel: RecipeViewModel
) {
    val focusManager = LocalFocusManager.current
    var recipeName by remember { mutableStateOf(viewModel.recipeEntity.title) }
    var recipeDescription by remember { mutableStateOf(viewModel.recipeEntity.description) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.height(50.dp),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                        text = stringResource(R.string.screen_title_save_recipe),
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
                            contentDescription = "Back to recipe calculation",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorScheme.primary,
                )
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Image(
                painter = painterResource(id = R.drawable.background_save),
                contentDescription = "Background image",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp, start = 68.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    value = recipeName,
                    onValueChange = { recipeName = it },
                    label = {
                        Text(
                            stringResource(R.string.recipe_title),
                            style = typography.labelLarge,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    },
                    textStyle = typography.bodyLarge,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) },
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(light_gray),
                        unfocusedContainerColor = colorResource(light_gray),
                        focusedTextColor = colorResource(orange_900),
                        unfocusedTextColor = colorScheme.secondary,
                        cursorColor = colorResource(orange_900),
                        selectionColors = LocalTextSelectionColors.current,
                    ),
                )
                TextField(
                    value = recipeDescription,
                    onValueChange = { recipeDescription = it },
                    label = {
                        Text(
                            stringResource(R.string.text_field_description),
                            style = typography.labelLarge,
                            modifier = Modifier.padding(bottom = 6.dp),
                        )
                    },
                    textStyle = typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 15,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colorResource(light_gray),
                        unfocusedContainerColor = colorResource(light_gray),
                        focusedTextColor = colorResource(orange_900),
                        unfocusedTextColor = colorScheme.secondary,
                        cursorColor = colorResource(orange_900),
                        selectionColors = LocalTextSelectionColors.current,
                    ),
                )
                Button(
                    onClick = {
                        viewModel.onSaveClick(title = recipeName, description = recipeDescription)
                        navController.navigate(CALCULATION_SCREEN) {
                            launchSingleTop = true
                        }
                    },
                    enabled = recipeName.isNotEmpty(),
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(vertical = 6.dp),
                        text = stringResource(R.string.button_save_text),
                        style = typography.bodyLarge,
                        fontSize = 24.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}