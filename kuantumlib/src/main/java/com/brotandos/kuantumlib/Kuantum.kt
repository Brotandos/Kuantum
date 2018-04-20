package com.brotandos.kuantumlib

import android.view.View

/**
 * @author: Brotandos
 * @creation_date: 31.03.2018
 */
abstract class Kuantum<T: Any, V: View> {
    abstract var value: T
    protected val viewList: MutableList<V> = mutableListOf()
    protected var handleReaction: (T) -> Unit = {}
    protected var handleReflectiveReaction: V.(T) -> Unit = {}

    val firstView: V get() = viewList[0]
    val viewsSize: Int get() = viewList.size

    open fun resetViews() = viewList.clear()
    open fun clearReaction() { handleReaction = {} }
    open fun clearReflective() { handleReflectiveReaction = {} }
    open fun reset() {
        viewList.clear()
        clearReaction()
        clearReflective()
    }

    open fun add(view: V) {
        viewList += view
    }

    open infix fun becomes(value: T) {
        this.value = value
    }

    open fun set(reaction: (T) -> Unit) {
        this.handleReaction = reaction
    }

    open fun setReflective(reflectiveReaction: V.(T) -> Unit) {
        this.handleReflectiveReaction = reflectiveReaction
    }

    open infix fun separate(view: V) {
        viewList.remove(view)
    }

    inline operator fun <K: Kuantum<*, *>> K.times(block: K.() -> Unit): K {
        block()
        return this
    }
}

infix fun <V: View, K: Kuantum<*, V>> V.of(q: K): V {
    q.add(this)
    return this
}

operator fun <T, K: Kuantum<T, *>> K.invoke(reaction: (T) -> Unit): K {
    set(reaction)
    return this
}

operator fun <T, V: View, K: Kuantum<T, V>> K.invoke(isReflective: Boolean, reflectiveReaction: V.(T) -> Unit): K {
    setReflective(reflectiveReaction)
    return this
}
