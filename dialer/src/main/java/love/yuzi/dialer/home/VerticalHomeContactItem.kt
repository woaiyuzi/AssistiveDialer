package love.yuzi.dialer.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.R
import love.yuzi.dialer.components.Avatar
import love.yuzi.dialer.components.TextAvatar
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme

@Composable
fun VerticalHomeContactItem(
    contact: Contact,
    onVoiceCallRequest: (Contact) -> Unit,
    onVideoCallRequest: (Contact) -> Unit,
    index: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier.aspectRatio(1f),
            ) {
                if (contact.avatarUri.isNullOrEmpty().not()) {
                    Avatar(
                        modifier = Modifier.fillMaxSize(),
                        data = contact.avatarUri!!,
                        enableRounded = false,
                        radius = 24.dp,
                        contentDescription = { contact.name }
                    )
                } else {
                    TextAvatar(
                        modifier = Modifier.fillMaxSize(),
                        text = (index + 1).toString(),
                        enableRounded = false,
                        fontSize = 228.sp,
                        radius = 24.dp
                    )
                }
            }

            // 右侧按钮区
            Row(
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 24.dp,
                    alignment = Alignment.CenterHorizontally
                )
            ) {
                CallButton(
                    icon = Icons.Rounded.Call,
                    text = "语音",
                    background = Color(0xFF4CAF50),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentDescription = stringResource(R.string.contact_voice_call),
                    onCall = { onVoiceCallRequest(contact) }
                )

                CallButton(
                    icon = Icons.Rounded.Videocam,
                    text = "视频",
                    background = Color(0xFF2196F3),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentDescription = stringResource(R.string.contact_video_call),
                    onCall = { onVideoCallRequest(contact) }
                )
            }
        }
    }
}

@Composable
private fun CallButton(
    icon: ImageVector,
    text: String,
    background: Color,
    contentDescription: String,
    modifier: Modifier = Modifier,
    onCall: () -> Unit = {}
) {
    Button(
        onClick = onCall,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(containerColor = background)
    ) {
        Icon(icon, contentDescription = contentDescription)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text
        )
    }
}

@Preview
@Composable
private fun HomeContactItemPreview() {
    AssistiveDialerTheme {
        VerticalHomeContactItem(
            index = 1,
            onVoiceCallRequest = {},
            onVideoCallRequest = {},
            contact = Contact(
                lookupKey = "1314",
                name = "玉子",
                phone = "1234567890",
                avatarUri = null,
                lastUpdatedTimestamp = 123456
            )
        )
    }
}
