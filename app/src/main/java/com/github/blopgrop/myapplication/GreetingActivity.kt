package com.github.blopgrop.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.ui.platform.LocalContext

class GreetingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val name = (LocalContext.current as GreetingActivity).intent.getStringExtra("name")
            Text("Welcome$name")
        }
    }
}