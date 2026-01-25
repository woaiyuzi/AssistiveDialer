package love.yuzi.base.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.charset.Charset

object FileWriteUtils {

    fun writeString(
        filename: String,
        content: String,
        append: Boolean = false,
        charset: Charset = Charsets.UTF_8
    ) {
        val file = File(filename)
        // 自动创建父目录
        file.parentFile?.mkdirs()

        CoroutineScope(Dispatchers.IO).launch {
            if (append) {
                file.appendText(content, charset)

            } else {
                file.writeText(content, charset)
            }
        }

    }
}