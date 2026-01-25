package love.yuzi.dialer.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import love.yuzi.dialer.detail.ContactDetailScreen
import love.yuzi.dialer.home.HomeScreen
import love.yuzi.dialer.manager.ContactManagerScreen
import love.yuzi.dialer.permission.AppPermissionInfo
import love.yuzi.dialer.permission.PermissionGuide
import love.yuzi.dialer.picker.ContactPickerScreen
import love.yuzi.dialer.settings.SettingsData
import love.yuzi.dialer.settings.SettingsScreen
import love.yuzi.dialer.settings.rememberSettingsState
import love.yuzi.dialer.ui.nav.BottomSheetSceneStrategy
import love.yuzi.dialer.ui.nav.ContactDetail
import love.yuzi.dialer.ui.nav.ContactManager
import love.yuzi.dialer.ui.nav.ContactPicker
import love.yuzi.dialer.ui.nav.Home
import love.yuzi.dialer.ui.nav.Settings
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme
import love.yuzi.dialer.utils.setCallVolume
import love.yuzi.dialer.utils.videoCall
import love.yuzi.dialer.utils.voiceCall

abstract class DialerActivity : ComponentActivity() {

    abstract val includeNoAvatar: Boolean

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val backStack = rememberNavBackStack(Home)
            val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

            val permissions = listOf(
                AppPermissionInfo.READ_CONTACTS,
                AppPermissionInfo.CALL_PHONE,
            )

            val settingsState by rememberSettingsState().settingsFlow.collectAsStateWithLifecycle()

            AssistiveDialerTheme(
                dynamicColor = settingsState.useMaterialYou
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PermissionGuide(
                        permissionInfos = permissions,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavDisplay(
                            backStack = backStack,
                            onBack = { backStack.removeLastOrNull() },
                            sceneStrategy = bottomSheetStrategy,
                            entryDecorators = listOf(
                                rememberSaveableStateHolderNavEntryDecorator(),
                                rememberViewModelStoreNavEntryDecorator()
                            ),
                            entryProvider = entryProvider {
                                EntryProvider(backStack, this,settingsState)
                            }
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    @Composable
    private fun EntryProvider(
        backStack: NavBackStack<NavKey>,
        entryProviderScope: EntryProviderScope<NavKey>,
        settingsData: SettingsData
    ) {
        with(entryProviderScope) {
            entry<Home> {
                val context = LocalContext.current
                val hapticFeedback = LocalHapticFeedback.current

                HomeScreen(
                    onNavContactManager = { backStack.add(ContactManager) },
                    onNavToSettings = { backStack.add(Settings) },
                    onAddContactRequest = {
                        backStack.add(
                            ContactPicker(
                                includeNoAvatar = includeNoAvatar,
                                excludeLookupKeys = it
                            )
                        )
                    },
                    onVoiceCallRequest = {
                        context.voiceCall(it.phone, settingsData.autoOpenSpeaker)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        context.setCallVolume(settingsData.volume)
                    },
                    onVideoCallRequest = {
                        context.videoCall(it.phone)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        context.setCallVolume(settingsData.volume)
                    }
                )
            }

            entry<ContactPicker>(
                metadata = BottomSheetSceneStrategy.bottomSheet()
            ) {
                ContactPickerScreen(
                    onFinish = { backStack.removeLastOrNull() },
                    includeNoAvatar = it.includeNoAvatar,
                    excludeLookupKeys = it.excludeLookupKeys
                )
            }

            entry<ContactManager> {
                ContactManagerScreen(
                    onNavToSettings = {
                        backStack.add(Settings)
                    },
                    onNavToDetail = {
                        backStack.add(ContactDetail(it))
                    },
                    onBack = { backStack.removeLastOrNull() },
                    onAddContactRequest = {
                        backStack.add(ContactPicker(includeNoAvatar, it))
                    }
                )
            }

            entry<ContactDetail>(
                metadata = BottomSheetSceneStrategy.bottomSheet(skipPartiallyExpanded = true)
            ) {
                ContactDetailScreen(
                    lookupKey = it.lookupKey,
                    onDelete = { backStack.removeLastOrNull() }
                )
            }

            entry<Settings> {
                SettingsScreen(
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    }
}
