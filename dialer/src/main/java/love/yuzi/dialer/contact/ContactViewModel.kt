package love.yuzi.dialer.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

abstract class ContactViewModel : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ContactUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    protected fun sendUiEvent(event: ContactUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private var ioJob: Job? = null

    protected fun safeLaunch(
        onError: (Throwable) -> Unit = {},
        onFinally: () -> Unit = {},
        block: suspend () -> Unit
    ) {
        ioJob?.cancel()

        ioJob = viewModelScope.launch {
            try {
                block()
            } catch (e: Throwable) {
                if (e is CancellationException) throw e
                onError(e)
            } finally {
                ioJob = null
                onFinally()
            }
        }
    }
}