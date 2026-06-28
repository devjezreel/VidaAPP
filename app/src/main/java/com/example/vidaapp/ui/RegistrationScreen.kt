package com.example.vidaapp.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.vidaapp.MemberEntity
import com.example.vidaapp.util.isValidCpf

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (memberToEdit == null) "Cadastro de Membro" else "Editar Membro",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome Completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = cpf,
            onValueChange = { 
                if (it.length <= 11) {
                    cpf = it.filter { char -> char.isDigit() }
                    cpfError = cpf.length == 11 && !isValidCpf(cpf)
                }
            },
            label = { Text("CPF (somente números)") },
            isError = cpfError,
            supportingText = { if (cpfError) Text("CPF inválido") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha de Acesso") },
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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Data de Nascimento (DD/MM/AAAA)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Endereço Residencial") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Telefone") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = role,
            onValueChange = { role = it },
            label = { Text("Cargo/Função") },
            modifier = Modifier.fillMaxWidth()
        )
        
        if (isAdminUser) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .toggleable(
                        value = isMemberAdmin,
                        onValueChange = { isMemberAdmin = it },
                        role = Role.Checkbox
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isMemberAdmin,
                    onCheckedChange = null 
                )
                Text(
                    text = "Acesso de Administrador",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && cpf.length == 11 && isValidCpf(cpf) && password.isNotBlank()) {
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
                    
                    // Aviso de sucesso
                    Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    
                    // Limpar campos após salvar (se for novo cadastro)
                    if (memberToEdit == null) {
                        name = ""
                        cpf = ""
                        birthDate = ""
                        address = ""
                        phone = ""
                        role = ""
                        password = ""
                        isMemberAdmin = false
                    }
                }
            },
            enabled = name.isNotBlank() && cpf.length == 11 && !cpfError && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                if (memberToEdit == null) Icons.Default.Add else Icons.Default.Check,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (memberToEdit == null) "Confirmar Cadastro" else "Salvar Alterações")
        }
    }
}
