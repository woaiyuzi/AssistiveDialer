# Assistive Dialers

该项目提供两个快速拨号的App。

## 功能

他们的核心功能都是一样的，用大头像列表来显示联系人，然后提供一键语音/视频拨号。

## Speed Dialer

该App类似老人机的长按拨号，主要用于设置那些没有头像的联系人，此处同样以数字作为联系人头像。

## Ai Dialer

该App要求联系人必须设置头像，主要用于那些和用户具有亲属关系的联系人。

## UI

UI层的设计基本完全一样，唯一的区别就是联系人头像的显示方式。

### Home Page

应用的主页，主要用于显示联系人列表。

- ActionTopBar
  - Title 标题，此处显示应用名称
  - ActionButton 跳转到[联系人管理页面](#contact-manager-page)的按钮
  - ActionButton 跳转到[设置页面](#settings-page)的按钮

- LazyColumn 联系人列表
    - ContactItem 联系人Item
        - ContactAvatar 联系人头像，显示一个大头像
            - ContactWatermark 联系人姓名和电话号码的水印
        - CallButton 语音拨号按钮
        - CallButton 视频拨号按钮

### Contact Manager Screen

联系人管理页面，主要用于对联系人的增删改查操作。

- ActionTopBar
    - Title 标题，此处显示：联系人管理
    - ActionButton 跳转到[设置页面](#settings-page)的按钮
    - ActionButton 跳转到[帮助页面](#help-page)的按钮

- LazyColumn 联系人列表
    - ContactItem 联系人Item，点击跳转到[联系人详情页面](#contact-detial-page)
        - ContactAvatar 联系人头像，显示一个小头像
        - Text 联系人姓名
        - Text 联系人电话号码

- FloatingActionButton 添加联系人按钮，主要用于跳转到[联系人现在页面](#contact-picker-page)

### Contact Picker Page

联系人选择页面，主要用于`新增联系人`。

- ActionTopBar 此处是一个模拟的TopBar
    - Title 标题，此处显示：`选择联系人`
    - ActionButton 此处显示：`完成(count)`，如果没有任何选中的联系人，则仅仅关闭页面。