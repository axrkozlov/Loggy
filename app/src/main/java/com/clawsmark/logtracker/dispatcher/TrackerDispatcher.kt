package com.clawsmark.logtracker.dispatcher

import android.util.Log
import com.clawsmark.logtracker.courier.Courier
import com.clawsmark.logtracker.filemanager.FileManager
import java.io.File
import java.lang.reflect.ParameterizedType
import java.util.*
import kotlin.reflect.KProperty

object TrackerDispatcher {
//
//
//    val eldestFile: File = FileManager.getEldestFile()
//    operator fun getValue(tracker: Tracker, property: KProperty<*>): (LinkedHashSet<Message>) -> Unit = FileManager.onBufferSaveEvent
//    operator fun getValue(courier: Courier, property: KProperty<*>): (File) -> Unit = FileManager.onDeleteFileEvent
    private var subscribers = LinkedList<Subscriber<Event>>()

    fun subscribe(subscriber: Subscriber<Event>){
        subscribers.add(subscriber)
    }


    @Suppress("UNCHECKED_CAST")
    fun <T:Event> dispatch(event: T){
        for (item in subscribers) {
            if (event.javaClass.isAssignableFrom(
                            (item.javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0] as Class<T>)){
                item.onNewEvent(event)
                Log.i("TrackerDispatcher", "dispatch: ${item.javaClass.declaredMethods[0].parameterTypes[0]}")


            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    fun <T:Event> dispatch1(event: T){
        for (item in subscribers) {
            if (event.javaClass.isAssignableFrom(
                            (item.javaClass.genericInterfaces[0] as ParameterizedType).actualTypeArguments[0] as Class<T>)){
                item.onNewEvent(event)
                Log.i("TrackerDispatcher", "dispatch: ${item.javaClass.declaredMethods[0].parameterTypes[0]}")


            }
        }
    }

}

interface Subscriber<out T:Event>{
    fun onNewEvent(event: Event)
}
