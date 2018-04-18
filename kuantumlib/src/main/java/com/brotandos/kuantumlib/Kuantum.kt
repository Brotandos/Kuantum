package com.brotandos.kuantumlib

import android.view.View

/**
 * @author: Brotandos
 * @creation_date: 31.03.2018
 */
abstract class Kuantum<T: Any, V: View> {
    abstract var value: T
    protected val viewList: MutableList<V> = mutableListOf()
    protected val reactions: MutableList<(T) -> Unit> = mutableListOf()

    val firstView: V get() = viewList[0]
    val viewsSize: Int get() = viewList.size

    open fun resetViews() = viewList.clear()
    open fun resetReactions() = reactions.clear()
    open fun reset() {
        viewList.clear()
        reactions.clear()
    }

    open fun add(view: V) {
        viewList += view
    }

    open fun add(reaction: (T) -> Unit) {
        reactions += reaction
    }

    open infix fun separate(view: V) {
        viewList.remove(view)
    }

    open infix fun separate(reaction: (T) -> Unit) {
        reactions -= reaction
    }

    inline operator fun <K: Kuantum<*, *>> K.times(block: K.() -> Unit): K {
        block()
        return this
    }
}

infix fun <VIEW: View, K: Kuantum<*, VIEW>> VIEW.of(q: K) {
    q.add(this)
}

infix fun <T, K: Kuantum<T, *>> K.reacts(reaction: (T) -> Unit): K {
    add(reaction)
    return this
}

operator fun <T, K: Kuantum<T, *>> K.invoke(reaction: (T) -> Unit): K {
    add(reaction)
    return this
}
