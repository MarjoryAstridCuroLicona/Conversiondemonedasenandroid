package practica1.moviles.pruebaparapc02.ui.converter

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(navController: NavController) {
    var amount by remember { mutableStateOf("") }
    var fromCurrency by remember { mutableStateOf("") }
    var toCurrency by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    var rates by remember { mutableStateOf<Map<String, Double>>(emptyMap()) }
    var currencies by remember { mutableStateOf<List<String>>(emptyList()) }

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        firestore.collection("rates").document("latest")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val fetchedRates = document.data?.mapValues { ((it.value as? Number)?.toDouble() ?: 0.0) } ?: emptyMap()
                    rates = fetchedRates
                    currencies = fetchedRates.keys.sorted()
                    if (currencies.isNotEmpty()) {
                        fromCurrency = currencies.firstOrNull { it == "USD" } ?: currencies[0]
                        toCurrency = currencies.firstOrNull { it == "EUR" } ?: currencies.getOrNull(1) ?: currencies[0]
                    }
                } else {
                    Log.w("Firestore", "Documento 'rates/latest' no encontrado en Firestore.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener las tasas", exception)
            }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currencies.isEmpty()) {
            CircularProgressIndicator()
            Text("Cargando tasas de cambio...", modifier = Modifier.padding(16.dp))
        } else {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Monto") },
                modifier = Modifier.padding(8.dp)
            )

            Row(modifier = Modifier.padding(8.dp)) {
                CurrencyDropdown(currencies, fromCurrency) { fromCurrency = it }
                Spacer(modifier = Modifier.width(8.dp))
                CurrencyDropdown(currencies, toCurrency) { toCurrency = it }
            }

            Button(onClick = {
                val amountValue = amount.toDoubleOrNull() ?: 0.0
                val fromRate = rates[fromCurrency] ?: 1.0
                val toRate = rates[toCurrency] ?: 1.0
                
                if (fromRate == 0.0) {
                    result = "La tasa de origen no puede ser cero."
                    return@Button
                }

                val convertedAmount = amountValue * (toRate / fromRate)
                result = "$amountValue $fromCurrency equivalen a ${String.format("%.2f", convertedAmount)} $toCurrency"

                val conversion = hashMapOf(
                    "userId" to auth.currentUser?.uid,
                    "from" to fromCurrency,
                    "to" to toCurrency,
                    "amount" to amountValue,
                    "result" to convertedAmount,
                    "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
                firestore.collection("conversions").add(conversion)

            }) {
                Text("Convertir")
            }

            Text(result, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            auth.signOut()
            navController.navigate("login") { popUpTo("converter") { inclusive = true } }
        }) {
            Text("Cerrar sesión")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(currencies: List<String>, selectedCurrency: String, onCurrencySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        TextField(
            modifier = Modifier.menuAnchor().width(120.dp),
            readOnly = true,
            value = selectedCurrency,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text(currency) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}
