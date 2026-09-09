package com.example.sipuhhtools.ui.lhc

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val tabs = listOf("HDR", "PTK", "JLR", "DTL", "FND")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LhcScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("SIPUHH LHC") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("←")
                    }
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
                0 -> HeaderTab()
                1 -> PetakTab()
                2 -> JalurTab()
                3 -> DetailTab()
                else -> FindTab()
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
private fun HeaderTab() {
    var kodeLhc by remember { mutableStateOf("") }

    FormContainer {
        SectionTitle("Header Cruising")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Import Header CSV")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(onClick = { }, modifier = Modifier.weight(1f)) {
                Text("Simpan")
            }
            Button(onClick = { kodeLhc = "" }, modifier = Modifier.weight(1f)) {
                Text("Header Baru")
            }
        }
    }
}

@Composable
private fun PetakTab() {
    var kodeLhc by remember { mutableStateOf("") }
    var nomorPetak by remember { mutableStateOf("") }
    var luasPetak by remember { mutableStateOf("") }

    FormContainer {
        SectionTitle("Data Petak")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Field("Nomor Petak", nomorPetak) { nomorPetak = it }
        Field("Luas Petak (hektar)", luasPetak) { luasPetak = it }

        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Petak CSV")
        }
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Simpan")
        }
    }
}

@Composable
private fun JalurTab() {
    var kodeLhc by remember { mutableStateOf("") }
    var nomorPetak by remember { mutableStateOf("") }
    var nomorJalur by remember { mutableStateOf("") }
    var arah by remember { mutableStateOf("UTARA") }
    var panjang by remember { mutableStateOf("") }

    FormContainer {
        SectionTitle("Data Jalur")
        Field("Kode LHC", kodeLhc) { kodeLhc = it }
        Field("Nomor Petak", nomorPetak) { nomorPetak = it }
        Field("Nomor Jalur", nomorJalur) { nomorJalur = it }
        Field("Arah Jalur", arah) { arah = it }
        Field("Panjang Jalur (meter)", panjang) { panjang = it }

        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Jalur CSV")
        }
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Simpan")
        }
    }
}

@Composable
private fun DetailTab() {
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Import Detail CSV")
        }
        Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
            Text("Simpan")
        }
    }
}

@Composable
private fun FindTab() {
    var search by remember { mutableStateOf("") }

    FormContainer {
        Text(
            text = "Jumlah Data: 0",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Field("Search", search) { search = it }
        Text(
            text = "Data hasil import akan ditampilkan di sini pada tahap database berikutnya.",
            style = MaterialTheme.typography.bodyMedium
        )
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
