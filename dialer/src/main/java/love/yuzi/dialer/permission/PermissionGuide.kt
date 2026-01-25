package love.yuzi.dialer.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import love.yuzi.dialer.R
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme
import love.yuzi.dialer.utils.openAppSettings

@Suppress("UnstableCollections")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionGuide(
    modifier: Modifier = Modifier,
    permissionInfos: List<AppPermissionInfo> = emptyList(),
    content: @Composable () -> Unit
) {
    val permissionGuideState = rememberPermissionGuideState()

    val multiplePermissionsState = rememberMultiplePermissionsState(
        permissions = permissionInfos.map { it.permission },
        onPermissionsResult = { _ ->
            permissionGuideState.markFirstRequestDone()
        }
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
    ) {
        when {
            permissionInfos.isEmpty() or multiplePermissionsState.allPermissionsGranted -> {
                content()
            }

            else -> {
                PermissionGuideContent(
                    permissionInfos = permissionInfos,
                    multiplePermissionsState = multiplePermissionsState,
                    isFirstRequest = permissionGuideState.isFirstRequest
                )
            }
        }
    }
}

@Suppress("UnstableCollections")
@Composable
@OptIn(ExperimentalPermissionsApi::class)
private fun PermissionGuideContent(
    permissionInfos: List<AppPermissionInfo>,
    isFirstRequest: Boolean,
    multiplePermissionsState: MultiplePermissionsState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 24.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        val context = LocalContext.current

        val description = buildString {
            permissionInfos.forEach {
                append(stringResource(it.descriptionResId))
                append("，")
            }
            if (isNotEmpty()) {
                deleteCharAt(lastIndex)
            }
        }

        val appName = stringResource(R.string.app_name)

        Text(
            modifier = Modifier.sizeIn(maxWidth = 300.dp),
            text = stringResource(
                R.string.permission_rationale,
                appName, description
            ),
            textAlign = TextAlign.Center
        )

        val shouldShowRationale = multiplePermissionsState.shouldShowRationale or isFirstRequest

        Button(onClick = {
            if (shouldShowRationale) {
                multiplePermissionsState.launchMultiplePermissionRequest()
            } else {
                context.openAppSettings()
            }
        }) {
            val text = stringResource(
                if (shouldShowRationale)
                    R.string.permission_request
                else R.string.permission_open_app_settings
            )
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionDeniedPreview() {
    AssistiveDialerTheme {
        PermissionGuide(
            modifier = Modifier.fillMaxSize(),
            permissionInfos = listOf(
                AppPermissionInfo.READ_CONTACTS, AppPermissionInfo.CALL_PHONE
            )
        ) {
            Text("Permission Denied")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionGrantedPreview() {
    AssistiveDialerTheme {
        PermissionGuide(
            modifier = Modifier.fillMaxSize(),
            permissionInfos = emptyList()
        ) {
            Text("Permission Granted")
        }
    }
}