package com.pemmob.wishare.ui.layar.riwayat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ItemRiwayat(val id: String, val judul: String, val deskripsi: String, val isFile: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LayarRiwayat() {
    val daftarRiwayat = listOf(
        ItemRiwayat("1", "Tugas_Akhir.pdf", "Selesai diunduh - 2.4 MB", true),
        ItemRiwayat("2", "Pesan dari Pixel 7", "Halo, file sudah saya kirim ya!", false),
        ItemRiwayat("3", "Foto_Liburan.jpg", "Selesai dikirim - 4.1 MB", true)
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Riwayat Transaksi") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(daftarRiwayat, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (item.isFile) Icons.Default.FileDownload else Icons.Default.Chat,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = item.judul, fontWeight = FontWeight.Bold)
                            Text(text = item.deskripsi, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
