package com.example.sipuhhtools.repository

import com.example.sipuhhtools.core.csv.LhcDetailRow
import com.example.sipuhhtools.core.csv.LhcHeaderRow
import com.example.sipuhhtools.core.csv.LhcJalurRow
import com.example.sipuhhtools.core.csv.LhcPetakRow
import com.example.sipuhhtools.database.lhc.LhcDao
import com.example.sipuhhtools.database.lhc.LhcDetailEntity
import com.example.sipuhhtools.database.lhc.LhcHeaderEntity
import com.example.sipuhhtools.database.lhc.LhcJalurEntity
import com.example.sipuhhtools.database.lhc.LhcPetakEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LhcRepository(
    private val dao: LhcDao
) {
    val headers: Flow<List<LhcHeaderRow>> = dao.observeHeaders().map { rows ->
        rows.map { LhcHeaderRow(it.sourceId, it.kodeLhc) }
    }

    val petaks: Flow<List<LhcPetakRow>> = dao.observePetaks().map { rows ->
        rows.map { LhcPetakRow(it.sourceId, it.kodeLhc, it.nomorPetak, it.luasPetak) }
    }

    val jalurs: Flow<List<LhcJalurRow>> = dao.observeJalurs().map { rows ->
        rows.map {
            LhcJalurRow(
                id = it.sourceId,
                kodeLhc = it.kodeLhc,
                petak = it.petak,
                nomorJalur = it.nomorJalur,
                arah = it.arah,
                panjang = it.panjang
            )
        }
    }

    val details: Flow<List<LhcDetailRow>> = dao.observeDetails().map { rows ->
        rows.map {
            LhcDetailRow(
                id = it.sourceId,
                kodeLhc = it.kodeLhc,
                petak = it.petak,
                jalur = it.jalur,
                nomorPohon = it.nomorPohon,
                status = it.status,
                qr = it.qr,
                jenisKayu = it.jenisKayu,
                diameter = it.diameter,
                tinggi = it.tinggi,
                volume = it.volume,
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    suspend fun importHeaders(rows: List<LhcHeaderRow>) {
        dao.insertHeaders(rows.map { LhcHeaderEntity(it.kodeLhc, it.id) })
    }

    suspend fun importPetaks(rows: List<LhcPetakRow>) {
        dao.insertPetaks(rows.map {
            LhcPetakEntity(
                kodeLhc = it.kodeLhc,
                nomorPetak = it.nomorPetak,
                luasPetak = it.luasPetak,
                sourceId = it.id
            )
        })
    }

    suspend fun importJalurs(rows: List<LhcJalurRow>) {
        dao.insertJalurs(rows.map {
            LhcJalurEntity(
                kodeLhc = it.kodeLhc,
                petak = it.petak,
                nomorJalur = it.nomorJalur,
                arah = it.arah,
                panjang = it.panjang,
                sourceId = it.id
            )
        })
    }

    suspend fun importDetails(rows: List<LhcDetailRow>) {
        dao.insertDetails(rows.map {
            LhcDetailEntity(
                kodeLhc = it.kodeLhc,
                petak = it.petak,
                jalur = it.jalur,
                nomorPohon = it.nomorPohon,
                status = it.status,
                qr = it.qr,
                jenisKayu = it.jenisKayu,
                diameter = it.diameter,
                tinggi = it.tinggi,
                volume = it.volume,
                latitude = it.latitude,
                longitude = it.longitude,
                sourceId = it.id
            )
        })
    }

    suspend fun clearAll() {
        dao.clearDetails()
        dao.clearJalurs()
        dao.clearPetaks()
        dao.clearHeaders()
    }
}
