package com.pemmob.wishare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pemmob.wishare.inti.perizinan.PemintaIzinAplikasi
import com.pemmob.wishare.inti.wifi.PengelolaWifiDirect
import com.pemmob.wishare.ui.komponen.NavigasiUtama
import com.pemmob.wishare.ui.layar.MainViewModel
import com.pemmob.wishare.ui.tema.TemaWiShare

class MainActivity : ComponentActivity() {

    private lateinit var pengelolaWifiDirect: PengelolaWifiDirect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        pengelolaWifiDirect = PengelolaWifiDirect(this)

        val factory = viewModelFactory {
            initializer { MainViewModel(pengelolaWifiDirect) }
        }

        setContent {
            TemaWiShare {
                val mainViewModel: MainViewModel = viewModel(factory = factory)

                PemintaIzinAplikasi {
                    NavigasiUtama(viewModel = mainViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        pengelolaWifiDirect.daftarReceiver()
    }

    override fun onPause() {
        super.onPause()
        pengelolaWifiDirect.lepasReceiver()
    }
}