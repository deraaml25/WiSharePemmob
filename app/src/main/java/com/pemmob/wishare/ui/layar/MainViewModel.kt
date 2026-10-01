package com.pemmob.wishare.ui.layar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pemmob.wishare.data.model.PerangkatPeer
import com.pemmob.wishare.inti.wifi.PengelolaWifiDirect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State Ui untuk mematuhi kaidah Arsitektur MVVM (UiState: Loading, Success, Error)
 */
sealed class PencarianUiState {
    object Idle : PencarianUiState()
    object Loading : PencarianUiState()
    data class Success(val perangkat: List<PerangkatPeer>) : PencarianUiState()
    data class Error(val pesan: String) : PencarianUiState()
}

class MainViewModel(
    private val pengelolaWifi: PengelolaWifiDirect
) : ViewModel() {

    // Pengelola Soket TCP
    val pengelolaSoket = com.pemmob.wishare.inti.jaringan.PengelolaSoket()

    private val _uiState = MutableStateFlow<PencarianUiState>(PencarianUiState.Idle)
    val uiState: StateFlow<PencarianUiState> = _uiState.asStateFlow()

    private val _isMencari = MutableStateFlow(false)
    val isMencari = _isMencari.asStateFlow()

    init {
        // Obeservasi status koneksi grup (Kapan harus buka soket)
        viewModelScope.launch {
            pengelolaWifi.infoKoneksi.collect { info ->
                if (info != null && info.groupFormed) {
                    if (info.isGroupOwner) {
                        pengelolaSoket.mulaiSebagaiServer()
                    } else {
                        val hostAddress = info.groupOwnerAddress?.hostAddress
                        if (hostAddress != null) {
                            pengelolaSoket.mulaiSebagaiKlien(hostAddress)
                        }
                    }
                } else {
                    pengelolaSoket.putuskan()
                }
            }
        }


        // Obeservasi status pencarian P2P (State Management & UDF)
        viewModelScope.launch {
            pengelolaWifi.statusPencarian.collect { status ->
                _isMencari.value = status
                if (status) {
                    _uiState.value = PencarianUiState.Loading
                }
            }
        }

        // Observasi daftar perangkat P2P
        viewModelScope.launch {
            pengelolaWifi.daftarPeer.collect { peers ->
                if (peers.isNotEmpty()) {
                    _uiState.value = PencarianUiState.Success(peers)
                } else if (!_isMencari.value) {
                    _uiState.value = PencarianUiState.Idle
                }
            }
        }
    }

    fun mulaiPencarian() {
        pengelolaWifi.cariPerangkat(
            onFailure = { _uiState.value = PencarianUiState.Error("Gagal memulai pencarian (Kode: $it)") }
        )
    }

    fun hentikanPencarian() {
        pengelolaWifi.hentikanPencarian()
        _uiState.value = PencarianUiState.Idle
    }

    fun hubungkanKePerangkat(perangkat: PerangkatPeer) {
        pengelolaWifi.hubungkan(
            alamatMac = perangkat.alamatMac,
            onFailure = { _uiState.value = PencarianUiState.Error("Gagal menghubungkan ke ${perangkat.namaPerangkat}") }
        )
    }
}
