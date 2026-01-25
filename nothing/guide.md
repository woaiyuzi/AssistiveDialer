# 动画
- AnimatedContent
- animateItem

# 布局
- AppScaffold
- AppBox

# 状态
- Loading
- Success

```kotlin
val uiState: StateFlow<UserListUiState> = repository.allUsers
    .map { users -> 
        if (users.isEmpty()) UserListUiState.Empty else UserListUiState.Success(users) 
    }
    .stateIn(
        scope = viewModelScope,
        // 技巧 1：改用 Lazily。只要 ViewModel 还在，管道就通着。
        // 因为返回列表页时，ViewModel 通常没被杀掉，数据会瞬间显示。
        started = SharingStarted.Lazily, 
        
        // 技巧 2：如果一定要用 WhileSubscribed，
        // initialValue 可以尝试获取 Repository 当前的缓存快照（如果有的话）
        initialValue = repository.cachedUsers?.let { UserListUiState.Success(it) } ?: UserListUiState.Loading
    )
```

# 事件
- Error
- Other
- SharedFlow
- LaunchedEffect(Unit)

# Plugin
- copilot AI补全
- Jetpack Compose Preview Creator 快速生成Preview code
- SonarQube for IDE 实时lint
- Translation 翻译
- Adb idea 调试工具
- Key Promoter x 快捷键提示

# 工具
- AGP Upgrade Assistant APG升级工具
- Update Dependencies with Gemini 依赖升级工具
- Android SDK Upgrade Assistant SDK升级工具

# 组件
- ActionTopBar
- 