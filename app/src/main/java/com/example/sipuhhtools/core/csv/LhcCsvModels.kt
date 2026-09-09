package com.example.sipuhhtools.core.csv

data class LhcHeaderRow(
    val id: String,
    val kodeLhc: String
)

data class LhcPetakRow(
    val id: String,
    val kodeLhc: String,
    val nomorPetak: String,
    val luasPetak: String
)

data class LhcJalurRow(
    val id: String,
    val kodeLhc: String,
    val petak: String,
    val nomorJalur: String,
    val arah: String,
    val panjang: String
)

data class LhcDetailRow(
    val id: String,
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
    val longitude: String
)
