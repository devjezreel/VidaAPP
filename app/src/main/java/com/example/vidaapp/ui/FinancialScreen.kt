package com.example.vidaapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.vidaapp.model.FinancialEntryEntity // Import corrigido
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreen(
    entries: List<FinancialEntryEntity>,
    onAddEntry: (String, Double, String, Boolean, String) -> Unit, // Adicionado parâmetro location
    onDeleteEntry: (FinancialEntryEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0)
    val startOfDay = calendar.timeInMillis
    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    val startOfWeek = calendar.timeInMillis
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startOfMonth = calendar.timeInMillis

    // Funções de soma corrigidas para evitar ambiguidade
    fun sumEntries(list: List<FinancialEntryEntity>) = list.filter { !it.isExpense }.sumOf { it.amount }
    fun sumExpenses(list: List<FinancialEntryEntity>) = list.filter { it.isExpense }.sumOf { it.amount }

    val todayEntries = entries.filter { it.timestamp >= startOfDay }
    val weekEntries = entries.filter { it.timestamp >= startOfWeek }
    val monthEntries = entries.filter { it.timestamp >= startOfMonth }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Fluxo de Caixa (ADM)", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryCard("Hoje", sumEntries(todayEntries) - sumExpenses(todayEntries), Modifier.weight(1f))
            SummaryCard("Semana", sumEntries(weekEntries) - sumExpenses(weekEntries), Modifier.weight(1f))
            SummaryCard("Mês", sumEntries(monthEntries) - sumExpenses(monthEntries), Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Saldo Geral", style = MaterialTheme.typography.labelMedium)
                    Text(text = "R$ ${String.format("%.2f", sumEntries(entries) - sumExpenses(entries))}", 
                        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                Button(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("Lançar")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Histórico de Lançamentos", style = MaterialTheme.typography.titleMedium)
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(entries) { entry ->
                FinancialEntryItem(entry, onDelete = { onDeleteEntry(entry) })
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }

    if (showAddDialog) {
        AddFinancialDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { desc, value, cat, isExp ->
                onAddEntry(desc, value, cat, isExp, "Brasil - Sede")
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SummaryCard(label: String, value: Double, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Text(text = "R$ ${String.format("%.0f", value)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, 
                color = if (value >= 0) Color(0xFF4CAF50) else Color.Red)
        }
    }
}

@Composable
fun FinancialEntryItem(entry: FinancialEntryEntity, onDelete: () -> Unit) {
    val date = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (entry.isExpense) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
            contentDescription = null,
            tint = if (entry.isExpense) Color.Red else Color(0xFF4CAF50),
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = entry.description, fontWeight = FontWeight.Bold)
            Text(text = "${entry.category} • $date", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        Text(
            text = "${if (entry.isExpense) "-" else "+"} R$ ${String.format("%.2f", entry.amount)}",
            fontWeight = FontWeight.Bold,
            color = if (entry.isExpense) Color.Red else Color(0xFF4CAF50)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFinancialDialog(onDismiss: () -> Unit, onConfirm: (String, Double, String, Boolean) -> Unit) {
    var desc by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var cat by remember { mutableStateOf("Dízimo") }
    var isExpense by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Lançamento") },
        text = {
            Column {
                Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))) {
                    TabButton("ENTRADA", !isExpense, Color(0xFF4CAF50), Modifier.weight(1f)) { isExpense = false; cat = "Dízimo" }
                    TabButton("SAÍDA", isExpense, Color.Red, Modifier.weight(1f)) { isExpense = true; cat = "Aluguel" }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text(if (isExpense) "Motivo da Saída" else "Nome do Doador") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = value, onValueChange = { value = it }, label = { Text("Valor R$") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Categoria:", style = MaterialTheme.typography.labelMedium)
                val cats = if (isExpense) listOf("Aluguel", "Luz/Água", "Missões", "Manutenção", "Outros") else listOf("Dízimo", "Oferta", "Doação", "Cantina")
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    cats.take(3).forEach { c ->
                        FilterChip(selected = cat == c, onClick = { cat = c }, label = { Text(c, fontSize = 10.sp) })
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(desc, value.replace(",",".").toDoubleOrNull() ?: 0.0, cat, isExpense) }, enabled = desc.isNotBlank() && value.isNotBlank()) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun TabButton(label: String, selected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(4.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (selected) color else Color.Transparent, contentColor = if (selected) Color.White else Color.Gray),
        elevation = null,
        shape = RoundedCornerShape(4.dp)
    ) { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
}
