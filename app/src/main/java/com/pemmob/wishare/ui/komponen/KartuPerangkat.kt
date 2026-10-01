package com.pemmob.wishare.ui.komponen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pemmob.wishare.data.model.PerangkatPeer
import com.pemmob.wishare.data.model.StatusPeer
import com.pemmob.wishare.ui.tema.HijauSukses

@Composable
fun KartuPerangkat(
    perangkat: PerangkatPeer,
    jarakEstimasi: String = "0.0 m",
    kualitasSinyal: String = "Baik",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon Perangkat
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = dapatkanIkonPerangkat(perangkat.namaPerangkat),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Info Perangkat
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = perangkat.namaPerangkat,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = jarakEstimasi,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = " • ",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (perangkat.status == StatusPeer.TERSEDIA) "Siap Pasang" else perangkat.status.name,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Indikator Sinyal
            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(HijauSukses)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = kualitasSinyal,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun dapatkanIkonPerangkat(nama: String): ImageVector {
    val namaLower = nama.lowercase()
    return when {
        namaLower.contains("mac") || namaLower.contains("pc") || namaLower.contains("desktop") -> Icons.Default.Computer
        namaLower.contains("ipad") || namaLower.contains("tab") -> Icons.Default.Tablet
        else -> Icons.Default.Smartphone
    }
}
