package com.brotandos.kuantumlib

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.brotandos.koatlib.KoatlContext
import com.brotandos.koatlib.KoatlViewHolder
import com.brotandos.koatlib.row

open class ListKuantum<E>(list: MutableList<E> = mutableListOf()) {
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
                itemViewMap[value[holder.adapterPosition]] = holder.itemView
                holder.bind(value[holder.adapterPosition], holder.adapterPosition)
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
            handleSetLayoutParams: ViewGroup.LayoutParams.() -> Unit = row,
            holderView: KoatlContext<ViewGroup>.(E, Int) -> Unit
    ): ListKuantum<E> {
        this.handleSetOriginalLayoutParams = handleSetLayoutParams
        adapter = object : RecyclerView.Adapter<KoatlViewHolder<E>>() {
            override fun getItemCount() = size

            override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int)
            = KoatlViewHolder(FrameLayout(parent.context), parent, holderView, handleSetLayoutParams)

            override fun onBindViewHolder(holder: KoatlViewHolder<E>, position: Int) {
                itemViewMap[value[holder.adapterPosition]] = holder.itemView
                holder.bind(value[holder.adapterPosition], holder.adapterPosition)
            }
        }
        return this@ListKuantum
    }

    operator fun get(i: Int): E = value[i]

    fun removeFirstWhere(condition: (E) -> Boolean) {
        val element = find(condition)
        if (element != null) remove(element)
    }

    open infix fun becomes(list: List<E>) {
        value = list.toMutableList()
    }

    fun filterView(predicate: (E) -> Boolean) {
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