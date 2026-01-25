package love.yuzi.dialer.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight

@DslMarker
annotation class TopBarDsl

@Immutable
data class TopBarAction(
    val icon: ImageVector,
    val contentDescription: (@Composable () -> String),
    val onClick: () -> Unit
)

@TopBarDsl
class TopBarActionsScope internal constructor() {
    internal val actions = mutableListOf<TopBarAction>()

    fun action(
        icon: ImageVector,
        contentDescription: (@Composable () -> String),
        onClick: () -> Unit
    ) {
        actions += TopBarAction(icon, contentDescription, onClick)
    }
}

@Suppress("ParameterOrdering")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: TopBarActionsScope.() -> Unit = {}
) {
    val actionList = actions.let { TopBarActionsScope().apply(it).actions }

    TopAppBar(
        modifier = modifier,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        navigationIcon = navigationIcon,
        actions = {
            actionList.forEach { action ->
                ActionIconButton(
                    icon = action.icon,
                    contentDescription = action.contentDescription,
                    onClick = action.onClick
                )
            }
        }
    )
}