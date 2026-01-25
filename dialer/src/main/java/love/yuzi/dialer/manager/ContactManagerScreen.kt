package love.yuzi.dialer.manager

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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import love.yuzi.contact.model.Contact
import love.yuzi.dialer.R
import love.yuzi.dialer.components.ActionIconButton
import love.yuzi.dialer.components.ActionTopBar
import love.yuzi.dialer.contact.ContactList
import love.yuzi.dialer.contact.ContactUiState
import love.yuzi.dialer.contact.SimpleContactItem
import love.yuzi.dialer.contact.uiEventHandler
import love.yuzi.dialer.ui.theme.AssistiveDialerTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import sh.calvin.reorderable.rememberReorderableLazyListState
import timber.log.Timber

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ContactManagerScreen(
    onNavToSettings: () -> Unit,
    onNavToDetail: (String) -> Unit,
    onAddContactRequest: (List<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactManagerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    uiEventHandler(viewModel, snackbarHostState)

    ContactManagerContent(
        uiState = uiState,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
        onNavToSettings = onNavToSettings,
        onAddContactRequest = { onAddContactRequest(uiState.contacts.map { it.lookupKey }) },
        onNavToDetail = onNavToDetail,
        onRefresh = { viewModel.pullRefreshContacts() },
        modifier = modifier,
        onSyncToOriginalContacts = { viewModel.updateContactsOrderIndex(it) }
    )
}

/**
 * 页面内容
 *
 * @param uiState 联系人列表的状态
 * @param modifier 修饰符
 */
@Suppress("UnstableCollections", "EffectKeys")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun ContactManagerContent(
    uiState: ContactUiState,
    onBack: () -> Unit,
    onNavToSettings: () -> Unit,
    onNavToDetail: (String) -> Unit,
    onAddContactRequest: () -> Unit,
    onRefresh: () -> Unit,
    onSyncToOriginalContacts: (List<Contact>) -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            ActionTopBar(
                title = stringResource(R.string.contact_manager_title, uiState.contacts.size),
                navigationIcon = {
                    ActionIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = { stringResource(R.string.back) },
                        onClick = onBack
                    )
                }
            ) {
                action(
                    icon = Icons.Rounded.PersonAdd,
                    contentDescription = { stringResource(R.string.contact_add) },
                    onClick = onAddContactRequest
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
            // Loading state
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

            // 下拉刷新组件
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize(),
                isRefreshing = uiState.isRefreshing, // 是否刷新取决于 uiState 的 isRefreshing
                onRefresh = onRefresh
            ) {
                if (uiState.isEmpty) {
                    // 联系人为空时的状态
                    EmptyContactList(
                        onClick = onAddContactRequest
                    )
                } else {
                    // 存在联系人时的状态，此列表可以拖动排序
                    ReorderableContactList(
                        uiState = uiState,
                        onSyncToOriginalContacts = {
                            // 更新数据库联系人的priority, 使用按index -> priority的简单实现即可
                            onSyncToOriginalContacts(it)
                        },
                        onContactClick = onNavToDetail
                    )
                }
            }
        }
    }
}

/**
 * 可拖动排序的联系人列表
 *
 * @param uiState 联系人列表的状态
 * @param onSyncToOriginalContacts 同步至原始联系人列表
 */
