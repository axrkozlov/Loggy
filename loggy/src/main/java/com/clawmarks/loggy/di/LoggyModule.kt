package com.clawmarks.loggy.di

import com.clawmarks.loggy.report.ReportType
import com.clawmarks.loggy.buffer.AnalyticsBuffer
import com.clawmarks.loggy.buffer.LogcatBuffer
import com.clawmarks.loggy.filelist.LoggyFileList
import com.clawmarks.loggy.filelist.LoggyFileListState
import com.clawmarks.loggy.sender.LoggySender
import com.clawmarks.loggy.prefs.LoggyPrefs
import com.clawmarks.loggy.prefs.LoggyDefaultPrefs
import com.clawmarks.loggy.writer.BufferWriter
import com.clawmarks.loggy.writer.BufferWriterImpl
import com.clawmarks.loggy.context.LoggyContext
import com.clawmarks.loggy.context.LoggyContextImpl
import com.clawmarks.loggy.sender.SentNamesHolder
import com.clawmarks.loggy.uploader.LoggyDefaultUploader
import com.clawmarks.loggy.uploader.LoggyUploader
import com.clawmarks.loggy.userinteraction.*
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

val loggyModule = module {
    single<LoggyContext> { LoggyContextImpl(get()) }

    single<LoggyFileList>(named("analyticsFileList")) { LoggyFileList(get(), ReportType.ANALYTIC) }
    single<LoggyFileList>(named("logcatFileList")) { LoggyFileList(get(), ReportType.REGULAR) }
    single(named("analyticsFileListState")) { provideLoggyFileListState(get(named("analyticsFileList"))) }
    single(named("logcatFileListState")) { provideLoggyFileListState(get(named("logcatFileList"))) }

    single<BufferWriter>(named("analyticsBufferWriter")) { BufferWriterImpl(get(), ReportType.ANALYTIC, get(named("analyticsFileListState"))) }
    single { AnalyticsBuffer(get(), get(named("analyticsBufferWriter"))) }
    single<BufferWriter>(named("logcatBufferWriter")) { BufferWriterImpl(get(), ReportType.REGULAR, get(named("logcatFileListState"))) }
    single { LogcatBuffer(get(), get(named("logcatBufferWriter"))) }
    single { SentNamesHolder(get()) }
    single { LoggySender(get(),get(), get(named("analyticsFileList")), get(named("logcatFileList")),get()) }
    single<LoggyPrefs> { LoggyDefaultPrefs() }
    single<LoggyUploader> { LoggyDefaultUploader() }
    single { InteractionPermissionHandler(get()) } binds arrayOf(SendingPermissionObserver::class, UserInteractionObserver::class)
    single { provideUserInteractionDispatcher(get(), get()) }
    single { provideSendingPermissionDispatcher(get(), get()) }
}

private fun provideLoggyFileListState(loggyFileList: LoggyFileList): LoggyFileListState {
    return loggyFileList.state
}

private fun provideUserInteractionDispatcher(userInteractionObserver: UserInteractionObserver,context:LoggyContext): UserInteractionDispatcher {
    val dispatcher = PeriodicCheckIdleDispatcher(context)
    dispatcher.addObserver(userInteractionObserver)
    return dispatcher
}

private fun provideSendingPermissionDispatcher(sendingPermissionObserver: SendingPermissionObserver,context:LoggyContext): SendingPermissionDispatcher {
    val dispatcher = SendingPermissionDispatcherImpl(context)
    dispatcher.addObserver(sendingPermissionObserver)
    return dispatcher
}









