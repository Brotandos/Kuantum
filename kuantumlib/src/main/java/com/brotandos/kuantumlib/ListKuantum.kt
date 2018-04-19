package com.brotandos.kuantumlib

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.FrameLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoContextImpl
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.wrapContent

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
            holderView: AnkoContext<ViewGroup>.(E, Int) -> Unit
    ): this(list) {
        adapter = object : RecyclerView.Adapter<KoatlViewHolder<E>>() {
            override fun onBindViewHolder(holder: KoatlViewHolder<E>, position: Int) {
                holder.bind(value[holder.adapterPosition], holder.adapterPosition)
            }
            override fun getItemCount() = size
            override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): KoatlViewHolder<E> {
                return KoatlViewHolder(FrameLayout(parent.context), parent, holderView)
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

    fun vForEach(holderView: AnkoContext<ViewGroup>.(E, Int) -> Unit): ListKuantum<E> {
        adapter = object : RecyclerView.Adapter<KoatlViewHolder<E>>() {
            override fun onBindViewHolder(holder: KoatlViewHolder<E>, position: Int) {
                holder.bind(value[holder.adapterPosition], holder.adapterPosition)
            }
            override fun getItemCount() = size
            override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): KoatlViewHolder<E> {
                return KoatlViewHolder(FrameLayout(parent.context), parent, holderView)
            }
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


    class KoatlViewHolder<in E> (
            private val vItem: FrameLayout,
            private val parent: ViewGroup,
            private val holderView: AnkoContext<ViewGroup>.(E, Int) -> Unit
    ): RecyclerView.ViewHolder(vItem) {
        fun bind(item: E, position: Int) {
            vItem.layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
            vItem.addView( AnkoContextImpl(parent.context, parent, false)
                    .apply { holderView(item, position) }.view)
        }
    }
}


infix fun <E> RecyclerView.of(q: ListKuantum<E>) { this.adapter = q.adapter }