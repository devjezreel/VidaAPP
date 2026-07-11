package com.example.vidaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.vidaapp.model.MemberEntity
import com.example.vidaapp.R

@Composable
fun MembersListScreen(
    members: List<MemberEntity>,
    onEditMember: (MemberEntity) -> Unit,
    onDeleteMember: (MemberEntity) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Logo de fundo (Padrão Vida APP)
        Image(
            painter = painterResource(id = R.drawable.logo_igreja),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp)
                .alpha(0.05f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                text = "Lista de Membros (ADM)", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            if (members.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Nenhum membro cadastrado ainda.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(members) { member ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            MemberItem(
                                member = member,
                                onEditClick = { onEditMember(member) },
                                onDeleteClick = { onDeleteMember(member) }
                            )
                        }
                    }
                }
            }
        }
    }
}