@Suppress("UnstableCollections")
@Composable
private fun ReorderableContactList(
    uiState: ContactUiState,
    onSyncToOriginalContacts: (List<Contact>) -> Unit,
    onContactClick: (String) -> Unit,
) {
    // 用于触发拖动反馈
    val hapticFeedback = LocalHapticFeedback.current

    // 原始联系人列表
    val originalContacts = uiState.contacts
    // 仅拖动排序用的可变联系人列表
    val reorderableContacts = remember { uiState.contacts.toMutableStateList() }

    // 辅助记录排序是否变更，只要触发过onMove则表示有变更
    var isMoved by remember { mutableStateOf(false) }

    // LazyList的状态，此处服务于LazyColumn
    val lazyListState = rememberLazyListState()
    // 拖动排序组件的状态
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        reorderableContacts.add(to.index, reorderableContacts.removeAt(from.index))
        hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        isMoved = true
        Timber.d("Item Moved from ${from.index} to ${to.index}")
    }

    // 是否需要同步排序至数据库，在拖动调整过顺序后触发
    val needSyncToOriginalContacts by remember(
        isMoved,
        reorderableLazyListState.isAnyItemDragging
    ) {
        derivedStateOf {
            isMoved && !reorderableLazyListState.isAnyItemDragging
        }
    }

    // 同步至数据库
    LaunchedEffect(needSyncToOriginalContacts, onSyncToOriginalContacts) {
        if (needSyncToOriginalContacts) {
            onSyncToOriginalContacts(reorderableContacts)
            Timber.d("Sync to original contacts")
        }
    }

    // 从数据库同步
    LaunchedEffect(originalContacts) {
        syncForReorderableContacts(reorderableContacts, originalContacts)
    }

    // 联系人列表
    ContactList(
        modifier = Modifier.fillMaxSize(),
        contacts = reorderableContacts,
        state = lazyListState
    ) { contact ->
        ContactItem(
            contact = contact,
            reorderableLazyListState = reorderableLazyListState,
            hapticFeedback = hapticFeedback,
            onContactClick = onContactClick
        )
    }
}

/**
 * 联系人列表Item
 *
 * @param contact 联系人
 * @param reorderableLazyListState 拖动排序组件的状态
 */
@Composable
private fun LazyItemScope.ContactItem(
    contact: Contact,
    reorderableLazyListState: ReorderableLazyListState,
    hapticFeedback: HapticFeedback,
    onContactClick: (String) -> Unit
) {
    ReorderableItem(reorderableLazyListState, key = contact.lookupKey) { isDragging ->
        SimpleContactItem(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(if (isDragging) 1f else 0f)
                .longPressDraggableHandle(
                    onDragStarted = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                    },
                    onDragStopped = {
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                    },
                )
                .animateItem(),
            contact = contact,
            onClick = { onContactClick(it.lookupKey) },
            selected = isDragging,
        )
    }
}

/**
 * 从数据库同步到reorderableContacts，该同步会进行 diff 检查
 *
 * @param reorderableContacts 仅拖动排序用的可变联系人列表
 * @param originalContacts 原始联系人列表
 */
private fun syncForReorderableContacts(
    reorderableContacts: SnapshotStateList<Contact>,
    originalContacts: List<Contact>
) {
    Timber.d("--- Sync for reorderable contacts: diff check start ---")
    // 1. 数量不等（增删），全量同步
    if (reorderableContacts.size != originalContacts.size) {
        reorderableContacts.clear()
        reorderableContacts.addAll(originalContacts)
        Timber.d("Full sync: because of size difference")
        return
    }

    // 2. Diff 检查
    originalContacts.forEachIndexed { index, originalContact ->
        val reorderableContact = reorderableContacts[index]

        if (reorderableContact != originalContact) {
            reorderableContacts[index] = originalContact
        }
    }

    Timber.d("--- Sync for reorderable contacts: diff check end ---")
}

/**
 * 联系人为空时的状态
 *
 * @param onClick 点击事件
 */
@Composable
private fun EmptyContactList(
    onClick: () -> Unit = {}
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillParentMaxSize()
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(text = stringResource(R.string.contact_empty_add_text))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactManagerPreview() {
    val contacts = List(10) {
        Contact(
            lookupKey = "lookup_key_$it",
            name = "王思玉_$it",
            phone = "123 4567 890$it",
            lastUpdatedTimestamp = it.toLong(),
            avatarUri = null
        )
    }

    AssistiveDialerTheme {
        ContactManagerContent(
            uiState = ContactUiState(contacts = contacts),
            onNavToSettings = TODO(),
            onNavToDetail = TODO(),
            onAddContactRequest = TODO(),
            onRefresh = TODO(),
            onSyncToOriginalContacts = TODO(),
            modifier = TODO(),
            snackbarHostState = TODO(),
            onBack = TODO()
        )
    }

}