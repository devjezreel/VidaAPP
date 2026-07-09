package com.example.vidaapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.PropertyName

@Entity(tableName = "members")
data class MemberEntity(
    @PrimaryKey val idString: String = "",
    val name: String = "",
    val cpf: String = "",
    val birthDate: String = "",
    val address: String = "",
    val phone: String = "",
    val role: String = "",
    val password: String = "123456",
    val location: String = "Brasil - Sede",
    @get:PropertyName("isAdmin") @set:PropertyName("isAdmin") var isAdmin: Boolean = false
) {
    // Construtor sem argumentos para o Firebase
    constructor() : this("", "", "", "", "", "", "", "123456", "Brasil - Sede", false)
}

@Entity(tableName = "contents")
data class ContentEntity(
    @PrimaryKey val idString: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "",
    val url: String = "",
    val location: String = "Geral",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", "Geral", System.currentTimeMillis())
}

@Entity(tableName = "prayers")
data class PrayerEntity(
    @PrimaryKey val idString: String = "",
    val memberName: String = "",
    val request: String = "",
    val location: String = "Geral",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "Geral", System.currentTimeMillis())
}

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val idString: String = "",
    val contentId: String = "",
    val memberName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", System.currentTimeMillis())
}

@Entity(tableName = "financial_entries")
data class FinancialEntryEntity(
    @PrimaryKey val idString: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val isExpense: Boolean = false,
    val location: String = "Brasil - Sede",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", 0.0, "", false, "Brasil - Sede", System.currentTimeMillis())
}

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val idString: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "Geral",
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", "Geral", System.currentTimeMillis())
}
