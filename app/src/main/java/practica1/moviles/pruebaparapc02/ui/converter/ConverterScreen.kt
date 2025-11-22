package practica1.moviles.pruebaparapc02.ui.converter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var result by remember { mutableStateOf("") }
    val currencies = listOf("USD", "EUR", "PEN", "GBP", "JPY")
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    // Hardcoded rates
    val rates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.925,
        "PEN" to 3.75,
        "GBP" to 0.78,
        "JPY" to 157.5
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
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
            val convertedAmount = amountValue * (toRate / fromRate)
            result = "$amountValue $fromCurrency equivalen a ${String.format("%.2f", convertedAmount)} $toCurrency"

            // Save to Firestore
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
            modifier = Modifier.menuAnchor(),
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