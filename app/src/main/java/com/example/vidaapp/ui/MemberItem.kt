package com.example.vidaapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vidaapp.model.MemberEntity // Import corrigido

@Composable
fun MemberItem(
    member: MemberEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = member.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "CPF: ${member.cpf}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Nasc: ${member.birthDate}", style = MaterialTheme.typography.bodySmall)
            Text(text = "End: ${member.address}", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Cargo: ${member.role}", style = MaterialTheme.typography.bodyMedium)
                Text(text = member.phone, style = MaterialTheme.typography.bodySmall)
            }
        }
        
        Row {
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Color.Red)
            }
        }
    }
}
