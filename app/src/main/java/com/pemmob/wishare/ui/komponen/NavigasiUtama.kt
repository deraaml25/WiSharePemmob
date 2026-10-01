package com.pemmob.wishare.ui.komponen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pemmob.wishare.ui.layar.MainViewModel
import com.pemmob.wishare.ui.layar.pencarian.LayarPencarian
import com.pemmob.wishare.ui.layar.riwayat.LayarRiwayat
import com.pemmob.wishare.ui.layar.transfer.LayarTransfer
import androidx.compose.runtime.collectAsState

@Composable
fun NavigasiUtama(viewModel: MainViewModel) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { NavigasiBawah(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "pencarian",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("pencarian") {
                val isMencari by viewModel.isMencari.collectAsState()
                val uiState by viewModel.uiState.collectAsState()
                
                val daftarPerangkat = if (uiState is com.pemmob.wishare.ui.layar.PencarianUiState.Success) {
                    (uiState as com.pemmob.wishare.ui.layar.PencarianUiState.Success).perangkat
                } else {
                    emptyList()
                }

                LayarPencarian(
                    daftarPerangkat = daftarPerangkat,
                    isMencari = isMencari,
                    onMulaiPencarian = { viewModel.mulaiPencarian() },
                    onHentikanPencarian = { viewModel.hentikanPencarian() },
                    onPerangkatDiklik = { perangkat ->
                        viewModel.hubungkanKePerangkat(perangkat)
                        // Navigasi ke transfer dengan MAC Address
                        navController.navigate("transfer/${perangkat.alamatMac}")
                    }
                )
            }
            
            composable("riwayat") {
                LayarRiwayat()
            }
            
            composable("transfer/{alamatMac}") { backStackEntry ->
                val mac = backStackEntry.arguments?.getString("alamatMac")
                LayarTransfer(alamatMac = mac)
            }
        }
    }
}

@Composable
fun NavigasiBawah(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Radar") },
            label = { Text("Radar") },
            selected = currentRoute == "pencarian",
            onClick = {
                if (currentRoute != "pencarian") {
                    navController.navigate("pencarian") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "Riwayat") },
            label = { Text("Riwayat") },
            selected = currentRoute == "riwayat",
            onClick = {
                if (currentRoute != "riwayat") {
                    navController.navigate("riwayat") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            }
        )
    }
}
