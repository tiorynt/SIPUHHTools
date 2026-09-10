package com.example.sipuhhtools.core.csv

import android.content.Context
import android.net.Uri

object LhcCsvParser {

    fun parseHeader(context: Context, uri: Uri): List<LhcHeaderRow> =
        readRows(context, uri, expectedColumns = 2).map { (_, c) ->
            LhcHeaderRow(
                id = c[0],
                kodeLhc = c[1]
            )
        }

    fun parsePetak(context: Context, uri: Uri): List<LhcPetakRow> =
        readRows(context, uri, expectedColumns = 4).map { (_, c) ->
            LhcPetakRow(
                id = c[0],
                kodeLhc = c[1],
                nomorPetak = c[2],
                luasPetak = c[3]
            )
        }

    fun parseJalur(context: Context, uri: Uri): List<LhcJalurRow> =
        readRows(context, uri, expectedColumns = 6).map { (_, c) ->
            LhcJalurRow(
                id = c[0],
                kodeLhc = c[1],
                petak = c[2],
                nomorJalur = c[3],
                arah = c[4],
                panjang = c[5]
            )
        }

    fun parseDetail(context: Context, uri: Uri): List<LhcDetailRow> =
        readRows(context, uri, expectedColumns = 13).map { (_, c) ->
            LhcDetailRow(
                id = c[0],
                kodeLhc = c[1],
                petak = c[2],
                jalur = c[3],
                nomorPohon = c[4],
                status = c[5],
                qr = c[6],
                jenisKayu = c[7],
                diameter = c[8],
                tinggi = c[9],
                volume = c[10],
                latitude = c[11],
                longitude = c[12]
            )
        }

    private fun readRows(
        context: Context,
        uri: Uri,
        expectedColumns: Int
    ): List<Pair<Int, List<String>>> {
        val input = context.contentResolver.openInputStream(uri)
            ?: error("File tidak dapat dibuka")

        return input.bufferedReader(Charsets.UTF_8).useLines { lines ->
            lines.mapIndexedNotNull { index, rawLine ->
                val lineNumber = index + 1
                val line = rawLine.trim()
                if (line.isBlank()) return@mapIndexedNotNull null

                val columns = parseCsvLine(line).toMutableList()
                if (columns.isNotEmpty()) {
                    columns[0] = columns[0].removePrefix("\uFEFF")
                }

                if (columns.size != expectedColumns) {
                    throw IllegalArgumentException(
                        "Baris $lineNumber memiliki ${columns.size} kolom, seharusnya $expectedColumns"
                    )
                }

                lineNumber to columns.map { it.trim() }
            }.toList()
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val ch = line[i]
            when {
                ch == '"' && inQuotes && i + 1 < line.length && line[i + 1] == '"' -> {
                    current.append('"')
                    i++
                }

                ch == '"' -> inQuotes = !inQuotes

                ch == ',' && !inQuotes -> {
                    result += current.toString()
                    current.clear()
                }

                else -> current.append(ch)
            }
            i++
        }

        result += current.toString()
        return result
    }
}
