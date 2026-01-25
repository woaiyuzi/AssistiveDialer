package love.yuzi.dialer.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import love.yuzi.dialer.contact.ContactItemStyle
import love.yuzi.dialer.permission.permissionDataStore

data class SettingsData(
    val useMaterialYou: Boolean = true,
    val contactItemStyle: ContactItemStyle = ContactItemStyle.HORIZONTAL,
    val autoOpenSpeaker: Boolean = true,
    val volume: Int = 8
)

@Stable
class SettingsState(
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope
) {
    companion object {
        private val useMaterialYouKey = booleanPreferencesKey("use_material_you")
        private val contactItemStyleKey = stringPreferencesKey("contact_item_style")
        private val autoOpenSpeakerKey = booleanPreferencesKey("auto_open_speaker")
        private val volumeKey = intPreferencesKey("volume")
    }

    val settingsFlow: StateFlow<SettingsData> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }
        .map { prefs ->
            SettingsData(
                useMaterialYou = prefs[useMaterialYouKey] ?: true,
                contactItemStyle = safeValueOf(prefs[contactItemStyleKey]),
                autoOpenSpeaker = prefs[autoOpenSpeakerKey] ?: true,
                volume = prefs[volumeKey] ?: 8
            )
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsData()
        )

    fun setUseMaterialYou(value: Boolean) {
        scope.launch {
            dataStore.edit { it[useMaterialYouKey] = value }
        }
    }

    fun setContactItemStyle(style: ContactItemStyle) {
        scope.launch {
            dataStore.edit { it[contactItemStyleKey] = style.name }
        }
    }

    fun setAutoOpenSpeaker(value: Boolean) {
        scope.launch {
            dataStore.edit { it[autoOpenSpeakerKey] = value }
        }
    }

    fun setVolume(value: Int) {
        scope.launch {
            dataStore.edit { it[volumeKey] = value }
        }
    }

    private fun safeValueOf(name: String?): ContactItemStyle {
        return try {
            ContactItemStyle.valueOf(name ?: "")
        } catch (_: Exception) {
            ContactItemStyle.HORIZONTAL
        }
    }
}

@Composable
internal fun rememberSettingsState(): SettingsState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    return remember(context, scope) {
        SettingsState(context.permissionDataStore, scope)
    }
}