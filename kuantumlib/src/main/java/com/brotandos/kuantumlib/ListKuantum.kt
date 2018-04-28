package com.brotandos.kuantumlib

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.brotandos.koatlib.KoatlContext
import com.brotandos.koatlib.KoatlViewHolder
import com.brotandos.koatlib.mw

class ListKuantum<E>(list: MutableList<E> = mutableListOf()) {
    lateinit var adapter: RecyclerView.Adapter<KoatlViewHolder<E>>
    var value: MutableList<E> = list
        set(value) {
            if (::adapter.isInitialized) {
                adapter.notifyItemRangeRemoved(0, field.size)
                field = value
                adapter.notifyItemRangeInserted(0, value.size)
            } else {
                field = value
                adapter.notifyItemRangeInserted(0, value.size)
            }
        }

    constructor (
            list: MutableList<E> = mutableListOf(),
            handleLayoutParams: View.() -> Unit = mw,
            holderView: KoatlContext<ViewGroup>.(E, Int) -> Unit
    ): this(list) {
        adapter = object : RecyclerView.Adapter<KoatlViewHolder<E>>() {
            override fun onBindViewHolder(holder: KoatlViewHolder<E>, position: Int) {
                holder.bind(value[holder.adapterPosition], holder.adapterPosition)
            }
            override fun getItemCount() = size
            override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): KoatlViewHolder<E> {
                return KoatlViewHolder(FrameLayout(parent.context), parent, holderView, handleLayoutParams)
            }
        }
    }

    fun forEach (action: (E) -> Unit) {
        for (element in value) action(element)
    }

    fun forEachIndexed (action: (E, Int) -> Unit) {
        value.forEachIndexed { index, e -> action(e, index) }
    }

    fun find(condition: (E) -> Boolean) = value.find(condition)

    fun vForEach (
            handleLayoutParams: View.() -> Unit = mw,
            holderView: KoatlContext<ViewGroup>.(E, Int) -> Unit
    ): ListKuantum<E> {
        adapter = object : RecyclerView.Adapter<KoatlViewHolder<E>>() {
            override fun getItemCount() = size

            override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int)
            = KoatlViewHolder(FrameLayout(parent.context), parent, holderView, handleLayoutParams)

            override fun onBindViewHolder(holder: KoatlViewHolder<E>, position: Int)
            = holder.bind(value[holder.adapterPosition], holder.adapterPosition)
        }
        return this@ListKuantum
    }

    operator fun get(i: Int): E = value[i]

    fun removeFirstWhere(condition: (E) -> Boolean) {
        val element = find(condition)
        if (element != null) remove(element)
    }


    /**
     * Below MutableList's functions' implementations
     * */
    val size: Int get() = value.size
    val lastIndex: Int get() = value.lastIndex
    fun contains(element: E) = value.contains(element)
    fun containsAll(elements: Collection<E>) = value.containsAll(elements)
    fun indexOf(element: E) = value.indexOf(element)
    fun isEmpty() = value.isEmpty()
    fun iterator(): MutableIterator<E> = value.iterator()
    fun lastIndexOf(element: E) = value.lastIndexOf(element)
    fun listIterator() = value.listIterator()
    fun listIterator(index: Int) = value.listIterator(index)
    fun set(index: Int, element: E) = value.set(index, element)
    fun subList(fromIndex: Int, toIndex: Int) = value.subList(fromIndex, toIndex)

    fun add(index: Int, element: E) {
        value.add(index, element)
        adapter.notifyItemInserted(index)
    }

    fun add(element: E) {
        value.add(element)
        val i = value.lastIndex
        adapter.notifyItemInserted(i)
    }

    fun clear() {
        val size = value.size
        value.clear()
        adapter.notifyItemRangeRemoved(0, size)
    }

    fun remove(element: E) {
        val i = value.indexOf(element)
        removeAt(i)
    }

    fun removeAt(index: Int) {
        value.removeAt(index)
        adapter.notifyItemRemoved(index)
    }

    fun addAll(index: Int, elements: Collection<E>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun addAll(elements: Collection<E>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun removeAll(elements: Collection<E>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun retainAll(elements: Collection<E>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


infix fun <E> RecyclerView.of(q: ListKuantum<E>) : RecyclerView {
    this.adapter = q.adapter
    return this
}