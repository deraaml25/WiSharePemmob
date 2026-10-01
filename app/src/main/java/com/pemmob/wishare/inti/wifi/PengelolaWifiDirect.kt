package com.pemmob.wishare.inti.wifi

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Looper
import com.pemmob.wishare.data.model.PerangkatPeer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Manajer untuk mengelola siklus hidup WifiP2pManager (FT-01, FT-02, FT-03, FT-12).
 */
@SuppressLint("MissingPermission") // Asumsi izin dikelola di UI (FT-06)
class PengelolaWifiDirect(
    private val context: Context
) {
    private val p2pManager: WifiP2pManager? = context.getSystemService(Context.WIFI_P2P_SERVICE) as? WifiP2pManager
    private val channel: WifiP2pManager.Channel? = p2pManager?.initialize(context, Looper.getMainLooper(), null)

    private val _statusWifiP2p = MutableStateFlow(false) // FT-01
    val statusWifiP2p: StateFlow<Boolean> = _statusWifiP2p.asStateFlow()

    private val _daftarPeer = MutableStateFlow<List<PerangkatPeer>>(emptyList()) // FT-02
    val daftarPeer: StateFlow<List<PerangkatPeer>> = _daftarPeer.asStateFlow()

    private val _infoKoneksi = MutableStateFlow<WifiP2pInfo?>(null) // FT-03
    val infoKoneksi: StateFlow<WifiP2pInfo?> = _infoKoneksi.asStateFlow()

    private val _perangkatSaya = MutableStateFlow<PerangkatPeer?>(null) // FT-12
    val perangkatSaya: StateFlow<PerangkatPeer?> = _perangkatSaya.asStateFlow()
    
    private val _statusPencarian = MutableStateFlow(false)
    val statusPencarian = _statusPencarian.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    _statusWifiP2p.value = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    p2pManager?.requestPeers(channel) { peers ->
                        _daftarPeer.value = peers.deviceList.map { PerangkatPeer.dariWifiP2pDevice(it) }
                    }
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo?.isConnected == true) {
                        p2pManager?.requestConnectionInfo(channel) { info ->
                            _infoKoneksi.value = info
                        }
                    } else {
                        _infoKoneksi.value = null
                    }
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    val device = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    device?.let {
                        _perangkatSaya.value = PerangkatPeer.dariWifiP2pDevice(it)
                    }
                }
                WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1)
                    _statusPencarian.value = state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED
                }
            }
        }
    }

    fun daftarReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)
        }
        context.registerReceiver(receiver, intentFilter)
    }

    fun lepasReceiver() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) { /* ignored */ }
    }

    // FT-02: Peer Discovery
    fun cariPerangkat(onSuccess: () -> Unit = {}, onFailure: (Int) -> Unit = {}) {
        p2pManager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() = onSuccess()
            override fun onFailure(reason: Int) = onFailure(reason)
        })
    }
    
    fun hentikanPencarian() {
        p2pManager?.stopPeerDiscovery(channel, null)
    }

    // FT-03: Hubungkan
    fun hubungkan(alamatMac: String, onSuccess: () -> Unit = {}, onFailure: (Int) -> Unit = {}) {
        val config = WifiP2pConfig().apply {
            deviceAddress = alamatMac
        }
        p2pManager?.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() = onSuccess()
            override fun onFailure(reason: Int) = onFailure(reason)
        })
    }

    // FT-07: Putuskan Koneksi
    fun putuskanKoneksi() {
        p2pManager?.requestGroupInfo(channel) { group: WifiP2pGroup? ->
            if (group != null) {
                p2pManager.removeGroup(channel, null)
            }
        }
    }
}
