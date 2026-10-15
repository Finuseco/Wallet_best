package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CountryEntity::class,
        UserProfileEntity::class,
        SessionEntity::class,
        TransactionEntity::class,
        NotificationEntity::class,
        CpkKeyEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class CashPayDatabase : RoomDatabase() {
    abstract fun cashPayDao(): CashPayDao

    companion object {
        @Volatile
        private var INSTANCE: CashPayDatabase? = null

        fun getDatabase(context: Context): CashPayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CashPayDatabase::class.java,
                    "cashpay_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
