package love.yuzi.dialer.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState

suspend fun SnackbarHostState.showMessage(
    message: String,
    actionLabel: String = "关闭",
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    this.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = duration
    )
}