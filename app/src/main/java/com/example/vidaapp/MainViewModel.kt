package com.example.vidaapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vidaapp.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VidaRepository()

    private val _authState = MutableStateFlow<UiState<MemberEntity>>(UiState.Idle)
    val authState: StateFlow<UiState<MemberEntity>> = _authState.asStateFlow()

    val members: StateFlow<List<MemberEntity>> = repository.getMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contents: StateFlow<List<ContentEntity>> = repository.getContents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prayers: StateFlow<List<PrayerEntity>> = repository.getPrayers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialEntries: StateFlow<List<FinancialEntryEntity>> = repository.getFinancialEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<EventEntity>> = repository.getEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val birthdayMembers: StateFlow<List<MemberEntity>> = members.map { list ->
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        list.filter { member ->
            try {
                val parts = member.birthDate.split("/")
                if (parts.size >= 2) parts[1].toInt() == currentMonth else false
            } catch (e: Exception) { false }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var loggedUser by mutableStateOf<MemberEntity?>(null)
        private set

    var memberToEdit by mutableStateOf<MemberEntity?>(null)
        private set

    fun setEditMember(member: MemberEntity?) {
        memberToEdit = member
    }

    // Função para buscar comentários de um conteúdo específico
    fun getComments(contentId: String): Flow<List<CommentEntity>> {
        return repository.getComments(contentId)
    }

    fun login(cpf: String, password: String) {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            try {
                val snapshot = repository.getMemberByCpf(cpf)
                val userList = snapshot.toObjects(MemberEntity::class.java)
                val user = userList.firstOrNull()
                
                if (user != null) {
                    if (user.password == password) {
                        loggedUser = user
                        _authState.value = UiState.Success(user)
                    } else {
                        _authState.value = UiState.Error("Senha incorreta")
                    }
                } else {
                    _authState.value = UiState.Error("Usuário não encontrado")
                }
            } catch (e: Exception) {
                _authState.value = UiState.Error("Falha na conexão: ${e.message}")
            }
        }
    }

    fun logout() {
        loggedUser = null
        _authState.value = UiState.Idle
    }

    fun resetAuthState() {
        _authState.value = UiState.Idle
    }

    fun saveMember(member: MemberEntity, onSuccess: () -> Unit) = viewModelScope.launch {
        try {
            repository.saveMember(member)
            memberToEdit = null
            onSuccess()
        } catch (e: Exception) {}
    }

    fun deleteMember(member: MemberEntity) = viewModelScope.launch {
        repository.deleteMember(member.idString)
    }

    fun saveContent(title: String, description: String, type: String, url: String) = viewModelScope.launch {
        repository.saveContent(ContentEntity(idString = "", title = title, description = description, type = type, url = url))
    }

    fun deleteContent(content: ContentEntity) = viewModelScope.launch {
        repository.deleteContent(content.idString)
    }

    fun sendPrayerRequest(request: String) = viewModelScope.launch {
        loggedUser?.let { user ->
            repository.savePrayer(PrayerEntity(idString = "", memberName = user.name, request = request, location = user.location))
        }
    }

    fun deletePrayer(prayer: PrayerEntity) = viewModelScope.launch {
        repository.deletePrayer(prayer.idString)
    }

    fun addFinancialEntry(description: String, amount: Double, category: String, isExpense: Boolean, location: String) = viewModelScope.launch {
        repository.addFinancialEntry(FinancialEntryEntity(idString = "", description = description, amount = amount, category = category, isExpense = isExpense, location = location))
    }

    fun deleteFinancialEntry(entry: FinancialEntryEntity) = viewModelScope.launch {
        repository.deleteFinancialEntry(entry.idString)
    }

    fun saveEvent(title: String, description: String, date: String, time: String, location: String) = viewModelScope.launch {
        repository.saveEvent(EventEntity(idString = "", title = title, description = description, date = date, time = time, location = location))
    }

    fun deleteEvent(event: EventEntity) = viewModelScope.launch {
        repository.deleteEvent(event.idString)
    }

    fun addComment(contentId: String, text: String) = viewModelScope.launch {
        loggedUser?.let { user ->
            repository.addComment(CommentEntity(idString = "", contentId = contentId, memberName = user.name, text = text))
        }
    }

    fun deleteComment(comment: CommentEntity) = viewModelScope.launch {
        repository.deleteComment(comment.idString)
    }
}
