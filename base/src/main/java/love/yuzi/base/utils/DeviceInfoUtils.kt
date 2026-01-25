package love.yuzi.base.utils

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkCapabilities
import android.os.Build
import android.util.DisplayMetrics

object DeviceInfoUtils {

    fun Context.collectDeviceInfo(): String {
        val sb = StringBuilder()

        // 设备信息
        sb.appendLine("Manufacturer: ${Build.MANUFACTURER}")
        sb.appendLine("Model: ${Build.MODEL}")
        sb.appendLine("Device: ${Build.DEVICE}")
        sb.appendLine("Brand: ${Build.BRAND}")
        sb.appendLine("CPU_ABI: ${Build.SUPPORTED_ABIS.joinToString()}")
        sb.appendLine("Android Version: ${Build.VERSION.RELEASE}")
        sb.appendLine("SDK_INT: ${Build.VERSION.SDK_INT}")

        // 屏幕信息
        val metrics: DisplayMetrics = resources.displayMetrics
        sb.appendLine("Screen Width: ${metrics.widthPixels}")
        sb.appendLine("Screen Height: ${metrics.heightPixels}")
        sb.appendLine("Density: ${metrics.density}")

        // App 版本信息
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        sb.appendLine("App Version Name: ${packageInfo.versionName}")
        sb.appendLine("App Version Code: ${packageInfo.longVersionCode}")

        // 内存信息
        val memoryInfo = ActivityManager.MemoryInfo()
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(memoryInfo)
        sb.appendLine("Available Memory: ${memoryInfo.availMem / 1024 / 1024} MB")
        sb.appendLine("Total Memory: ${memoryInfo.totalMem / 1024 / 1024} MB")
        sb.appendLine("Low Memory: ${memoryInfo.lowMemory}")

        // 网络信息
        sb.appendLine(getNetworkInfoDetails())

        return sb.toString().trimEnd()
    }

    private fun Context.getNetworkInfoDetails(): String {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return "No active network"
        val capabilities =
            cm.getNetworkCapabilities(network) ?: return "Unknown network capabilities"
        val linkProperties: LinkProperties? = cm.getLinkProperties(network)

        val sb = StringBuilder()
        sb.appendLine("Network Type: ${getNetworkType()}")
        sb.appendLine("Has Internet: ${capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)}")
        sb.appendLine("Is Wi-Fi: ${capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)}")
        sb.appendLine("Is Cellular: ${capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)}")
        sb.appendLine("Link Properties: ${linkProperties?.interfaceName ?: "N/A"}")
        return sb.toString()
    }

    private fun Context.getNetworkType(): String {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return "No network"
        val capabilities = cm.getNetworkCapabilities(network) ?: return "Unknown"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile Data"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth"
            else -> "Other"
        }
    }
}