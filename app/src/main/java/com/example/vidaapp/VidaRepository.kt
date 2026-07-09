package com.example.vidaapp

import com.example.vidaapp.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class VidaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val membersCollection = db.collection("members")
    private val contentsCollection = db.collection("contents")
    private val prayersCollection = db.collection("prayers")
    private val commentsCollection = db.collection("comments")
    private val financialCollection = db.collection("financial_entries")
    private val eventsCollection = db.collection("events")

    private inline fun <reified T> observeCollection(query: Query): Flow<List<T>> = callbackFlow {
        val subscription = query.addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                trySend(snapshot.toObjects(T::class.java))
            }
        }
        awaitClose { subscription.remove() }
    }

    // Members
    fun getMembers() = observeCollection<MemberEntity>(membersCollection.orderBy("name"))
    
    suspend fun getMemberByCpf(cpf: String) = 
        membersCollection.whereEqualTo("cpf", cpf).limit(1).get().await()

    suspend fun saveMember(member: MemberEntity) {
        val docRef = if (member.idString.isEmpty()) membersCollection.document() else membersCollection.document(member.idString)
        val finalMember = if (member.idString.isEmpty()) member.copy(idString = docRef.id) else member
        docRef.set(finalMember).await()
    }

    suspend fun deleteMember(idString: String) = membersCollection.document(idString).delete().await()

    // Contents
    fun getContents() = observeCollection<ContentEntity>(contentsCollection.orderBy("timestamp", Query.Direction.DESCENDING))

    suspend fun saveContent(content: ContentEntity) {
        val docRef = contentsCollection.document()
        docRef.set(content.copy(idString = docRef.id)).await()
    }

    suspend fun deleteContent(idString: String) = contentsCollection.document(idString).delete().await()

    // Prayers
    fun getPrayers() = observeCollection<PrayerEntity>(prayersCollection.orderBy("timestamp", Query.Direction.DESCENDING))

    suspend fun savePrayer(prayer: PrayerEntity) {
        val docRef = prayersCollection.document()
        docRef.set(prayer.copy(idString = docRef.id)).await()
    }

    suspend fun deletePrayer(idString: String) = prayersCollection.document(idString).delete().await()

    // Comments
    fun getComments(contentId: String) = observeCollection<CommentEntity>(
        commentsCollection.whereEqualTo("contentId", contentId).orderBy("timestamp", Query.Direction.ASCENDING)
    )

    suspend fun addComment(comment: CommentEntity) {
        val docRef = commentsCollection.document()
        docRef.set(comment.copy(idString = docRef.id)).await()
    }

    suspend fun deleteComment(idString: String) = commentsCollection.document(idString).delete().await()

    // Financial
    fun getFinancialEntries() = observeCollection<FinancialEntryEntity>(financialCollection.orderBy("timestamp", Query.Direction.DESCENDING))

    suspend fun addFinancialEntry(entry: FinancialEntryEntity) {
        val docRef = financialCollection.document()
        docRef.set(entry.copy(idString = docRef.id)).await()
    }

    suspend fun deleteFinancialEntry(idString: String) = financialCollection.document(idString).delete().await()

    // Events
    fun getEvents() = observeCollection<EventEntity>(eventsCollection.orderBy("timestamp", Query.Direction.DESCENDING))

    suspend fun saveEvent(event: EventEntity) {
        val docRef = eventsCollection.document()
        docRef.set(event.copy(idString = docRef.id)).await()
    }

    suspend fun deleteEvent(idString: String) = eventsCollection.document(idString).delete().await()
}
