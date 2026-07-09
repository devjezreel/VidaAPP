package com.example.vidaapp

import android.content.Context
import androidx.room.*
import com.example.vidaapp.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>
    @Query("SELECT * FROM members WHERE cpf = :cpf LIMIT 1")
    suspend fun getMemberByCpf(cpf: String): MemberEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertMember(member: MemberEntity)
    @Update suspend fun updateMember(member: MemberEntity)
    @Delete suspend fun deleteMember(member: MemberEntity)
}

@Dao
interface ContentDao {
    @Query("SELECT * FROM contents ORDER BY timestamp DESC")
    fun getAllContents(): Flow<List<ContentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertContent(content: ContentEntity)
    @Delete suspend fun deleteContent(content: ContentEntity)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE contentId = :contentId ORDER BY timestamp ASC")
    fun getCommentsForContent(contentId: String): Flow<List<CommentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertComment(comment: CommentEntity)
    @Delete suspend fun deleteComment(comment: CommentEntity)
}

@Dao
interface PrayerDao {
    @Query("SELECT * FROM prayers ORDER BY timestamp DESC")
    fun getAllPrayers(): Flow<List<PrayerEntity>>
    @Insert suspend fun insertPrayer(prayer: PrayerEntity)
    @Delete suspend fun deletePrayer(prayer: PrayerEntity)
}

@Dao
interface FinancialDao {
    @Query("SELECT * FROM financial_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<FinancialEntryEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertEntry(entry: FinancialEntryEntity)
    @Delete suspend fun deleteEntry(entry: FinancialEntryEntity)
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY timestamp DESC")
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
