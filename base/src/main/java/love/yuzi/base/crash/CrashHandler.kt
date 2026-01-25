package love.yuzi.base.crash

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import love.yuzi.base.utils.DateTimeUtils
import love.yuzi.base.utils.DeviceInfoUtils.collectDeviceInfo
import love.yuzi.base.utils.FileWriteUtils.writeString
import java.io.File
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object CrashHandler : Thread.UncaughtExceptionHandler {
    private var context: Context? = null
    private var defaultHandler: Thread.UncaughtExceptionHandler? = null

    fun init(context: Context) {
        this.context = context.applicationContext
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        val stackTrace = Log.getStackTraceString(throwable)

        // 启动崩溃显示界面
        context?.let { ctx ->
            val filename =
                File(
                    ctx.externalCacheDir,
                    "crash-${DateTimeUtils.getCurrentTimeSeconds()}.log"
                ).absolutePath

            val content = StringBuilder()
            content.apply {
                appendLine(DateTimeUtils.getCurrentTime())
                appendLine()
                appendLine("Process: ${Application.getProcessName()}")
                appendLine()
                appendLine(ctx.collectDeviceInfo())
                appendLine()
                appendLine(stackTrace)
            }

            writeString(filename, content.toString())

            val intent = Intent(ctx, CrashReportActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                putExtra(EXTRA_CRASH_LOG_FILENAME, filename)
            }
            ctx.startActivity(intent)
        }

        // 结束当前崩溃的进程
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}