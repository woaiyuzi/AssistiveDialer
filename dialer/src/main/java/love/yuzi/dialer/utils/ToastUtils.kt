package love.yuzi.dialer.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun showToast(context: Context, message: Any, duration: Int = Toast.LENGTH_SHORT) {
    // 切换到主线程显示
    CoroutineScope(Dispatchers.Main).launch {
        val text = when (message) {
            is String -> message
            is Int -> context.getString(message)
            else -> message.toString()
        }
        Toast.makeText(context, text, duration).show()
    }
}

@Suppress("ModifierRequired", "ComposableNaming")
@Composable
fun showToast(message: Any, duration: Int = Toast.LENGTH_SHORT) {
    val context = LocalContext.current
    showToast(context, message, duration)
}