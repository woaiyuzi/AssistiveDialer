package love.yuzi.dialer.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.BuildConfig
import love.yuzi.dialer.R
import love.yuzi.dialer.components.ActionIconButton
import love.yuzi.dialer.components.ActionTopBar
import love.yuzi.dialer.contact.ContactItemStyle
import love.yuzi.dialer.home.HorizontallyHomeContactItem
import love.yuzi.dialer.home.VerticalHomeContactItem
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme
import love.yuzi.dialer.utils.getMaxVoiceVolume
import love.yuzi.dialer.utils.getMinVoiceVolume
import love.yuzi.dialer.utils.setCallVolume

private val placeholderContact = Contact(
    lookupKey = "",
    name = "",
    phone = "",
    avatarUri = "placeholder",
    lastUpdatedTimestamp = 0L
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    settingsState: SettingsState = rememberSettingsState()
) {
    SettingContent(settingsState = settingsState, modifier = modifier, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingContent(
    settingsState: SettingsState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settingsData by settingsState.settingsFlow.collectAsStateWithLifecycle()

    val itemSelectedBorder = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)

    val showVolumeSliderDialog = remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ActionTopBar(
                title = stringResource(R.string.settings_title),
                navigationIcon = {
                    ActionIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = { stringResource(R.string.back) },
                        onClick = {
                            onBack()
                        }
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(modifier = Modifier.padding(padding)) {

            // --- 个性化部分 ---
            item(contentType = "theme") { SettingHeader("动态配色") }

            // Material You 选项
            item(contentType = "material_you") {
                SettingSwitchItem(
                    title = "Material You",
                    subtitle = "基于壁纸动态取色",
                    icon = Icons.Rounded.Palette,
                    checked = settingsData.useMaterialYou,
                    onCheckedChange = {
                        settingsState.setUseMaterialYou(it)
                    }
                )
            }

            // 辅助功能
            item(contentType = "assistive") { SettingHeader("辅助功能") }

            // 自动开启免提
            item(contentType = "spacker") {
                SettingSwitchItem(
                    title = "扬声器",
                    subtitle = "拨号时自动打开扬声器",
                    icon = Icons.AutoMirrored.Rounded.VolumeUp,
                    checked = settingsData.autoOpenSpeaker,
                    onCheckedChange = {
                        settingsState.setAutoOpenSpeaker(it)
                    }
                )
            }

            // 自动调节通话音量
            item(contentType = "volume") {
                SettingSliderItem(
                    title = "通话音量",
                    subtitle = "拨号时自动调整到该音量",
                    icon = Icons.AutoMirrored.Rounded.VolumeUp,
                    volume = settingsData.volume,
                    onClick = { showVolumeSliderDialog.value = true }
                )
            }

            // --- 样式预览选择器 ---
            item(contentType = "contact_item_style") { SettingHeader("Item样式") }

            val isVertical = settingsData.contactItemStyle == ContactItemStyle.VERTICAL

            item(contentType = "contact_item_style_horizontal") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = if (!isVertical) itemSelectedBorder else null,
                    onClick = { settingsState.setContactItemStyle(ContactItemStyle.HORIZONTAL) }
                ) {
                    HorizontallyHomeContactItem(
                        index = 1,
                        modifier = Modifier.padding(16.dp),
                        contact = placeholderContact,
                        onVoiceCallRequest = { settingsState.setContactItemStyle(ContactItemStyle.HORIZONTAL) },
                        onVideoCallRequest = { settingsState.setContactItemStyle(ContactItemStyle.HORIZONTAL) },
                        onClick = { settingsState.setContactItemStyle(ContactItemStyle.HORIZONTAL) }
                    )
                }
            }

            item(contentType = "contact_item_style_vertical") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    border = if (isVertical) itemSelectedBorder else null,
                    onClick = { settingsState.setContactItemStyle(ContactItemStyle.VERTICAL) }
                ) {
                    VerticalHomeContactItem(
                        index = 1,
                        modifier = Modifier.padding(16.dp),
                        contact = placeholderContact,
                        onVoiceCallRequest = { settingsState.setContactItemStyle(ContactItemStyle.VERTICAL) },
                        onVideoCallRequest = { settingsState.setContactItemStyle(ContactItemStyle.VERTICAL) },
                        onClick = { settingsState.setContactItemStyle(ContactItemStyle.VERTICAL) }
                    )
                }
            }

            // --- 其他项 ---
            item(contentType = "spacer") { Spacer(modifier = Modifier.height(16.dp)) }
            item(contentType = "about") {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${BuildConfig.VERSION_NAME}_${BuildConfig.BUILD_TIME}-${BuildConfig.BUILD_TYPE}",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            item(contentType = "spacer") { Spacer(modifier = Modifier.height(32.dp)) }
        }

        if (showVolumeSliderDialog.value) {
            VolumeSliderDialog(
                volume = settingsData.volume,
                onVolumeChange = {
                    settingsState.setVolume(it)
                    context.setCallVolume(it
                    )
                },
                onDismiss = { showVolumeSliderDialog.value = false },
                valueRange = context.getMinVoiceVolume().toFloat()..context.getMaxVoiceVolume()
                    .toFloat()
            )
        }
    }
}


@Composable
private fun VolumeSliderDialog(
    onVolumeChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    volume: Int = 8
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("确定")
            }
        },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp
                )
            ) {
                Text("通话音量: $volume", style = MaterialTheme.typography.bodyLarge)
                Slider(
                    value = volume.toFloat(),
                    onValueChange = { onVolumeChange(it.toInt()) },
                    valueRange = valueRange,
                    steps = (valueRange.endInclusive - valueRange.start).toInt() - 1
                )
            }
        }
    )
}

@Composable
private fun SettingHeader(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
    )
}

@Composable
private fun SettingSwitchItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingSliderItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: (Int) -> Unit,
    volume: Int = 8
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(volume) })
            .padding(16.dp, 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
        Row(
            horizontalArrangement = Arrangement.End
        ) {
            Text(text = "$volume", style = MaterialTheme.typography.bodyLarge)
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AssistiveDialerTheme() {
        SettingsScreen(
            modifier = Modifier.fillMaxSize(),
            settingsState = rememberSettingsState(),
            onBack = {}

        )
    }
}