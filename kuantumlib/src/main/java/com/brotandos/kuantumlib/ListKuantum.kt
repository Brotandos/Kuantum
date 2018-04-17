package com.brotandos.kuantumlib

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoContextImpl

class ListKuantum<T>(val ctx: Context, list: List<T> = listOf()) : Kuantum<List<T>, RecyclerView>() {
    override var value: List<T> = list
        set(value) {
            viewList.forEach { it.adapter.apply {
                notifyItemRangeRemoved(0, field.size)
                field = value
                notifyItemRangeInserted(0, field.size)
            }}
        }

    private lateinit var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    //lateinit var context: Context
    val size get() = value.size
    val isEmpty get() = value.isEmpty()

    override fun add(view: RecyclerView) {
        super.add(view)
        view.adapter = this.adapter
    }

    fun forEach (action: (T) -> Unit) {
        for (element in value) action(element)
    }

    fun vForEach(holderView: AnkoContext<ViewGroup>.(T, Int) -> Unit): ListKuantum<T> {
        adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}
            override fun getItemCount() = size
            override fun getItemViewType(position: Int) = position
            override fun onCreateViewHolder(parent: ViewGroup, position: Int)
                    = object : RecyclerView.ViewHolder(
                    AnkoContextImpl(ctx, parent, false)
                            .apply { holderView(value[position], position) }.view
            ) {}
        }
        return this
    }

    fun add(element: T) { value += element }

    fun remove(element: T) { value -= element }

    operator fun get(index: Int): T = value[index]
}