package com.example.vidaapp.ui

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vidaapp.CommentEntity
import com.example.vidaapp.ContentEntity
import com.example.vidaapp.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ContentScreen(
    isAdmin: Boolean = false,
    contents: List<ContentEntity>,
    onSaveContent: (String, String, String, String) -> Unit,
    onDeleteContent: (ContentEntity) -> Unit,
    mainViewModel: MainViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedContent by remember { mutableStateOf<ContentEntity?>(null) }

    if (selectedContent != null) {
        val comments by mainViewModel.getComments(selectedContent!!.idString).collectAsState(initial = emptyList())
        var newCommentText by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { selectedContent = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text(text = selectedContent!!.title, style = MaterialTheme.typography.titleMedium)
            }
            
            VideoPlayer(url = selectedContent!!.url)
            
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(text = "Descrição do ADM", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(text = selectedContent!!.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Text(text = "Comentários", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
                }

                items(comments) { comment ->
                    CommentItem(comment, isAdmin, onDelete = { mainViewModel.deleteComment(comment) })
                }
            }

            // Barra de digitar comentário
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Escreva um comentário...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    shape = RoundedCornerShape(24.dp)
                )
                IconButton(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            mainViewModel.addComment(selectedContent!!.idString, newCommentText)
                            newCommentText = ""
                        }
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Vídeos e Áudios", style = MaterialTheme.typography.headlineSmall)
                if (isAdmin) {
                    Button(onClick = { showAddDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Novo")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (contents.isEmpty()) {
                Text(text = "Nenhum conteúdo publicado ainda.", color = Color.Gray)
            }

            LazyColumn {
                items(contents) { item ->
                    ContentItem(
                        item = item, 
                        isAdmin = isAdmin, 
                        onDelete = { onDeleteContent(item) },
                        onClick = { selectedContent = item }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddContentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc, type, url ->
                onSaveContent(title, desc, type, url)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CommentItem(comment: CommentEntity, isAdmin: Boolean, onDelete: () -> Unit) {
    val date = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(comment.timestamp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = comment.memberName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Text(text = date, fontSize = 10.sp, color = Color.Gray)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = comment.text, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                if (isAdmin) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VideoPlayer(url: String) {
    val finalUrl = if (url.contains("youtube.com/watch?v=")) {
        url.replace("watch?v=", "embed/")
    } else if (url.contains("youtu.be/")) {
        url.replace("youtu.be/", "youtube.com/embed/")
    } else {
        url
    }

    AndroidView(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                loadUrl(finalUrl)
            }
        }
    )
}

@Composable
fun ContentItem(item: ContentEntity, isAdmin: Boolean, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.PlayArrow, 
                contentDescription = null, 
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontWeight = FontWeight.Bold)
                Text(text = item.description, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                Text(text = item.type, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
            }
            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }
        }
    }
}

@Composable
fun AddContentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Vídeo") }
    var url by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Novo Conteúdo") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descrição") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("URL") })
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "Vídeo", onClick = { type = "Vídeo" })
                    Text("Vídeo")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = type == "Áudio", onClick = { type = "Áudio" })
                    Text("Áudio")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(title, description, type, url) }) { Text("Salvar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
