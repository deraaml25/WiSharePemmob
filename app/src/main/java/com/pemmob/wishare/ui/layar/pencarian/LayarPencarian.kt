package com.pemmob.wishare.ui.layar.pencarian

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.wishare.data.model.PerangkatPeer
import com.pemmob.wishare.ui.komponen.AnimasiRadar
import com.pemmob.wishare.ui.komponen.KartuPerangkat
import com.pemmob.wishare.ui.komponen.TopBarWiShare
import com.pemmob.wishare.ui.tema.PermukaanGelapVarian

@Composable
fun LayarPencarian(
    daftarPerangkat: List<PerangkatPeer>,
    isMencari: Boolean,
    onMulaiPencarian: () -> Unit,
    onHentikanPencarian: () -> Unit,
    onPerangkatDiklik: (PerangkatPeer) -> Unit
) {
    Scaffold(
        topBar = {
            TopBarWiShare(statusAktif = true)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Bagian Atas: Animasi Radar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimasiRadar(
                    isMencari = isMencari,
                    modifier = Modifier.size(300.dp)
                )
                
                // Ikon Perangkat Saya di Tengah
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                // Tombol Mulai/Henti (Sesuai Referensi)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (isMencari) {
                        Button(
                            onClick = onHentikanPencarian,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hentikan Pencarian")
                        }
                    } else {
                        Button(
                            onClick = onMulaiPencarian,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = CircleShape
                        ) {
                            Text("Mulai Mencari...")
                        }
                    }
                }
            }

            // Bagian Bawah: Daftar Perangkat
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TERDETEKSI DI DEKAT ANDA",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${daftarPerangkat.size} Ditemukan",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                LazyColumn {
                    items(daftarPerangkat) { perangkat ->
                        KartuPerangkat(
                            perangkat = perangkat,
                            onClick = { onPerangkatDiklik(perangkat) }
                        )
                    }
                }
            }
        }
    }
}
