package love.yuzi.base.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Clock

object DateTimeUtils {

    fun getCurrentTime(pattern: String = "yyyy-MM-dd HH:mm:ss z"): String {
        // 使用 ZonedDateTime 获取带时区的时间
        return ZonedDateTime.now().format(DateTimeFormatter.ofPattern(pattern))
    }

    fun getCurrentTimeSeconds(): Long {
        return Clock.System.now().epochSeconds
    }
}

