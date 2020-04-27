package com.clawmarks.logtracker.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.clawmarks.logtracker.data.report.ReportType
import com.clawmarks.logtracker.data.buffer.AnalyticsBuffer
import com.clawmarks.logtracker.data.buffer.LogcatBuffer
import com.clawmarks.logtracker.data.filelist.LoggyFileList
import com.clawmarks.logtracker.data.filelist.LoggyFileListState
import com.clawmarks.logtracker.data.sender.LoggySender
import com.clawmarks.logtracker.data.prefs.LoggyPrefs
import com.clawmarks.logtracker.data.prefs.LoggyPrefsImpl
import com.clawmarks.logtracker.data.userinteraction.PeriodicCheckIdleDispatcher
import com.clawmarks.logtracker.data.userinteraction.UserInteractionDispatcher
import com.clawmarks.logtracker.data.writer.BufferWriter
import com.clawmarks.logtracker.data.writer.BufferWriterImpl
import com.clawmarks.logtracker.data.context.LoggyContext
import com.clawmarks.logtracker.data.context.LoggyContextImpl
import com.clawmarks.logtracker.data.sender.LoggyUploader
import com.clawmarks.logtracker.data.sender.SentNamesHolder
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single { provideSharedPreferences(androidContext()) }
    single<LoggyPrefs> { LoggyPrefsImpl(get()) }
    single<LoggyContext> { LoggyContextImpl(get()) }

    single<LoggyFileList>(named("analyticsFileList")) { LoggyFileList(get(), ReportType.ANALYTIC) }
    single<LoggyFileList>(named("logcatFileList")) { LoggyFileList(get(), ReportType.REGULAR) }
    single(named("analyticsFileListState")) { provideLoggyFileListState(get(named("analyticsFileList"))) }
    single(named("logcatFileListState")) { provideLoggyFileListState(get(named("logcatFileList"))) }

    single<BufferWriter>(named("analyticsBufferWriter")) { BufferWriterImpl(get(), ReportType.ANALYTIC, get(named("analyticsFileListState"))) }
    single { AnalyticsBuffer(get(), get(named("analyticsBufferWriter"))) }
    single<BufferWriter>(named("logcatBufferWriter")) { BufferWriterImpl(get(), ReportType.REGULAR, get(named("logcatFileListState"))) }
    single { LogcatBuffer(get(), get(named("logcatBufferWriter"))) }
    single { LoggyUploader() }
    single { SentNamesHolder(get()) }
    single { LoggySender(get(),get(),get(), get(named("analyticsFileList")), get(named("logcatFileList"))) }

    single<UserInteractionDispatcher> { PeriodicCheckIdleDispatcher() }

//    single { LogcatBuffer(get(),getProperty("logcatBufferWriter"))    }
//    factory(named("analyticsBufferWriter")) { BufferWriterImpl(get(),ReportType.ANALYTIC) }
//    factory(named("logcatBufferWriter"))  { provideBufferWriterLogcat(get()) }

//    single(named("")) { BufferWriterImpl(get(),get()) }
//    single { AnalyticsBuffer(get(),get()) }
}

private fun provideLoggyFileListState(loggyFileList: LoggyFileList): LoggyFileListState {
    return loggyFileList.state
}
//private fun provideBufferWriterLogcat(loggyContext: LoggyContext): BufferWriter {
//    return BufferWriterImpl(loggyContext,ReportType.REGULAR)
//}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}





