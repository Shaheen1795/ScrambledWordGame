package com.example.scrambledwordgame.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun InfoDialog( title: String,
                message: String,
                onDismissRequest: () -> Unit,
                onConfirmation: () -> Unit,
){
    AlertDialog(
    onDismissRequest = { onDismissRequest() },
    title = { Text(title) },
    text = { Text(message) },
    confirmButton = {
        Button(onClick = { onConfirmation() }) {
            Text("OK")
        }
    }
)
}