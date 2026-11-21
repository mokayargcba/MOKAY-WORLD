package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.AuthScreen
import com.example.auth.authStateFlow
import com.example.data.firebase.SaveRepository
import com.example.game.engine.DialogueManager
import com.example.game.ui.MokayGameScreen
import com.example.game.ui.WebGameScreen
import com.example.game.viewmodel.GameViewModel
import com.example.ui.theme.MokayWorldTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MokayWorldTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val currentUser by Firebase.auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = Firebase.auth.currentUser)
    var showWebVersion by rememberSaveable { mutableStateOf(false) }

    if (showWebVersion) {
        WebGameScreen(onExitWebMode = { showWebVersion = false })
        return
    }

    val user = currentUser
    if (user == null) {
        AuthScreen(
            onAuthSuccess = {},
            onOpenWebVersion = { showWebVersion = true }
        )
    } else {
        key(user.uid) {
            val gameViewModel: GameViewModel = viewModel(
                key = user.uid,
                factory = viewModelFactory {
                    initializer {
                        val app = checkNotNull(this[APPLICATION_KEY])
                        val dbId = app.getString(R.string.firestore_database_id)
                        GameViewModel(
                            saveRepository = SaveRepository(FirebaseFirestore.getInstance(dbId)),
                            currentUserId = user.uid,
                            dialogueManager = DialogueManager.fromAssets(app.assets)
                        )
                    }
                }
            )
            MokayGameScreen(
                viewModel = gameViewModel,
                onOpenWebVersion = { showWebVersion = true }
            )
        }
    }
}
