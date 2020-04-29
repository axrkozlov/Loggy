package com.clawmarks.loggy.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.clawmarks.loggy.report.ReportType
import com.clawmarks.loggy.buffer.AnalyticsBuffer
import com.clawmarks.loggy.buffer.LogcatBuffer
import com.clawmarks.loggy.filelist.LoggyFileList
import com.clawmarks.loggy.filelist.LoggyFileListState
import com.clawmarks.loggy.sender.LoggySender
import com.clawmarks.loggy.prefs.LoggyPrefs
import com.clawmarks.loggy.prefs.LoggyDefaultPrefs
import com.clawmarks.loggy.userinteraction.PeriodicCheckIdleDispatcher
import com.clawmarks.loggy.userinteraction.UserInteractionDispatcher
import com.clawmarks.loggy.writer.BufferWriter
import com.clawmarks.loggy.writer.BufferWriterImpl
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.context.LoggyContextImpl
import com.clawmarks.loggy.sender.SentNamesHolder
import com.clawmarks.loggy.uploader.LoggyDefaultUploader
import com.clawmarks.loggy.uploader.LoggyUploader
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appyModule = module {
    single { provideSharedPreferences(androidContext()) }
    single<UserInteractionDispatcher> { PeriodicCheckIdleDispatcher() }

}

//    single { LogcatBuffer(get(),getProperty("logcatBufferWriter"))    }
//    factory(named("analyticsBufferWriter")) { BufferWriterImpl(get(),ReportType.ANALYTIC) }
//    factory(named("logcatBufferWriter"))  { provideBufferWriterLogcat(get()) }

//    single(named("")) { BufferWriterImpl(get(),get()) }
//    single { AnalyticsBuffer(get(),get()) }
//}

//private fun provideLoggyFileListState(loggyFileList: LoggyFileList): LoggyFileListState {
//    return loggyFileList.state
//}
//private fun provideBufferWriterLogcat(loggyContext: LoggyContext): BufferWriter {
//    return BufferWriterImpl(loggyContext,ReportType.REGULAR)
//}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}





