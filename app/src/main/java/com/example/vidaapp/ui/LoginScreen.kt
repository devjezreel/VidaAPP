package com.example.vidaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.vidaapp.R

@Composable
fun LoginScreen(
    isAdminLogin: Boolean = false,
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    errorMessage: String? = null
) {
    var cpf by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        // Logo de fundo desfocado (Padrão Vida APP)
        Image(
            painter = painterResource(id = R.drawable.logo_igreja),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(10.dp)
                .alpha(0.1f),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo colorido no topo do formulário
            Image(
                painter = painterResource(id = R.drawable.logo_igreja),
                contentDescription = "Logo Igreja",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 24.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = if (isAdminLogin) "Acesso Administrativo" else "Área do Membro", 
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = cpf,
                onValueChange = { if (it.length <= 11) cpf = it },
                label = { Text("CPF (apenas números)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onLoginClick(cpf, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("ENTRAR", fontWeight = FontWeight.Bold)
            }

            if (!isAdminLogin) {
                TextButton(onClick = onRegisterClick, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Ainda não tem conta? Cadastre-se")
                }
            }
        }
    }
}
