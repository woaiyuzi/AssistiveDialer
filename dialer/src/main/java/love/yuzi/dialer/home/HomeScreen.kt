package love.yuzi.dialer.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.R
import love.yuzi.dialer.components.ActionTopBar
import love.yuzi.dialer.contact.ContactItemStyle
import love.yuzi.dialer.contact.ContactList
import love.yuzi.dialer.contact.ContactUiState
import love.yuzi.dialer.contact.uiEventHandler
import love.yuzi.dialer.settings.rememberSettingsState
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme
import kotlin.time.Clock

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavContactManager: () -> Unit,
    onNavToSettings: () -> Unit,
    onAddContactRequest: (List<String>) -> Unit,
    onVoiceCallRequest: (Contact) -> Unit,
    onVideoCallRequest: (Contact) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    uiEventHandler(viewModel, snackbarHostState)

    LaunchedEffect(viewModel) {
        viewModel.silentRefreshContacts()
    }

    HomeScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onNavContactManager = onNavContactManager,
        onNavToSettings = onNavToSettings,
        onAddContactRequest = { onAddContactRequest(uiState.contacts.map { it.lookupKey }) },
        onRefresh = { viewModel.pullRefreshContacts() },
        onVoiceCallRequest = onVoiceCallRequest,
        onVideoCallRequest = onVideoCallRequest,
        modifier = modifier
    )
}

@Suppress("UnstableCollections", "EffectKeys")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun HomeScreenContent(
    uiState: ContactUiState,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onNavContactManager: () -> Unit = {},
    onNavToSettings: () -> Unit = {},
    onAddContactRequest: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onVoiceCallRequest: (Contact) -> Unit = {},
    onVideoCallRequest: (Contact) -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            ActionTopBar(
                title = stringResource(R.string.app_name)
            ) {
                action(
                    icon = Icons.Rounded.Person,
                    contentDescription = { stringResource(R.string.text_contact_manager) },
                    onClick = onNavContactManager
                )

                action(
                    icon = Icons.Rounded.Settings,
                    contentDescription = { stringResource(R.string.settings_text) },
                    onClick = onNavToSettings
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        strokeCap = StrokeCap.Round
                    )
                }
                return@Box
            }

            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize(),
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh
            ) {
                if (uiState.isEmpty) {
                    EmptyContactList(
                        onClick = onAddContactRequest
                    )
                } else {
                    val settingsState by rememberSettingsState().settingsFlow.collectAsStateWithLifecycle()
                    ContactList(
                        modifier = Modifier.fillMaxSize(),
                        contacts = uiState.contacts,
                    ) { contact ->
                        when (settingsState.contactItemStyle) {
                            ContactItemStyle.VERTICAL -> {
                                VerticalHomeContactItem(
                                    modifier = Modifier.padding(16.dp),
                                    contact = contact,
                                    onVoiceCallRequest = { onVoiceCallRequest(contact) },
                                    onVideoCallRequest = { onVideoCallRequest(contact) },
                                    index = uiState.contacts.indexOf(contact)
                                )
                            }

                            ContactItemStyle.HORIZONTAL -> {
                                HorizontallyHomeContactItem(
                                    modifier = Modifier.padding(16.dp),
                                    contact = contact,
                                    onVoiceCallRequest = { onVoiceCallRequest(contact) },
                                    onVideoCallRequest = { onVideoCallRequest(contact) },
                                    index = uiState.contacts.indexOf(contact)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContactList(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = modifier
                    .fillParentMaxSize()
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.contact_empty_add_text))
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, name = "Permission Denied")
@Composable
private fun PermissionDeniedPreview() {
    AssistiveDialerTheme {
        HomeScreenContent(
            uiState = ContactUiState(isLoading = false)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, name = "Empty Contact List")
@Composable
private fun EmptyContactListPreview() {
    AssistiveDialerTheme {
        HomeScreenContent(uiState = ContactUiState(isLoading = false))
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, name = "Loading")
@Composable
private fun LoadingPreview() {
    AssistiveDialerTheme {
        HomeScreenContent(
            uiState = ContactUiState(isLoading = true)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, name = "Refreshing")
@Composable
private fun RefreshingPreview() {
    AssistiveDialerTheme {
        HomeScreenContent(
            uiState = ContactUiState(isRefreshing = true)
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true, name = "Loading")
@Composable
private fun ContactListPreview() {
    val context = LocalContext.current
    val drawableId = R.drawable.avatar_placeholder

    val uri = "android.resource://${context.packageName}/$drawableId"

    val contacts = List(5) {
        Contact(
            lookupKey = "lookup_key_$it",
            name = "王思玉_$it",
            phone = "123 4567 890$it",
            avatarUri = uri,
            lastUpdatedTimestamp = Clock.System.now().epochSeconds + it
        )
    }

    AssistiveDialerTheme {
        HomeScreenContent(
            uiState = ContactUiState(contacts = contacts)
        )
    }
}