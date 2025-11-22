package practica1.moviles.pruebaparapc02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import practica1.moviles.pruebaparapc02.ui.login.LoginScreen
import practica1.moviles.pruebaparapc02.ui.converter.ConverterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val firebaseAuth = FirebaseAuth.getInstance()
            val startDestination = if (firebaseAuth.currentUser != null) "converter" else "login"

            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") {
                    LoginScreen(navController = navController)
                }
                composable("converter") {
                    ConverterScreen(navController = navController)
                }
            }
        }
    }
}
