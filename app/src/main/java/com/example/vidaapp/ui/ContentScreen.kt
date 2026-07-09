package com.example.vidaapp.ui

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vidaapp.MainViewModel
import com.example.vidaapp.model.CommentEntity
import com.example.vidaapp.model.ContentEntity
import com.example.vidaapp.R
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

    Box(modifier = Modifier.fillMaxSize()) {
        // Logo de fundo com transparência e desfoque (Igual à Home)
        Image(
            painter = painterResource(id = R.drawable.logo_igreja),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(8.dp)
                .alpha(0.05f),
            contentScale = ContentScale.Fit
        )

        if (selectedContent != null) {
            val comments by mainViewModel.getComments(selectedContent!!.idString).collectAsState(initial = emptyList())
            var newCommentText by remember { mutableStateOf("") }

            Column(modifier = Modifier.fillMaxSize()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    IconButton(onClick = { selectedContent = null }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                    Text(text = selectedContent!!.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                }
                
                VideoPlayer(url = selectedContent!!.url)
                
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(text = "Descrição", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(text = selectedContent!!.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Text(text = "Comentários", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
                    }

                    items(comments) { comment ->
                        CommentItem(comment, isAdmin, onDelete = { mainViewModel.deleteComment(comment) })
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(8.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCommentText,
                        onValueChange = { newCommentText = it },
                        placeholder = { Text("Escreva um comentário...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    IconButton(onClick = {
                        if (newCommentText.isNotBlank()) {
                            mainViewModel.addComment(selectedContent!!.idString, newCommentText)
                            newCommentText = ""
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Logo visível no topo da lista
                Image(
                    painter = painterResource(id = R.drawable.logo_igreja),
                    contentDescription = "Logo Igreja",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Fit
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Vídeos e Áudios", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    if (isAdmin) {
                        Button(onClick = { showAddDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Novo")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (contents.isEmpty()) Text(text = "Nenhum conteúdo publicado.", color = Color.Gray)
                LazyColumn {
                    items(contents) { item ->
                        ContentItem(item = item, isAdmin = isAdmin, onDelete = { onDeleteContent(item) }, onClick = { selectedContent = item })
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
    
    if (showAddDialog) {
        AddContentDialog(onDismiss = { showAddDialog = false }, onConfirm = onSaveContent)
    }
}

@Composable
fun CommentItem(comment: CommentEntity, isAdmin: Boolean, onDelete: () -> Unit) {
    val date = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(comment.timestamp))
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))) {
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
    val finalUrl = remember(url) {
        val videoId = when {
            url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
            url.contains("youtu.be/") -> url.substringAfterLast("/")
            url.contains("embed/") -> url.substringAfter("embed/").substringBefore("?")
            url.contains("/live/") -> url.substringAfter("/live/").substringBefore("?")
            else -> if (url.length == 11) url else null
        }
        if (videoId != null && videoId.length >= 11) {
            "https://www.youtube.com/embed/$videoId?autoplay=1&modestbranding=1"
        } else {
            url
        }
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(Color.Black),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mediaPlaybackRequiresUserGesture = false
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    userAgentString = "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36"
                }
                webViewClient = WebViewClient()
                webChromeClient = WebChromeClient()
                loadUrl(finalUrl)
            }
        },
        update = { webView ->
            if (webView.url != finalUrl) {
                webView.loadUrl(finalUrl)
            }
        }
    )
}

@Composable
fun ContentItem(item: ContentEntity, isAdmin: Boolean, onDelete: () -> Unit, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, fontWeight = FontWeight.Bold)
                Text(text = item.type, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
            }
            if (isAdmin) IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
        }
    }
}

@Composable
fun AddContentDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Vídeo") }
    var url by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Novo Conteúdo") }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Link do YouTube") }, modifier = Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = type == "Vídeo", onClick = { type = "Vídeo" })
                Text("Vídeo")
                Spacer(modifier = Modifier.width(8.dp))
                RadioButton(selected = type == "Áudio", onClick = { type = "Áudio" })
                Text("Áudio")
            }
        }
    }, confirmButton = { Button(onClick = { onConfirm(title, desc, type, url) }) { Text("Salvar") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } })
}
