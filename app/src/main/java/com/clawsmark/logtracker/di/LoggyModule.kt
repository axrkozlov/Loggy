package com.clawsmark.logtracker.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.clawsmark.logtracker.data.report.ReportType
import com.clawsmark.logtracker.data.buffer.AnalyticsBuffer
import com.clawsmark.logtracker.data.buffer.LogcatBuffer
import com.clawsmark.logtracker.data.filelist.LoggyFileList
import com.clawsmark.logtracker.data.filelist.LoggyFileListState
import com.clawsmark.logtracker.data.sender.LoggySender
import com.clawsmark.logtracker.data.prefs.LoggyPrefs
import com.clawsmark.logtracker.data.prefs.LoggyPrefsImpl
import com.clawsmark.logtracker.data.userinteraction.PeriodicCheckIdleDispatcher
import com.clawsmark.logtracker.data.userinteraction.UserInteractionDispatcher
import com.clawsmark.logtracker.data.writer.BufferWriter
import com.clawsmark.logtracker.data.writer.BufferWriterImpl
import com.clawsmark.logtracker.data.context.LoggyContext
import com.clawsmark.logtracker.data.context.LoggyContextImpl
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
    single { LoggySender(get(), get(named("analyticsFileList")), get(named("logcatFileList"))) }

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





