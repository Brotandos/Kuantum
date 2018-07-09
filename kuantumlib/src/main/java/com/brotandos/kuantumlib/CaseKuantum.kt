package com.brotandos.kuantumlib

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

/**
 * @author: Brotandos
 * @creation_date: 08.04.2018
 */
class CaseKuantum<T: Any> (
        private val cases: List<Case<T>>,
        initialPosition: Int = 0
): Kuantum<Int, Spinner>() {

    constructor(vararg cases: Case<T>, initialPosition: Int = 0) : this(cases.toList(), initialPosition)

    override var value: Int = initialPosition // selected position
        set(value) {
            field = value
            viewList.forEach { it.setSelection(value) }
            cases[value].apply { reaction(caseValue) }
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
            adapter = ArrayAdapter<Case<T>>(context, R.layout.support_simple_spinner_dropdown_item, cases).apply {
                setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
            }
            setSelection(value)
            onItemSelectedListener = this@CaseKuantum.onItemSelectedListener
        }
    }

    open class Case<T> (
            val caseValue: T,
            val reaction: (T) -> Unit,
            private val label: String = caseValue.toString()
    ) {
        override fun toString() = label
    }
}