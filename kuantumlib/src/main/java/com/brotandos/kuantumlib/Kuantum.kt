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

    open fun resetViews() = viewList.clear()
    open fun resetReactions() = reactions.clear()
    open fun reset() {
        viewList.clear()
        reactions.clear()
    }

    open fun addView(view: V) {
        viewList += view
    }

    open operator fun plus(view: V): Kuantum<T, V> {
        viewList += view
        return this
    }

    open operator fun plusAssign(reaction: (T) -> Unit) { reactions += reaction }

    open infix fun set(value: T) {
        this.value = value
    }

    open infix fun seperate(view: V) {
        viewList.remove(view)
    }

    open operator fun minus(view: V) {
        viewList.remove(view)
    }
}

infix fun <VIEW: View> VIEW.of(q: Kuantum<*, VIEW>) {
    q.addView(this)
}