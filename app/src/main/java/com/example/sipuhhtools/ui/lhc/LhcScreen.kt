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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sipuhhtools.core.csv.LhcCsvParser
import com.example.sipuhhtools.core.csv.LhcDetailRow
import com.example.sipuhhtools.core.csv.LhcHeaderRow
import com.example.sipuhhtools.core.csv.LhcJalurRow
import com.example.sipuhhtools.core.csv.LhcPetakRow
import com.example.sipuhhtools.database.SipuhhDatabase
import com.example.sipuhhtools.repository.LhcRepository
import kotlinx.coroutines.launch

private val tabs = listOf("HDR", "PTK", "JLR", "DTL", "FND")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LhcScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val context = LocalContext.current.applicationContext
    val repository = remember {
        LhcRepository(SipuhhDatabase.getInstance(context).lhcDao())
    }
    val scope = rememberCoroutineScope()

    val headers by repository.headers.collectAsStateWithLifecycle(initialValue = emptyList())
    val petaks by repository.petaks.collectAsStateWithLifecycle(initialValue = emptyList())
    val jalurs by repository.jalurs.collectAsStateWithLifecycle(initialValue = emptyList())
    val details by repository.details.collectAsStateWithLifecycle(initialValue = emptyList())

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
                0 -> HeaderTab(headers) { rows ->
                    scope.launch { repository.importHeaders(rows) }
                }
                1 -> PetakTab(petaks) { rows ->
                    scope.launch { repository.importPetaks(rows) }
                }
                2 -> JalurTab(jalurs) { rows ->
                    scope.launch { repository.importJalurs(rows) }
                }
                3 -> DetailTab(details) { rows ->
                    scope.launch { repository.importDetails(rows) }
                }
                else -> FindTab(
                    headerCount = headers.size,
                    petakCount = petaks.size,
                    jalurCount = jalurs.size,
                    details = details,
                    onClearAll = { scope.launch { repository.clearAll() } }
                )
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
    var kodeLhc by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    LaunchedEffect(rows.firstOrNull()) {
        if (kodeLhc.isBlank()) kodeLhc = rows.firstOrNull()?.kodeLhc.orEmpty()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching { LhcCsvParser.parseHeader(context, uri) }
            .onSuccess {
                onImported(it)
                kodeLhc = it.firstOrNull()?.kodeLhc.orEmpty()
                status = "Berhasil import ${it.size} baris Header. Data disimpan permanen."
            }
            .onFailure { status = "Gagal import: ${it.message}" }
    }

    FormContainer {
        SectionTitle("Header Cruising")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Button(
            onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Import Header CSV")
        }
        if (status.isNotBlank()) StatusText(status)
        Text("Tersimpan: ${rows.size} header", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun PetakTab(
    rows: List<LhcPetakRow>,
    onImported: (List<LhcPetakRow>) -> Unit
) {
    val context = LocalContext.current
    var kodeLhc by remember { mutableStateOf("") }
    var nomorPetak by remember { mutableStateOf("") }
    var luasPetak by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    LaunchedEffect(rows.firstOrNull()) {
        val first = rows.firstOrNull() ?: return@LaunchedEffect
        if (kodeLhc.isBlank()) kodeLhc = first.kodeLhc
        if (nomorPetak.isBlank()) nomorPetak = first.nomorPetak
        if (luasPetak.isBlank()) luasPetak = first.luasPetak
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        runCatching { LhcCsvParser.parsePetak(context, uri) }
            .onSuccess {
                onImported(it)
                val first = it.firstOrNull()
                kodeLhc = first?.kodeLhc.orEmpty()
                nomorPetak = first?.nomorPetak.orEmpty()
                luasPetak = first?.luasPetak.orEmpty()
                status = "Berhasil import ${it.size} baris Petak. Data disimpan permanen."
            }
            .onFailure { status = "Gagal import: ${it.message}" }
    }

    FormContainer {
        SectionTitle("Data Petak")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Field("Nomor Petak", nomorPetak) { nomorPetak = it }
        Field("Luas Petak (hektar)", luasPetak) { luasPetak = it }
        Button(
            onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Import Petak CSV") }
        if (status.isNotBlank()) StatusText(status)
        Text("Tersimpan: ${rows.size} petak", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun JalurTab(
    rows: List<LhcJalurRow>,
    onImported: (List<LhcJalurRow>) -> Unit
) {
    val context = LocalContext.current
    var kodeLhc by remember { mutableStateOf("") }
    var nomorPetak by remember { mutableStateOf("") }
    var nomorJalur by remember { mutableStateOf("") }
    var arah by remember { mutableStateOf("UTARA") }
    var panjang by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    LaunchedEffect(rows.firstOrNull()) {
        val first = rows.firstOrNull() ?: return@LaunchedEffect
        if (kodeLhc.isBlank()) kodeLhc = first.kodeLhc
        if (nomorPetak.isBlank()) nomorPetak = first.petak
        if (nomorJalur.isBlank()) nomorJalur = first.nomorJalur
        if (panjang.isBlank()) panjang = first.panjang
        arah = first.arah.ifBlank { "UTARA" }
    }

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
                status = "Berhasil import ${it.size} baris Jalur. Data disimpan permanen."
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
        Button(
            onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Import Jalur CSV") }
        if (status.isNotBlank()) StatusText(status)
        Text("Tersimpan: ${rows.size} jalur", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun DetailTab(
    rows: List<LhcDetailRow>,
    onImported: (List<LhcDetailRow>) -> Unit
) {
    val context = LocalContext.current
    var kodeLhc by remember { mutableStateOf("") }
    var nomorPetak by remember { mutableStateOf("") }
    var nomorJalur by remember { mutableStateOf("") }
    var nomorPohon by remember { mutableStateOf("") }
    var statusPohon by remember { mutableStateOf("POHON TEBANG") }
    var dataQr by remember { mutableStateOf("") }
    var jenisKayu by remember { mutableStateOf("") }
    var diameter by remember { mutableStateOf("") }
    var tinggi by remember { mutableStateOf("") }
    var volume by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var importStatus by remember { mutableStateOf("") }

    LaunchedEffect(rows.firstOrNull()) {
        val first = rows.firstOrNull() ?: return@LaunchedEffect
        if (kodeLhc.isBlank()) kodeLhc = first.kodeLhc
        if (nomorPetak.isBlank()) nomorPetak = first.petak
        if (nomorJalur.isBlank()) nomorJalur = first.jalur
        if (nomorPohon.isBlank()) nomorPohon = first.nomorPohon
        if (dataQr.isBlank()) dataQr = first.qr
        if (jenisKayu.isBlank()) jenisKayu = first.jenisKayu
        if (diameter.isBlank()) diameter = first.diameter
        if (tinggi.isBlank()) tinggi = first.tinggi
        if (volume.isBlank()) volume = first.volume
        if (latitude.isBlank()) latitude = first.latitude
        if (longitude.isBlank()) longitude = first.longitude
        statusPohon = first.status.ifBlank { "POHON TEBANG" }
    }

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
                importStatus = "Berhasil import ${it.size} baris Detail. Data disimpan permanen."
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
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = { launcher.launch(arrayOf("text/*", "text/csv", "application/csv", "*/*")) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Import Detail CSV") }
        if (importStatus.isNotBlank()) StatusText(importStatus)
        Text("Tersimpan: ${rows.size} detail pohon", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun FindTab(
    headerCount: Int,
    petakCount: Int,
    jalurCount: Int,
    details: List<LhcDetailRow>,
    onClearAll: () -> Unit
) {
    var search by remember { mutableStateOf("") }
    val filtered = remember(search, details) {
        if (search.isBlank()) details.take(50)
        else details.filter {
            it.qr.contains(search, ignoreCase = true) ||
                it.jenisKayu.contains(search, ignoreCase = true) ||
                it.nomorPohon.contains(search, ignoreCase = true) ||
                it.petak.contains(search, ignoreCase = true)
        }.take(50)
    }

    FormContainer {
        Text(
            text = "Jumlah Detail: ${details.size}",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Header: $headerCount | Petak: $petakCount | Jalur: $jalurCount",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Data tersimpan di database SIPUHH Tools dan tetap ada setelah aplikasi ditutup.",
            style = MaterialTheme.typography.bodySmall
        )
        Field("Search barcode / jenis / nomor pohon / petak", search) { search = it }

        if (filtered.isEmpty()) {
            Text("Belum ada data detail yang tersimpan.")
        } else {
            filtered.forEach { row ->
                Text(
                    text = "${row.nomorPohon} • ${row.jenisKayu} • Petak ${row.petak}\n${row.qr}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Button(
            onClick = onClearAll,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hapus Semua Data LHC")
        }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
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
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun StatusText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
