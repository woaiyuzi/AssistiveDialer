package love.yuzi.base.crash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import love.yuzi.base.R
import java.io.File

internal const val EXTRA_CRASH_LOG_FILENAME = "crash_log_filename"


class CrashReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crash_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val crashFile = File(intent.getStringExtra(EXTRA_CRASH_LOG_FILENAME) ?: "unknown")

        loadFileToTextView(crashFile, findViewById(R.id.tv_crash_log))

        findViewById<FloatingActionButton>(R.id.fab_share).setOnClickListener {
            shareCrashLogFile(crashFile)
        }
    }

    private fun Context.loadFileToTextView(file: File, textView: TextView) {
        CoroutineScope(Dispatchers.IO).launch { // IO 线程读文件
            if (file.exists()) {
                val content = file.readText() // 读取整个文件
                withContext(Dispatchers.Main) { // 切换到主线程更新 UI
                    textView.text = content
                }
            } else {
                withContext(Dispatchers.Main) {
                    textView.text = getString(R.string.text_unknown_file, file.absolutePath)
                }
            }
        }
    }


    fun Context.shareCrashLogFile(file: File) {
        if (!file.exists()) return

        // 获取文件 URI
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.file_provider",
            file
        )

        // 创建分享 Intent
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM, uri) // 注意用 EXTRA_STREAM 分享文件
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 给接收应用临时读权限
        }

        startActivity(Intent.createChooser(intent, getString(R.string.text_share_log)))
    }
}