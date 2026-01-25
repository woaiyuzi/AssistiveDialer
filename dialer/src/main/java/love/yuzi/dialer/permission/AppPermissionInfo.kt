package love.yuzi.dialer.permission

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import love.yuzi.dialer.R

@Immutable
data class AppPermissionInfo(
    val permission: String,
    @param:StringRes val descriptionResId: Int
) {

    companion object {
        val READ_CONTACTS = AppPermissionInfo(
            permission = android.Manifest.permission.READ_CONTACTS,
            descriptionResId = R.string.description_read_contacts
        )

        val CALL_PHONE = AppPermissionInfo(
            permission = android.Manifest.permission.CALL_PHONE,
            descriptionResId = R.string.description_call_phone
        )
    }
}