package com.example.sipuhhtools.database.lhc

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LhcDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeaders(rows: List<LhcHeaderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPetaks(rows: List<LhcPetakEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJalurs(rows: List<LhcJalurEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(rows: List<LhcDetailEntity>)

    @Query("SELECT * FROM lhc_header ORDER BY kodeLhc")
    fun observeHeaders(): Flow<List<LhcHeaderEntity>>

    @Query("SELECT * FROM lhc_petak ORDER BY kodeLhc, nomorPetak")
    fun observePetaks(): Flow<List<LhcPetakEntity>>

    @Query("SELECT * FROM lhc_jalur ORDER BY kodeLhc, petak, nomorJalur")
    fun observeJalurs(): Flow<List<LhcJalurEntity>>

    @Query("SELECT * FROM lhc_detail ORDER BY kodeLhc, petak, jalur, nomorPohon")
    fun observeDetails(): Flow<List<LhcDetailEntity>>

    @Query("DELETE FROM lhc_header")
    suspend fun clearHeaders()

    @Query("DELETE FROM lhc_petak")
    suspend fun clearPetaks()

    @Query("DELETE FROM lhc_jalur")
    suspend fun clearJalurs()

    @Query("DELETE FROM lhc_detail")
    suspend fun clearDetails()

    @Query("DELETE FROM lhc_header")
    suspend fun deleteAllHeaders()

    @Query("DELETE FROM lhc_petak")
    suspend fun deleteAllPetaks()

    @Query("DELETE FROM lhc_jalur")
    suspend fun deleteAllJalurs()

    @Query("DELETE FROM lhc_detail")
    suspend fun deleteAllDetails()
}
