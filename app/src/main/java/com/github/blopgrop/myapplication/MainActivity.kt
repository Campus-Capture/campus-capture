package com.github.blopgrop.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                var name = rememberSaveable  { mutableStateOf("Your name here") }

                WelcomeMsg()
                NameInput(name.value, onNameChange = { name.value = it })
                MainButton(name.value)
            }
        }
    }

    @Composable
    fun WelcomeMsg() {
        Text(text = "Hello ! Who are you ?")
    }

    @Composable
    fun NameInput(name: String, onNameChange: (String) -> Unit) {
        TextField(
            value = name,
            onValueChange = onNameChange
        )
    }

    @Composable
    fun MainButton(name: String) {
        val context = LocalContext.current
        val intent = Intent(context, GreetingActivity::class.java)

        intent.putExtra("name", name )
        Button(
            onClick = {
                context.startActivity(intent)
            }) {
            Text("button")
        }
    }
}


