
package practica1.moviles.pruebaparapc02

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import practica1.moviles.pruebaparapc02.ui.liga1.ListadoScreen
import practica1.moviles.pruebaparapc02.ui.liga1.RegistroScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val firestore = FirebaseFirestore.getInstance()

            NavHost(navController = navController, startDestination = "registro") {
                composable("registro") {
                    RegistroScreen(navController = navController, db = firestore)
                }
                composable("listado") {
                    ListadoScreen(navController = navController, db = firestore)
                }
            }
        }
    }
}
