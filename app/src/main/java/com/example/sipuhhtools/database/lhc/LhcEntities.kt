package com.example.sipuhhtools.database.lhc

import androidx.room.Entity

@Entity(
    tableName = "lhc_header",
    primaryKeys = ["kodeLhc"]
)
data class LhcHeaderEntity(
    val kodeLhc: String,
    val sourceId: String
)

@Entity(
    tableName = "lhc_petak",
    primaryKeys = ["kodeLhc", "nomorPetak"]
)
data class LhcPetakEntity(
    val kodeLhc: String,
    val nomorPetak: String,
    val luasPetak: String,
    val sourceId: String
)

@Entity(
    tableName = "lhc_jalur",
    primaryKeys = ["kodeLhc", "petak", "nomorJalur"]
)
data class LhcJalurEntity(
    val kodeLhc: String,
    val petak: String,
    val nomorJalur: String,
    val arah: String,
    val panjang: String,
    val sourceId: String
)

@Entity(
    tableName = "lhc_detail",
    primaryKeys = ["kodeLhc", "petak", "jalur", "nomorPohon"]
)
data class LhcDetailEntity(
    val kodeLhc: String,
    val petak: String,
    val jalur: String,
    val nomorPohon: String,
    val status: String,
    val qr: String,
    val jenisKayu: String,
    val diameter: String,
    val tinggi: String,
    val volume: String,
    val latitude: String,
    val longitude: String,
    val sourceId: String
)
