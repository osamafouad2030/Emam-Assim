package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.MishkahDatabase
import com.example.data.MishkahRepository
import com.example.ui.MishkahApp
import com.example.ui.MishkahViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = MishkahDatabase.getDatabase(applicationContext)
        val repository = MishkahRepository(database.mishkahDao())

        setContent {
            val mishkahViewModel: MishkahViewModel = viewModel(
                factory = MishkahViewModel.provideFactory(repository, applicationContext)
            )
            MishkahApp(viewModel = mishkahViewModel)
        }
    }
}
