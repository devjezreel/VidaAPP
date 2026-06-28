package com.example.vidaapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vidaapp.PrayerEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PrayersScreen(
    isAdmin: Boolean,
    prayers: List<PrayerEntity>,
    onSendPrayer: (String) -> Unit,
    onDeletePrayer: (PrayerEntity) -> Unit
) {
    var prayerText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = if (isAdmin) "Pedidos de Oração (ADM)" else "Pedidos de Oração",
            style = MaterialTheme.typography.headlineSmall
        )
        
        if (!isAdmin) {
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = prayerText,
                onValueChange = { prayerText = it },
                label = { Text("Escreva seu pedido aqui...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    onSendPrayer(prayerText)
                    prayerText = ""
                },
                modifier = Modifier.align(Alignment.End),
                enabled = prayerText.isNotBlank()
            ) {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Enviar Pedido")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        if (prayers.isEmpty()) {
            Text(
                text = "Nenhum pedido registrado.",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        LazyColumn {
            items(prayers) { prayer ->
                PrayerItem(prayer, isAdmin, onDelete = { onDeletePrayer(prayer) })
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun PrayerItem(prayer: PrayerEntity, isAdmin: Boolean, onDelete: () -> Unit) {
    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(prayer.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = prayer.memberName, fontWeight = FontWeight.Bold)
                    Text(text = date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                if (isAdmin) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = prayer.request, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
