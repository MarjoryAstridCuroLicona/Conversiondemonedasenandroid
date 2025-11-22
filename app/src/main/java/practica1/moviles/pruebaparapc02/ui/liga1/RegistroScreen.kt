
package practica1.moviles.pruebaparapc02.ui.liga1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegistroScreen(navController: NavController, db: FirebaseFirestore) {
    var nombreEquipo by remember { mutableStateOf("") }
    var anioFundacion by remember { mutableStateOf("") }
    var titulosGanados by remember { mutableStateOf("") }
    var urlImagen by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro Liga 1")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nombreEquipo,
            onValueChange = { nombreEquipo = it },
            label = { Text("Nombre del equipo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = anioFundacion,
            onValueChange = { anioFundacion = it },
            label = { Text("Año de fundación") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = titulosGanados,
            onValueChange = { titulosGanados = it },
            label = { Text("Número de títulos ganados") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = urlImagen,
            onValueChange = { urlImagen = it },
            label = { Text("URL de la imagen del equipo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val equipo = hashMapOf(
                "nombre" to nombreEquipo,
                "fundacion" to anioFundacion,
                "titulos" to titulosGanados,
                "url" to urlImagen
            )

            db.collection("equipos")
                .add(equipo)
                .addOnSuccessListener {
                    navController.navigate("listado")
                }
                .addOnFailureListener { e ->
                    // Handle error
                }
        }) {
            Text("Guardar")
        }
    }
}
