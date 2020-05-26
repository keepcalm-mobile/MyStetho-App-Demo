package com.royser.stetho_demo

import android.annotation.SuppressLint
import android.util.Log
import com.facebook.stetho.inspector.console.CLog
import com.facebook.stetho.inspector.console.ConsolePeerManager
import com.facebook.stetho.inspector.protocol.module.Console
import timber.log.Timber
import java.util.regex.Pattern

//TODO #4.1 : Create DebugTree
class DebugTree : Timber.DebugTree() {
    @SuppressLint("DefaultLocale")
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        val stackTrace =
            Throwable().stackTrace
        check(stackTrace.size > CALL_STACK_INDEX) { "Synthetic stacktrace didn't have enough elements: are you using proguard?" }
        val clazz =
            extractClassName(stackTrace[CALL_STACK_INDEX])
        val lineNumber =
            stackTrace[CALL_STACK_INDEX]
                .lineNumber
        val newMessage =
            String.format("(%s:%d) - %s", clazz, lineNumber, message)
        super.log(priority, tag, newMessage, t)
        stethoLog(priority, message)
    }

    private fun stethoLog(priority: Int, message: String) {
        val peerManager = ConsolePeerManager.getInstanceOrNull() ?: return
        val logLevel: Console.MessageLevel
        logLevel = when (priority) {
            Log.VERBOSE, Log.DEBUG -> Console.MessageLevel.DEBUG
            Log.INFO -> Console.MessageLevel.LOG
            Log.WARN -> Console.MessageLevel.WARNING
            Log.ERROR, Log.ASSERT -> Console.MessageLevel.ERROR
            else -> Console.MessageLevel.LOG
        }
        CLog.writeToConsole(
            logLevel,
            Console.MessageSource.OTHER,
            message
        )
    }

    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element) + ":" + element.lineNumber
    }

    /**
     * Extract the class name without any anonymous class suffixes (e.g., `Foo$1`
     * becomes `Foo`).
     */
    private fun extractClassName(element: StackTraceElement): String {
        var tag = element.fileName
        val m =
            ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        return tag
    }

    companion object {
        private const val CALL_STACK_INDEX = 5
        private val ANONYMOUS_CLASS =
            Pattern.compile("(\\$\\d+)+$")
    }
}