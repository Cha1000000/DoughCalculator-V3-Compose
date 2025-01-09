package com.easycook.doughcalculator.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.easycook.doughcalculator.R
import com.easycook.doughcalculator.ui.theme.DoughCalculatorTheme

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

@Composable
fun ShowConfirmDialog(
    title: String,
    message: String,
    dialogState: MutableState<Boolean>,
    onConfirm: () -> Unit
) {
    DoughCalculatorTheme {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colorScheme.background
        ) {
            AlertDialog(
                onDismissRequest = { dialogState.value = false },
                title = {
                    Text(
                        color = colorScheme.tertiary,
                        text = title,
                        style = typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = message,
                        style = typography.labelLarge,
                        fontSize = 16.sp,
                    )
                },
                confirmButton = {
                    TextButton(onClick = onConfirm) {
                        Text(text = stringResource(R.string.alert_button_yes), fontSize = 20.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { dialogState.value = false }) {
                        Text(text = stringResource(R.string.alert_button_cancel), fontSize = 20.sp)
                    }
                },
            )
        }
    }
}