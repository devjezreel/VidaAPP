package com.example.vidaapp.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.vidaapp.model.MemberEntity
import com.example.vidaapp.util.isCpfValid
import com.example.vidaapp.R

@Composable
fun RegistrationScreen(
    memberToEdit: MemberEntity? = null,
    isAdminUser: Boolean = false,
    onSaveMember: (MemberEntity) -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isMemberAdmin by remember { mutableStateOf(false) }
    
    var cpfError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(memberToEdit) {
        memberToEdit?.let {
            name = it.name
            cpf = it.cpf
            birthDate = it.birthDate
            address = it.address
            phone = it.phone
            role = it.role
            password = it.password
            isMemberAdmin = it.isAdmin
        }
    }

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo no topo
            Image(
                painter = painterResource(id = R.drawable.logo_igreja),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = if (memberToEdit == null) "Cadastro de Membro" else "Editar Membro",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Formulário em um Card para destacar do fundo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = cpf,
                        onValueChange = { input ->
                            if (input.length <= 11) {
                                val numbers = input.filter { it.isDigit() }
                                cpf = numbers
                                // Uso de comparação explícita para evitar o erro do operador '!'
                                cpfError = numbers.length == 11 && isCpfValid(numbers) == false
                            }
                        },
                        label = { Text("CPF (apenas números)") },
                        isError = cpfError,
                        supportingText = { if (cpfError) Text("CPF inválido") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(value = birthDate, onValueChange = { birthDate = it }, label = { Text("Nascimento (DD/MM/AAAA)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Endereço") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Telefone") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    OutlinedTextField(value = role, onValueChange = { role = it }, label = { Text("Cargo na Igreja") }, modifier = Modifier.fillMaxWidth())
                    
                    if (isAdminUser) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .toggleable(value = isMemberAdmin, onValueChange = { isMemberAdmin = it }, role = Role.Checkbox)
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = isMemberAdmin, onCheckedChange = null)
                            Text(text = "Acesso de Administrador", modifier = Modifier.padding(start = 16.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val validCpf = isCpfValid(cpf)
                    if (name.isNotBlank() && cpf.length == 11 && validCpf && password.isNotBlank()) {
                        val member = MemberEntity(
                            idString = memberToEdit?.idString ?: "",
                            name = name,
                            cpf = cpf,
                            birthDate = birthDate,
                            address = address,
                            phone = phone,
                            role = role,
                            password = password,
                            isAdmin = isMemberAdmin
                        )
                        onSaveMember(member)
                        Toast.makeText(context, "Salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = name.isNotBlank() && cpf.length == 11 && cpfError == false && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(if (memberToEdit == null) Icons.Default.Add else Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (memberToEdit == null) "CONFIRMAR CADASTRO" else "SALVAR ALTERAÇÕES", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
