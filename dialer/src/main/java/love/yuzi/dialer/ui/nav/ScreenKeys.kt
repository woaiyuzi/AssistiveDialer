package love.yuzi.dialer.ui.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Serializable
data object ContactManager : NavKey

@Serializable
data class ContactDetail(
    val lookupKey: String
) : NavKey

@Serializable
data class ContactPicker(
    val includeNoAvatar: Boolean = false,
    val excludeLookupKeys: List<String> = emptyList()
) : NavKey

@Serializable
data object Settings : NavKey