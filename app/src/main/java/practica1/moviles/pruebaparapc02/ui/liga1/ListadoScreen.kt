
package practica1.moviles.pruebaparapc02.ui.liga1

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ListadoScreen(navController: NavController, db: FirebaseFirestore) {
    var equipos by remember { mutableStateOf<List<Equipo>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("equipos").get()
            .addOnSuccessListener { result ->
                equipos = result.map { document ->
                    Equipo(
                        nombre = document.getString("nombre") ?: "",
                        fundacion = document.getString("fundacion") ?: "",
                        titulos = document.getString("titulos") ?: "",
                        url = document.getString("url") ?: ""
                    )
                }
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Equipos", style = MaterialTheme.typography.headlineMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(equipos) { equipo ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = equipo.url,
                            contentDescription = "Escudo de ${equipo.nombre}",
                            modifier = Modifier.size(64.dp)
                        )
                        Text(text = equipo.fundacion)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = equipo.nombre,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = equipo.titulos,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        Button(
            onClick = { navController.navigate("registro") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Nuevo Registro")
        }
    }
}
