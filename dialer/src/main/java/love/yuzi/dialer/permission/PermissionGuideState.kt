package love.yuzi.dialer.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Stable
internal class PermissionGuideState(
    private val dataStore: DataStore<Preferences>,
    scope: CoroutineScope // 这里不再需要 private val，因为只在 init 中使用
) {
    companion object {
        private val isFirstRequestKey = booleanPreferencesKey("first_request")
    }

    var isFirstRequest by mutableStateOf(true)
        private set

    init {
        dataStore.data
            .catch { e ->
                if (e is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw e
                }
            }
            .map { it[isFirstRequestKey] ?: true }
            .onEach { isFirstRequest = it }
            .launchIn(scope)
    }

    private val _scope = scope

    fun markFirstRequestDone() {
        _scope.launch {
            dataStore.edit { prefs ->
                prefs[isFirstRequestKey] = false
            }
        }
    }
}

@Composable
internal fun rememberPermissionGuideState(): PermissionGuideState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    return remember(context, scope) {
        PermissionGuideState(context.permissionDataStore, scope)
    }
}