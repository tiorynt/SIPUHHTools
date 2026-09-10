package com.example.sipuhhtools.ui.lhc

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sipuhhtools.core.csv.LhcCsvParser
import com.example.sipuhhtools.core.csv.LhcDetailRow
import com.example.sipuhhtools.core.csv.LhcHeaderRow
import com.example.sipuhhtools.core.csv.LhcJalurRow
import com.example.sipuhhtools.core.csv.LhcPetakRow

private val tabs = listOf("HDR", "PTK", "JLR", "DTL", "FND")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LhcScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    var headers by remember { mutableStateOf<List<LhcHeaderRow>>(emptyList()) }
    var petaks by remember { mutableStateOf<List<LhcPetakRow>>(emptyList()) }
    var jalurs by remember { mutableStateOf<List<LhcJalurRow>>(emptyList()) }
    var details by remember { mutableStateOf<List<LhcDetailRow>>(emptyList()) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("SIPUHH LHC") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> HeaderTab(headers) { headers = it }
                1 -> PetakTab(petaks) { petaks = it }
                2 -> JalurTab(jalurs) { jalurs = it }
                3 -> DetailTab(details) { details = it }
                else -> FindTab(headers.size, petaks.size, jalurs.size, details)
            }
        }
    }
}

@Composable
private fun FormContainer(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        content()
    }
}

@Composable
private fun HeaderTab(
    rows: List<LhcHeaderRow>,
    onImported: (List<LhcHeaderRow>) -> Unit
) {
    val context = LocalContext.current
    var kodeLhc by remember { mutableStateOf(rows.firstOrNull()?.kodeLhc.orEmpty()) }
    var status by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching { LhcCsvParser.parseHeader(context, uri) }
            .onSuccess {
                onImported(it)
                kodeLhc = it.firstOrNull()?.kodeLhc.orEmpty()
                status = "Berhasil import ${it.size} baris Header"
            }
            .onFailure { status = "Gagal import: ${it.message}" }
    }

    FormContainer {
        SectionTitle("Header Cruising")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Button(onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Header CSV")
        }
        if (status.isNotBlank()) StatusText(status)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { }, modifier = Modifier.weight(1f)) { Text("Simpan") }
            Button(onClick = { kodeLhc = "" }, modifier = Modifier.weight(1f)) { Text("Header Baru") }
        }
    }
}

@Composable
private fun PetakTab(
    rows: List<LhcPetakRow>,
    onImported: (List<LhcPetakRow>) -> Unit
) {
    val context = LocalContext.current
    var kodeLhc by remember { mutableStateOf(rows.firstOrNull()?.kodeLhc.orEmpty()) }
    var nomorPetak by remember { mutableStateOf(rows.firstOrNull()?.nomorPetak.orEmpty()) }
    var luasPetak by remember { mutableStateOf(rows.firstOrNull()?.luasPetak.orEmpty()) }
    var status by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching { LhcCsvParser.parsePetak(context, uri) }
            .onSuccess {
                onImported(it)
                val first = it.firstOrNull()
                kodeLhc = first?.kodeLhc.orEmpty()
                nomorPetak = first?.nomorPetak.orEmpty()
                luasPetak = first?.luasPetak.orEmpty()
                status = "Berhasil import ${it.size} baris Petak"
            }
            .onFailure { status = "Gagal import: ${it.message}" }
    }

    FormContainer {
        SectionTitle("Data Petak")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Field("Nomor Petak", nomorPetak) { nomorPetak = it }
        Field("Luas Petak (hektar)", luasPetak) { luasPetak = it }
        Button(onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Petak CSV")
        }
        if (status.isNotBlank()) StatusText(status)
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Simpan") }
    }
}

@Composable
private fun JalurTab(
    rows: List<LhcJalurRow>,
    onImported: (List<LhcJalurRow>) -> Unit
) {
    val context = LocalContext.current
    var kodeLhc by remember { mutableStateOf(rows.firstOrNull()?.kodeLhc.orEmpty()) }
    var nomorPetak by remember { mutableStateOf(rows.firstOrNull()?.petak.orEmpty()) }
    var nomorJalur by remember { mutableStateOf(rows.firstOrNull()?.nomorJalur.orEmpty()) }
    var arah by remember { mutableStateOf(rows.firstOrNull()?.arah ?: "UTARA") }
    var panjang by remember { mutableStateOf(rows.firstOrNull()?.panjang.orEmpty()) }
    var status by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching { LhcCsvParser.parseJalur(context, uri) }
            .onSuccess {
                onImported(it)
                val first = it.firstOrNull()
                kodeLhc = first?.kodeLhc.orEmpty()
                nomorPetak = first?.petak.orEmpty()
                nomorJalur = first?.nomorJalur.orEmpty()
                arah = first?.arah ?: "UTARA"
                panjang = first?.panjang.orEmpty()
                status = "Berhasil import ${it.size} baris Jalur"
            }
            .onFailure { status = "Gagal import: ${it.message}" }
    }

    FormContainer {
        SectionTitle("Data Jalur")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Field("Nomor Petak", nomorPetak) { nomorPetak = it }
        Field("Nomor Jalur", nomorJalur) { nomorJalur = it }
        Field("Arah Jalur", arah) { arah = it }
        Field("Panjang Jalur (meter)", panjang) { panjang = it }
        Button(onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Jalur CSV")
        }
        if (status.isNotBlank()) StatusText(status)
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Simpan") }
    }
}

