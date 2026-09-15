package com.example.sipuhhtools.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sipuhhtools.database.lhc.LhcDao
import com.example.sipuhhtools.database.lhc.LhcDetailEntity
import com.example.sipuhhtools.database.lhc.LhcHeaderEntity
import com.example.sipuhhtools.database.lhc.LhcJalurEntity
import com.example.sipuhhtools.database.lhc.LhcPetakEntity

@Database(
    entities = [
        LhcHeaderEntity::class,
        LhcPetakEntity::class,
        LhcJalurEntity::class,
        LhcDetailEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SipuhhDatabase : RoomDatabase() {

    abstract fun lhcDao(): LhcDao

    companion object {
        @Volatile
        private var INSTANCE: SipuhhDatabase? = null

        fun getInstance(context: Context): SipuhhDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SipuhhDatabase::class.java,
                    "sipuhh_tools.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
