package com.pemmob.wishare.ui.layar.transfer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pemmob.wishare.data.model.ProgresTransfer

@Composable
fun LayarTransfer(
    alamatMac: String?,
    progres: ProgresTransfer = ProgresTransfer(namaFile = "Contoh_File_Besar.zip", bytesTerkirim = 250000, totalBytes = 1000000, sedangBerjalan = true),
    onBatalDiklik: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(title = { Text("Transfer Data") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.FilePresent,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Mengirim ke: $alamatMac", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = progres.namaFile, style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { progres.persentase },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${(progres.bytesTerkirim / 1024)} KB")
                Text(text = "${(progres.totalBytes / 1024)} KB")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (progres.sedangBerjalan) {
                Button(
                    onClick = onBatalDiklik,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Cancel, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Batalkan Transfer")
                }
            }
        }
    }
}
