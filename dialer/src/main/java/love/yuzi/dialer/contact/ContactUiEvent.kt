package love.yuzi.dialer.contact

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import love.yuzi.dialer.R
import love.yuzi.dialer.utils.UiText
import love.yuzi.dialer.utils.showMessage
import love.yuzi.dialer.utils.showToast

sealed interface ContactUiEvent {

    val uiText: UiText

    data object LoadFailed : ContactUiEvent {
        override val uiText: UiText
            get() = UiText.StringResource(R.string.contact_load_failed)
    }

    data object OperationFailed : ContactUiEvent {
        override val uiText: UiText
            get() = UiText.StringResource(R.string.contact_operation_failed)
    }

    data class Updated(val count: Int) : ContactUiEvent {
        override val uiText: UiText
            get() = UiText.StringResource(R.string.contact_updated, count)
    }
}

@Suppress("ComposableNaming")
@Composable
internal fun uiEventHandler(
    viewModel: ContactViewModel,
    snackbarHostState: SnackbarHostState? = null
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            val message = event.uiText.asString(context)
            snackbarHostState?.showMessage(message) ?: showToast(context, message)
        }
    }
}