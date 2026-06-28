package com.example.vidaapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.vidaapp.R

@Composable
fun WelcomeScreen(
    onEnterClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo com botão invisível em cima
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(280.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_igreja),
                    contentDescription = "Logo Igreja",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                
                // Botão invisível para ADM
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            onAdminClick()
                        }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onEnterClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            ) {
                Text(
                    text = "ENTRAR",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
