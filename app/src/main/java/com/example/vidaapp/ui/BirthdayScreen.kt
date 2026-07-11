package com.example.vidaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vidaapp.model.MemberEntity
import com.example.vidaapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun BirthdayScreen(
    members: List<MemberEntity>
) {
    val currentMonthName = SimpleDateFormat("MMMM", Locale("pt", "BR"))
        .format(Calendar.getInstance().time)
        .replaceFirstChar { it.uppercase() }

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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Cake, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aniversariantes de $currentMonthName", 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (members.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Nenhum aniversariante este mês.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(members) { member ->
                        BirthdayItem(member)
                    }
                }
            }
        }
    }
}

@Composable
fun BirthdayItem(member: MemberEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = member.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Data: ${member.birthDate}", style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "🎈", fontSize = 24.sp)
        }
    }
}
