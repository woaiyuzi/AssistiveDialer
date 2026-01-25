package love.yuzi.dialer.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DragGestureGuide(modifier: Modifier = Modifier) {
    // 控制位移和缩放的动画变量
    val infiniteTransition = rememberInfiniteTransition(label = "drag_guide")

    // 模拟手指按下的缩放（0.8 表示按下，1.0 表示抬起）
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                1f at 0           // 初始状态
                0.8f at 500       // 500ms 时按下去
                0.8f at 1500      // 保持按下状态到 1500ms
                1f at 2000        // 抬起
            }
        ), label = "scale"
    )

    // 模拟向上拖动的位移
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -100f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0f at 500         // 500ms 前不动
                -100f at 1500     // 500ms 到 1500ms 向上滑动 100px
                0f at 2000         // 快速回位
            }
        ), label = "offset"
    )

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.offset(y = offsetY.dp) // 应用位移
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp, // 一个小手的图标
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale) // 应用按下的缩放
                    .shadow(elevation = 8.dp, shape = CircleShape)
            )
            Text(
                "长按拖动排序",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DragGestureGuidePreview() {
    DragGestureGuide()
}