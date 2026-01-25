package love.yuzi.base

import android.app.Application
import android.util.Log
import love.yuzi.base.crash.CrashHandler
import love.yuzi.base.utils.DateTimeUtils
import timber.log.Timber
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

abstract class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        CrashHandler.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(SimpleGlobalTagTree())
        } else {
            val logFile = File(externalCacheDir, "app_logs.txt")

            FileLoggingTree.setup(logFile)
        }
    }
}

private class SimpleGlobalTagTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Log.println(priority, "AssistiveDialer", message)
    }
}

/**
 * 使用 Kotlin Object 实现的单例日志 Tree
 */
object FileLoggingTree : Timber.Tree() {

    private var writer: BufferedWriter? = null

    /**
     * 初始化方法，建议在 Application 类中调用
     */
    fun setup(logFile: File) {
        try {
            if (!logFile.exists()) {
                logFile.parentFile?.mkdirs()
                logFile.createNewFile()
            }
            // 使用 FileWriter(file, true) 开启追加模式
            writer = BufferedWriter(FileWriter(logFile, true))

            // 植入 Timber
            Timber.plant(this)
        } catch (e: IOException) {
            Timber.tag("FileLoggingTree").e(e, "无法初始化日志文件写入器")
        }
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if(priority < Log.INFO) return

        val currentWriter = writer ?: return

        // 线程安全同步
        synchronized(this) {
            try {
                val timestamp = DateTimeUtils.getCurrentTime()
                val priorityChar = when (priority) {
                    Log.INFO -> "I"
                    Log.WARN -> "W"
                    Log.ERROR -> "E"
                    Log.ASSERT -> "A"
                    else -> "U"
                }

                // 格式化：时间 级别/标签: 内容
                currentWriter.write("$timestamp $priorityChar/${tag ?: "AssistiveDialer"}: $message\n")

                t?.let {
                    currentWriter.write(Log.getStackTraceString(it))
                    currentWriter.newLine()
                }

                // 立即冲刷，防止崩溃时丢失关键日志
                currentWriter.flush()
            } catch (e: IOException) {
                Timber.tag("FileLoggingTree").e(e, "写入日志失败")
            }
        }
    }
}