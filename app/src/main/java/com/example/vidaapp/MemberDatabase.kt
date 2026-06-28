package com.example.vidaapp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.google.firebase.firestore.PropertyName
import kotlinx.coroutines.flow.Flow

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
) { constructor() : this("", "", "", "", "", "", "", "123456", "Brasil - Sede", false) }

@Entity(tableName = "contents")
data class ContentEntity(
    @PrimaryKey val idString: String = "",
    val title: String = "",
    val description: String = "",
    val type: String = "",
    val url: String = "",
    val location: String = "Geral",
    val timestamp: Long = System.currentTimeMillis()
) { constructor() : this("", "", "", "", "", "Geral", System.currentTimeMillis()) }

@Entity(tableName = "prayers")
data class PrayerEntity(
    @PrimaryKey val idString: String = "",
    val memberName: String = "",
    val request: String = "",
    val location: String = "Geral",
    val timestamp: Long = System.currentTimeMillis()
) { constructor() : this("", "", "", "Geral", System.currentTimeMillis()) }

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val idString: String = "",
    val contentId: String = "",
    val memberName: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
) { constructor() : this("", "", "", "", System.currentTimeMillis()) }

@Entity(tableName = "financial_entries")
data class FinancialEntryEntity(
    @PrimaryKey val idString: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val isExpense: Boolean = false,
    val location: String = "Brasil - Sede",
    val timestamp: Long = System.currentTimeMillis()
) { constructor() : this("", "", 0.0, "", false, "Brasil - Sede", System.currentTimeMillis()) }

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val idString: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val location: String = "Geral",
    val timestamp: Long = System.currentTimeMillis()
) { constructor() : this("", "", "", "", "", "Geral", System.currentTimeMillis()) }

@Dao
interface MemberDao {
    @androidx.room.Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>
    @androidx.room.Query("SELECT * FROM members WHERE cpf = :cpf LIMIT 1")
    suspend fun getMemberByCpf(cpf: String): MemberEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertMember(member: MemberEntity)
    @Update suspend fun updateMember(member: MemberEntity)
    @Delete suspend fun deleteMember(member: MemberEntity)
}

@Dao
interface ContentDao {
    @androidx.room.Query("SELECT * FROM contents ORDER BY timestamp DESC")
    fun getAllContents(): Flow<List<ContentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertContent(content: ContentEntity)
    @Delete suspend fun deleteContent(content: ContentEntity)
}

@Dao
interface CommentDao {
    @androidx.room.Query("SELECT * FROM comments WHERE contentId = :contentId ORDER BY timestamp ASC")
    fun getCommentsForContent(contentId: String): Flow<List<CommentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertComment(comment: CommentEntity)
    @Delete suspend fun deleteComment(comment: CommentEntity)
}

@Dao
interface PrayerDao {
    @androidx.room.Query("SELECT * FROM prayers ORDER BY timestamp DESC")
    fun getAllPrayers(): Flow<List<PrayerEntity>>
    @Insert suspend fun insertPrayer(prayer: PrayerEntity)
    @Delete suspend fun deletePrayer(prayer: PrayerEntity)
}

@Dao
interface FinancialDao {
    @androidx.room.Query("SELECT * FROM financial_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<FinancialEntryEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEntry(entry: FinancialEntryEntity)
    @Delete suspend fun deleteEntry(entry: FinancialEntryEntity)
}

@Dao
interface EventDao {
    @androidx.room.Query("SELECT * FROM events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<EventEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEvent(event: EventEntity)
    @Delete suspend fun deleteEvent(event: EventEntity)
}

@Database(entities = [MemberEntity::class, ContentEntity::class, PrayerEntity::class, CommentEntity::class, FinancialEntryEntity::class, EventEntity::class], version = 11, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun contentDao(): ContentDao
    abstract fun prayerDao(): PrayerDao
    abstract fun commentDao(): CommentDao
    abstract fun financialDao(): FinancialDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "church_database")
                .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
