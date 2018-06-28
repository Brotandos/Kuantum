package com.brotandos.kuantumlib

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

/**
 * @author: Brotandos
 * @creation_date: 08.04.2018
 */
class CodeKuantum<T: Any> (
        initialPosition: Int,
        private val codeTuples: List<CodeTuple<T>>
): Kuantum<Int, Spinner>() {
    override var value: Int = initialPosition // selected position
        set(value) {
            field = value
            viewList.forEach { it.setSelection(value) }
            codeTuples[value].apply { reaction(codeValue) }
        }

    private val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            value = position
        }
    }

    override fun add(view: Spinner) {
        view.apply {
            super.add(this)
            adapter = ArrayAdapter<CodeTuple<T>>(context, R.layout.support_simple_spinner_dropdown_item, codeTuples).apply {
                setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }
            setSelection(value)
            onItemSelectedListener = this@CodeKuantum.onItemSelectedListener
        }
    }

    open class CodeTuple<T> (
            val codeValue: T,
            val reaction: (T) -> Unit,
            private val label: String = codeValue.toString()
    ) {
        override fun toString() = label
    }
}