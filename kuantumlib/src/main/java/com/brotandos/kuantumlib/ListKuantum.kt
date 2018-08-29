package com.brotandos.kuantumlib

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.brotandos.koatlcontextlib.KoatlContext
import com.brotandos.koatlcontextlib.KoatlViewHolder
import com.brotandos.koatlcontextlib.row

open class ListKuantum<E>(list: MutableList<E> = mutableListOf()) {
    lateinit var adapter: RecyclerView.Adapter<KoatlViewHolder<E>>
    var list: MutableList<E> = list
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
    private var itemViewMap = mutableMapOf<E, View>()
    private lateinit var handleSetOriginalLayoutParams: ViewGroup.LayoutParams.() -> Unit

    constructor (
            list: MutableList<E> = mutableListOf(),
            handleSetLayoutParams: ViewGroup.LayoutParams.() -> Unit = row,
            holderView: KoatlContext<ViewGroup>.(E, Int) -> Unit
    ): this(list) {
        this.handleSetOriginalLayoutParams = handleSetLayoutParams
        adapter = object : RecyclerView.Adapter<KoatlViewHolder<E>>() {
            override fun getItemCount() = size

            override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int)
            = KoatlViewHolder(FrameLayout(parent.context), parent, holderView, handleSetLayoutParams)

            override fun onBindViewHolder(holder: KoatlViewHolder<E>, position: Int) {
                itemViewMap[list[holder.adapterPosition]] = holder.itemView
                holder.bind(list[holder.adapterPosition], holder.adapterPosition)
            }
        }
    }

    fun forEach (action: (E) -> Unit) {
        for (element in list) action(element)
    }

    fun forEachIndexed (action: (E, Int) -> Unit) {
        list.forEachIndexed { index, e -> action(e, index) }
    }

    fun find(condition: (E) -> Boolean) = list.find(condition)

    fun vForEach (
            handleSetLayoutParams: ViewGroup.LayoutParams.() -> Unit = row,
            holderView: KoatlContext<ViewGroup>.(E, Int) -> Unit
    ): ListKuantum<E> {
        this.handleSetOriginalLayoutParams = handleSetLayoutParams
        adapter = object : RecyclerView.Adapter<KoatlViewHolder<E>>() {
            override fun getItemCount() = size

            override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int)
            = KoatlViewHolder(FrameLayout(parent.context), parent, holderView, handleSetLayoutParams)

            override fun onBindViewHolder(holder: KoatlViewHolder<E>, position: Int) {
                itemViewMap[list[holder.adapterPosition]] = holder.itemView
                holder.bind(list[holder.adapterPosition], holder.adapterPosition)
            }
        }
        return this@ListKuantum
    }

    operator fun get(i: Int): E = list[i]

    fun removeFirstWhere(condition: (E) -> Boolean) {
        val element = find(condition) ?: throw RuntimeException("Element not found")
        remove(element)
    }

    open infix fun becomes(list: List<E>) {
        this.list = list.toMutableList()
    }

    fun filter(predicate: (E) -> Boolean) {
        itemViewMap.forEach { item, view ->
            if (predicate(item)) {
                view.visibility = View.VISIBLE
                view.layoutParams.handleSetOriginalLayoutParams()
            }
            else {
                view.visibility = View.GONE
                view.layoutParams.width = 0
                view.layoutParams.height = 0
            }
        }
    }

    fun clearViewFilter() {
        itemViewMap.forEach { _, view ->
            view.layoutParams.handleSetOriginalLayoutParams()
            view.visibility = View.VISIBLE
        }
    }


    /**
     * Below MutableList's functions' implementations
     * */
    val size: Int get() = list.size
    val lastIndex: Int get() = list.lastIndex
    fun contains(element: E) = list.contains(element)
    fun containsAll(elements: Collection<E>) = list.containsAll(elements)
    fun indexOf(element: E) = list.indexOf(element)
    fun isEmpty() = list.isEmpty()
    fun iterator(): MutableIterator<E> = list.iterator()
    fun lastIndexOf(element: E) = list.lastIndexOf(element)
    fun listIterator() = list.listIterator()
    fun listIterator(index: Int) = list.listIterator(index)
    fun set(index: Int, element: E) = list.set(index, element)
    fun subList(fromIndex: Int, toIndex: Int) = list.subList(fromIndex, toIndex)

    fun add(index: Int, element: E) {
        list.add(index, element)
        adapter.notifyItemInserted(index)
    }

    fun add(element: E) {
        list.add(element)
        val i = list.lastIndex
        adapter.notifyItemInserted(i)
    }

    fun clear() {
        val size = list.size
        list.clear()
        adapter.notifyItemRangeRemoved(0, size)
    }

    fun remove(element: E) {
        val i = list.indexOf(element)
        removeAt(i)
    }

    fun removeAt(index: Int) {
        list.removeAt(index)
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


infix fun <RV: RecyclerView, E> RV.of(q: ListKuantum<E>) : RV {
    this.adapter = q.adapter
    return this
}