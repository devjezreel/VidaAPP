package com.example.vidaapp.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.vidaapp.model.PrayerEntity
import com.example.vidaapp.R
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Logo de fundo (Identidade Vida APP)
        Image(
            painter = painterResource(id = R.drawable.logo_igreja),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp)
                .alpha(0.05f),
            contentScale = ContentScale.Fit
        )

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Logo no topo
            Image(
                painter = painterResource(id = R.drawable.logo_igreja),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Pedidos de Oração", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            ) {
                OutlinedTextField(
                    value = prayerRequest,
                    onValueChange = { prayerRequest = it },
                    label = { Text("No que podemos orar por você?") },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    trailingIcon = {
                        IconButton(onClick = { 
                            if (prayerRequest.isNotBlank()) {
                                onSendPrayer(prayerRequest)
                                prayerRequest = ""
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Mural de Clamor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(8.dp))

            if (prayers.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("Nenhum pedido no momento.", color = Color.Gray)
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
}

@Composable
fun PrayerItem(prayer: PrayerEntity, isAdmin: Boolean, onDelete: (PrayerEntity) -> Unit) {
    val date = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(prayer.timestamp))
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
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
