package love.yuzi.dialer.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.R
import love.yuzi.dialer.components.Avatar
import love.yuzi.dialer.components.TextAvatar
import love.yuzi.dialer.contact.uiEventHandler
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme

@Composable
fun ContactDetailScreen(
    lookupKey: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactDetailViewModel = hiltViewModel()
) {
    val contactState = viewModel.contact.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadContact(lookupKey)
    }

    uiEventHandler(viewModel)

    ContactDetailContent(
        contact = contactState.value,
        onDelete = {
            viewModel.deleteContact()
            onDelete()
        },
        modifier = modifier
    )
}

@Composable
private fun ContactDetailContent(
    contact: Contact?,
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        if (contact==null) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.contact_detail_empty))
            }
            return@Card
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(56.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier
                    .sizeIn(maxHeight = 220.dp)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            ) {
                if (contact.avatarUri != null) {
                    Avatar(
                        modifier = Modifier.fillMaxSize(),
                        data = contact.avatarUri!!,
                        enableRounded = false,
                        radius = 28.dp,
                        contentDescription = { contact.name }
                    )
                } else {
                    TextAvatar(
                        modifier = Modifier.fillMaxSize(),
                        text = contact.name.lastOrNull()?.uppercase() ?: "?",
                        fontSize = 96.sp,
                        radius = 28.dp,
                        enableRounded = false,
                    )
                }
            }

            // 姓名：加粗，间距加大
            Text(
                text = contact.name,
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            // 电话：颜色淡一点，增加对比度
            Text(
                text = contact.phone,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 删除按钮：不要用大笨按钮，用 Outline 或 TextButton 加点设计感
            OutlinedButton(
                modifier = Modifier,
                onClick = onDelete,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("删除联系人")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailEmptyAvatarPreview() {
    AssistiveDialerTheme {
        ContactDetailContent(
            modifier = Modifier.fillMaxWidth(),
            contact = Contact(
                lookupKey = "lookup_key",
                name = "王思玉",
                phone = "123 4567 8920",
                lastUpdatedTimestamp = 0,
                avatarUri = null
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactDetailHasAvatarPreview() {
    AssistiveDialerTheme {
        ContactDetailContent(
            modifier = Modifier.fillMaxWidth(),
            contact = Contact(
                lookupKey = "lookup_key",
                name = "王思玉",
                phone = "123 4567 8920",
                lastUpdatedTimestamp = 0,
                avatarUri = "R.drawable.avatar_placeholder"
            )
        )
    }
}