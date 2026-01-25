package love.yuzi.base

import android.content.Context
import androidx.startup.Initializer

class BaseInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        return
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}