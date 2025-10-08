package com.example.finai.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun COutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier,
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFFFFC107),
            unfocusedIndicatorColor = Color.Gray,
            focusedLabelColor = Color(0xFFFFC107),
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color(0xFFFFC107),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        singleLine = singleLine,
        keyboardOptions = keyboardOptions
    )
}
