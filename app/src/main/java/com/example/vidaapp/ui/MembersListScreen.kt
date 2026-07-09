package com.example.vidaapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.vidaapp.model.MemberEntity

@Composable
fun MembersListScreen(
    members: List<MemberEntity>,
    onEditMember: (MemberEntity) -> Unit,
    onDeleteMember: (MemberEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Lista de Membros (ADM)", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        if (members.isEmpty()) {
            Text(text = "Nenhum membro cadastrado ainda.")
        } else {
            LazyColumn {
                items(members) { member ->
                    MemberItem(
                        member = member,
                        onEditClick = { onEditMember(member) },
                        onDeleteClick = { onDeleteMember(member) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}
