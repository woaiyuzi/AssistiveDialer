package love.yuzi.aidialer

import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import love.yuzi.dialer.ui.DialerActivity

@AndroidEntryPoint
class MainActivity : DialerActivity() {
    override val includeNoAvatar: Boolean
        get() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }
}