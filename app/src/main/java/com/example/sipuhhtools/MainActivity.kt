package com.example.sipuhhtools

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sipuhhtools.ui.home.HomeScreen
import com.example.sipuhhtools.ui.home.SipuhhModule
import com.example.sipuhhtools.ui.theme.SIPUHHToolsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SIPUHHToolsTheme(dynamicColor = false) {
                SIPUHHToolsApp()
            }
        }
    }
}

@Composable
private fun SIPUHHToolsApp() {
    var selectedModule by remember { mutableStateOf<SipuhhModule?>(null) }

    if (selectedModule == null) {
        HomeScreen(onModuleClick = { selectedModule = it })
    } else {
        ModulePlaceholderScreen(
            module = selectedModule!!,
            onBack = { selectedModule = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModulePlaceholderScreen(
    module: SipuhhModule,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(module.title) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = module.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Modul sedang disiapkan. Dashboard utama sudah aktif.",
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            Button(onClick = onBack) {
                Text("Kembali ke Home")
            }
        }
    }
}
