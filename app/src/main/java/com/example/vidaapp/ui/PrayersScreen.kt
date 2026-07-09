package com.example.vidaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vidaapp.model.PrayerEntity // Import corrigido
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PrayersScreen(
    isAdmin: Boolean,
    prayers: List<PrayerEntity>,
    onSendPrayer: (String) -> Unit,
    onDeletePrayer: (PrayerEntity) -> Unit
) {
    var prayerRequest by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Pedidos de Oração", style = MaterialTheme.typography.headlineSmall)
        
        Spacer(modifier = Modifier.height(16.dp))

        // Campo para novo pedido (apenas usuários logados podem ver/enviar)
        OutlinedTextField(
            value = prayerRequest,
            onValueChange = { prayerRequest = it },
            label = { Text("No que podemos orar por você?") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { 
                    if (prayerRequest.isNotBlank()) {
                        onSendPrayer(prayerRequest)
                        prayerRequest = ""
                    }
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar")
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Pedidos Recentes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(8.dp))

        if (prayers.isEmpty()) {
            Box(modifier = Modifier.fillWeight(1f), contentAlignment = Alignment.Center) {
                Text("Nenhum pedido de oração no momento.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(prayers) { prayer ->
                    PrayerItem(prayer, isAdmin, onDeletePrayer)
                }
            }
        }
    }
}

@Composable
fun PrayerItem(prayer: PrayerEntity, isAdmin: Boolean, onDelete: (PrayerEntity) -> Unit) {
    val date = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(prayer.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = prayer.memberName, fontWeight = FontWeight.Bold)
                Text(text = prayer.request, style = MaterialTheme.typography.bodyMedium)
                Text(text = date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            if (isAdmin) {
                IconButton(onClick = { onDelete(prayer) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

private fun Modifier.fillWeight(f: Float): Modifier = this.fillMaxWidth().fillMaxHeight(f)
