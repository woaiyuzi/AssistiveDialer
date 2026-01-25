package love.yuzi.dialer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import love.yuzi.dialer.R
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme

@Composable
fun Avatar(
    data: Any,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enableRounded: Boolean = false,
    radius: Dp = 12.dp,
    contentDescription: @Composable () -> String
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.avatar_placeholder),
        error = painterResource(R.drawable.avatar_placeholder),
        contentDescription = contentDescription.invoke(),
        modifier = modifier
            .size(size)
            .aspectRatio(1f)
            .clip(if (enableRounded) CircleShape else RoundedCornerShape(radius)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun TextAvatar(
    text: String,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    enableRounded: Boolean = false,
    radius: Dp = 12.dp,
    background: Color = MaterialTheme.colorScheme.primary,
    fontSize: TextUnit = 20.sp,
    enableBold: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = modifier
            .size(size)
            .aspectRatio(1f)
            .clip(if (enableRounded) CircleShape else RoundedCornerShape(radius))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            fontSize = fontSize,
            color = color,
            fontWeight = if (enableBold) FontWeight.Bold else null
        )
    }
}

@Preview
@Composable
private fun AvatarPreview() {
    AssistiveDialerTheme {
        Avatar(
            data = R.drawable.avatar_placeholder,
            contentDescription = { "Avatar" }
        )
    }
}

@Preview
@Composable
private fun TextAvatarPreview() {
    AssistiveDialerTheme {
        TextAvatar("玉", enableBold = true)
    }
}