@Composable
private fun DetailTab(
    rows: List<LhcDetailRow>,
    onImported: (List<LhcDetailRow>) -> Unit
) {
    val context = LocalContext.current
    var kodeLhc by remember { mutableStateOf(rows.firstOrNull()?.kodeLhc.orEmpty()) }
    var nomorPetak by remember { mutableStateOf(rows.firstOrNull()?.petak.orEmpty()) }
    var nomorJalur by remember { mutableStateOf(rows.firstOrNull()?.jalur.orEmpty()) }
    var nomorPohon by remember { mutableStateOf(rows.firstOrNull()?.nomorPohon.orEmpty()) }
    var statusPohon by remember { mutableStateOf(rows.firstOrNull()?.status ?: "POHON TEBANG") }
    var dataQr by remember { mutableStateOf(rows.firstOrNull()?.qr.orEmpty()) }
    var jenisKayu by remember { mutableStateOf(rows.firstOrNull()?.jenisKayu.orEmpty()) }
    var diameter by remember { mutableStateOf(rows.firstOrNull()?.diameter.orEmpty()) }
    var tinggi by remember { mutableStateOf(rows.firstOrNull()?.tinggi.orEmpty()) }
    var volume by remember { mutableStateOf(rows.firstOrNull()?.volume.orEmpty()) }
    var latitude by remember { mutableStateOf(rows.firstOrNull()?.latitude.orEmpty()) }
    var longitude by remember { mutableStateOf(rows.firstOrNull()?.longitude.orEmpty()) }
    var importStatus by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching { LhcCsvParser.parseDetail(context, uri) }
            .onSuccess {
                onImported(it)
                val first = it.firstOrNull()
                kodeLhc = first?.kodeLhc.orEmpty()
                nomorPetak = first?.petak.orEmpty()
                nomorJalur = first?.jalur.orEmpty()
                nomorPohon = first?.nomorPohon.orEmpty()
                statusPohon = first?.status ?: "POHON TEBANG"
                dataQr = first?.qr.orEmpty()
                jenisKayu = first?.jenisKayu.orEmpty()
                diameter = first?.diameter.orEmpty()
                tinggi = first?.tinggi.orEmpty()
                volume = first?.volume.orEmpty()
                latitude = first?.latitude.orEmpty()
                longitude = first?.longitude.orEmpty()
                importStatus = "Berhasil import ${it.size} baris Detail"
            }
            .onFailure { importStatus = "Gagal import: ${it.message}" }
    }

    FormContainer {
        SectionTitle("Detail Pohon")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Field("Nomor Petak", nomorPetak) { nomorPetak = it }
        Field("Nomor Jalur", nomorJalur) { nomorJalur = it }
        Field("Nomor Pohon", nomorPohon) { nomorPohon = it }
        Field("Status Pohon", statusPohon) { statusPohon = it }
        Field("Data QR", dataQr) { dataQr = it }
        Field("Jenis Kayu", jenisKayu) { jenisKayu = it }
        Field("Diameter (cm)", diameter) { diameter = it }
        Field("Tinggi Pohon (meter)", tinggi) { tinggi = it }
        Field("Volume Pohon (m3)", volume) { volume = it }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitude") }, singleLine = true, modifier = Modifier.weight(1f))
            OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitude") }, singleLine = true, modifier = Modifier.weight(1f))
        }
        Button(onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Detail CSV")
        }
        if (importStatus.isNotBlank()) StatusText(importStatus)
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) { Text("Simpan") }
    }
}

@Composable
private fun FindTab(
    headerCount: Int,
    petakCount: Int,
    jalurCount: Int,
    details: List<LhcDetailRow>
) {
    var search by remember { mutableStateOf("") }
    val filtered = remember(search, details) {
        if (search.isBlank()) details.take(20)
        else details.filter {
            it.qr.contains(search, ignoreCase = true) ||
                it.jenisKayu.contains(search, ignoreCase = true) ||
                it.nomorPohon.contains(search, ignoreCase = true)
        }.take(20)
    }

    FormContainer {
        Text("Jumlah Detail: ${details.size}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Header: $headerCount | Petak: $petakCount | Jalur: $jalurCount", style = MaterialTheme.typography.bodyMedium)
        Field("Search barcode / jenis / nomor pohon", search) { search = it }
        if (filtered.isEmpty()) {
            Text("Belum ada data detail yang diimport.")
        } else {
            filtered.forEach { row ->
                Text(
                    text = "${row.nomorPohon} • ${row.jenisKayu}\n${row.qr}",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun Field(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
}

@Composable
private fun StatusText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
}
