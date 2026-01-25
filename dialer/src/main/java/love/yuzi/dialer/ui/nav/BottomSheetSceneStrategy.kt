package love.yuzi.dialer.ui.nav

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

@OptIn(ExperimentalMaterial3Api::class)
internal class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val modalBottomSheetProperties: ModalBottomSheetProperties,
    private val skipPartiallyExpanded: Boolean, // 新增参数
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        // 创建并配置 SheetState
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = skipPartiallyExpanded
        )

        ModalBottomSheet(
            onDismissRequest = onBack,
            properties = modalBottomSheetProperties,
            sheetState = sheetState, // 应用状态
        ) {
            entry.Content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val metadata = lastEntry.metadata

        val bottomSheetProperties = metadata[BOTTOM_SHEET_KEY] as? ModalBottomSheetProperties
        val skipPartiallyExpanded = metadata[SKIP_PARTIAL_KEY] as? Boolean ?: false // 提取参数

        return bottomSheetProperties?.let { properties ->
            @Suppress("UNCHECKED_CAST")
            BottomSheetScene(
                key = lastEntry.contentKey as T,
                previousEntries = entries.dropLast(1),
                overlaidEntries = entries.dropLast(1),
                entry = lastEntry,
                modalBottomSheetProperties = properties,
                skipPartiallyExpanded = skipPartiallyExpanded, // 传递下去
                onBack = onBack
            )
        }
    }

    companion object {
        @OptIn(ExperimentalMaterial3Api::class)
        fun bottomSheet(
            modalBottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
            skipPartiallyExpanded: Boolean = false // 新增参数
        ): Map<String, Any> = mapOf(
            BOTTOM_SHEET_KEY to modalBottomSheetProperties,
            SKIP_PARTIAL_KEY to skipPartiallyExpanded // 存入 Metadata
        )

        internal const val BOTTOM_SHEET_KEY = "bottom_sheet"
        internal const val SKIP_PARTIAL_KEY = "skip_partially_expanded" // 新增 Key
    }
}