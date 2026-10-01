package com.pemmob.wishare.inti.perizinan

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PemintaIzinAplikasi(
    modifier: Modifier = Modifier,
    onIzinDiberikan: @Composable () -> Unit
) {
    val izinDibutuhkan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.NEARBY_WIFI_DEVICES)
    } else {
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    var izinSudahDiberikan by remember { mutableStateOf(false) }
    
    val launcherIzin = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val semuaDiberikan = permissionsMap.values.all { it }
        izinSudahDiberikan = semuaDiberikan
    }

    // Cek izin saat pertama kali diluncurkan
    LaunchedEffect(Unit) {
        launcherIzin.launch(izinDibutuhkan.toTypedArray())
    }

    if (izinSudahDiberikan) {
        onIzinDiberikan()
    } else {
        // Tampilan peringatan jika izin belum diberikan
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Izin Diperlukan",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Aplikasi ini memerlukan izin Lokasi/Perangkat Sekitar agar dapat mendeteksi dan mengirim file melalui Wi-Fi Direct.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { launcherIzin.launch(izinDibutuhkan.toTypedArray()) }) {
                    Text("Berikan Izin")
                }
            }
        }
    }
}
