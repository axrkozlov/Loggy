package com.clawsmark.logtracker.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.clawsmark.logtracker.data.ReportType
import com.clawsmark.logtracker.data.buffer.AnalyticsBuffer
import com.clawsmark.logtracker.data.buffer.LogcatBuffer
import com.clawsmark.logtracker.data.services.prefs.LoggyPrefs
import com.clawsmark.logtracker.data.services.prefs.LoggyPrefsImpl
import com.clawsmark.logtracker.data.writer.BufferWriter
import com.clawsmark.logtracker.data.writer.BufferWriterImpl
import com.clawsmark.logtracker.loggy.LoggyContext
import com.clawsmark.logtracker.loggy.LoggyContextImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { provideSharedPreferences(androidContext()) }
    single<LoggyPrefs> { LoggyPrefsImpl(get()) }
    single<LoggyContext> { LoggyContextImpl(get() ) }
    single<BufferWriter>(named("analyticsBufferWriter")) { BufferWriterImpl(get(),ReportType.ANALYTIC) }
    single { AnalyticsBuffer(get(),get(named("analyticsBufferWriter")))    }
    single<BufferWriter>(named("logcatBufferWriter")) { BufferWriterImpl(get(),ReportType.REGULAR) }
    single { LogcatBuffer(get(),get(named("logcatBufferWriter")))    }
//    single { LogcatBuffer(get(),getProperty("logcatBufferWriter"))    }
//    factory(named("analyticsBufferWriter")) { BufferWriterImpl(get(),ReportType.ANALYTIC) }
//    factory(named("logcatBufferWriter"))  { provideBufferWriterLogcat(get()) }

//    single(named("")) { BufferWriterImpl(get(),get()) }
//    single { AnalyticsBuffer(get(),get()) }
}

//private fun provideBufferWriterAnalytics(loggyContext: LoggyContext): BufferWriter {
//    return BufferWriterImpl(loggyContext,ReportType.ANALYTIC)
//}
//private fun provideBufferWriterLogcat(loggyContext: LoggyContext): BufferWriter {
//    return BufferWriterImpl(loggyContext,ReportType.REGULAR)
//}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}





