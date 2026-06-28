package com.example.vidaapp

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val membersCollection = db.collection("members")
    private val contentsCollection = db.collection("contents")
    private val prayersCollection = db.collection("prayers")
    private val commentsCollection = db.collection("comments")
    private val financialCollection = db.collection("financial_entries")
    private val eventsCollection = db.collection("events")

    val members: StateFlow<List<MemberEntity>> = observeCollection<MemberEntity>(membersCollection.orderBy("name"))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contents: StateFlow<List<ContentEntity>> = observeCollection<ContentEntity>(contentsCollection.orderBy("timestamp", Query.Direction.DESCENDING))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prayers: StateFlow<List<PrayerEntity>> = observeCollection<PrayerEntity>(prayersCollection.orderBy("timestamp", Query.Direction.DESCENDING))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialEntries: StateFlow<List<FinancialEntryEntity>> = observeCollection<FinancialEntryEntity>(financialCollection.orderBy("timestamp", Query.Direction.DESCENDING))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val events: StateFlow<List<EventEntity>> = observeCollection<EventEntity>(eventsCollection.orderBy("timestamp", Query.Direction.DESCENDING))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aniversariantes do mês atual calculados a partir da lista de membros
    val birthdayMembers: StateFlow<List<MemberEntity>> = members.map { list ->
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        list.filter { member ->
            try {
                val parts = member.birthDate.split("/")
                if (parts.size >= 2) {
                    parts[1].toInt() == currentMonth
                } else false
            } catch (e: Exception) { false }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var memberToEdit by mutableStateOf<MemberEntity?>(null)
        private set

    var loggedUser by mutableStateOf<MemberEntity?>(null)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    private inline fun <reified T> observeCollection(query: Query): Flow<List<T>> = callbackFlow {
        val subscription = query.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val items = snapshot.toObjects(T::class.java)
                trySend(items)
            }
        }
        awaitClose { subscription.remove() }
    }

    fun setEditMember(member: MemberEntity?) {
        memberToEdit = member
    }

    fun login(cpf: String, password: String, onResult: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val snapshot = membersCollection.whereEqualTo("cpf", cpf).limit(1).get().await()
                val user = snapshot.toObjects(MemberEntity::class.java).firstOrNull()
                
                if (user != null) {
                    if (user.password == password) {
                        loggedUser = user
                        loginError = null
                        onResult(true, true)
                    } else {
                        loginError = "Senha incorreta"
                        onResult(false, true)
                    }
                } else {
                    loginError = "Usuário não encontrado"
                    onResult(false, false)
                }
            } catch (e: Exception) {
                loginError = "Erro ao conectar"
                onResult(false, false)
            }
        }
    }

    fun logout() {
        loggedUser = null
    }

    fun saveMember(member: MemberEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val docRef = if (member.idString.isEmpty()) membersCollection.document() else membersCollection.document(member.idString)
            val finalMember = if (member.idString.isEmpty()) member.copy(idString = docRef.id) else member
            docRef.set(finalMember).await()
            memberToEdit = null
            onSuccess()
        }
    }

    fun deleteMember(member: MemberEntity) {
        viewModelScope.launch {
            membersCollection.document(member.idString).delete().await()
        }
    }

    fun saveContent(title: String, description: String, type: String, url: String) {
        viewModelScope.launch {
            val docRef = contentsCollection.document()
            val content = ContentEntity(idString = docRef.id, title = title, description = description, type = type, url = url)
            docRef.set(content).await()
        }
    }

    fun deleteContent(content: ContentEntity) {
        viewModelScope.launch {
            contentsCollection.document(content.idString).delete().await()
        }
    }

    fun sendPrayerRequest(request: String) {
        viewModelScope.launch {
            loggedUser?.let { user ->
                val docRef = prayersCollection.document()
                val prayer = PrayerEntity(idString = docRef.id, memberName = user.name, request = request, location = user.location)
                docRef.set(prayer).await()
            }
        }
    }

    fun deletePrayer(prayer: PrayerEntity) {
        viewModelScope.launch {
            prayersCollection.document(prayer.idString).delete().await()
        }
    }

    fun getComments(contentId: String): Flow<List<CommentEntity>> {
        return observeCollection<CommentEntity>(commentsCollection.whereEqualTo("contentId", contentId).orderBy("timestamp", Query.Direction.ASCENDING))
    }

    fun addComment(contentId: String, text: String) {
        viewModelScope.launch {
            loggedUser?.let { user ->
                val docRef = commentsCollection.document()
                val comment = CommentEntity(idString = docRef.id, contentId = contentId, memberName = user.name, text = text)
                docRef.set(comment).await()
            }
        }
    }

    fun deleteComment(comment: CommentEntity) {
        viewModelScope.launch {
            commentsCollection.document(comment.idString).delete().await()
        }
    }

    fun addFinancialEntry(description: String, amount: Double, category: String, isExpense: Boolean, location: String) {
        viewModelScope.launch {
            val docRef = financialCollection.document()
            val entry = FinancialEntryEntity(
                idString = docRef.id,
                description = description,
                amount = amount,
                category = category,
                isExpense = isExpense,
                location = location,
                timestamp = System.currentTimeMillis()
            )
            docRef.set(entry).await()
        }
    }

    fun deleteFinancialEntry(entry: FinancialEntryEntity) {
        viewModelScope.launch {
            financialCollection.document(entry.idString).delete().await()
        }
    }

    // Eventos
    fun saveEvent(title: String, description: String, date: String, time: String, location: String) {
        viewModelScope.launch {
            val docRef = eventsCollection.document()
            val event = EventEntity(
                idString = docRef.id,
                title = title,
                description = description,
                date = date,
                time = time,
                location = location,
                timestamp = System.currentTimeMillis()
            )
            docRef.set(event).await()
        }
    }

    fun deleteEvent(event: EventEntity) {
        viewModelScope.launch {
            eventsCollection.document(event.idString).delete().await()
        }
    }
}
