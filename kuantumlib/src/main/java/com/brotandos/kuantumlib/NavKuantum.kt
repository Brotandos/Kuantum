package com.brotandos.kuantumlib

import android.support.design.internal.BottomNavigationPresenter
import android.support.design.widget.BottomNavigationView
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPresenter
import android.view.Menu
import android.view.MenuItem

class NavKuantum <T: BottomNavigationView> (
        private val navItems: List<NavItem>,
        initialPosition: Int = 0
) : Kuantum<Int, T>() {

    constructor(vararg navItems: NavItem, initialPosition: Int = 0) : this(navItems.toList(), initialPosition)

    init {
        navItems.forEachIndexed { index, navItem ->
            navItem.order = index
        }
    }

    private fun T.setupMenu() {
        // setting up menu
        val mMenuField = BottomNavigationView::class.java.getDeclaredField("mMenu")
        mMenuField.isAccessible = true
        val mMenu = mMenuField.get(this)
        // (mMenu as Menu).
        navItems.forEach { navItem ->
            /**
             * add(it.title)
             * */
            Menu::class.java.getDeclaredMethod(
                    "add",
                    Int::class.java,
                    Int::class.java,
                    Int::class.java,
                    CharSequence::class.java
            ).invoke(mMenu, Menu.NONE, navItem.id, navItem.order, navItem.title)

            if (navItem.iconResId > 0) {
                (mMenu as Menu).getItem(navItem.order).setIcon(navItem.iconResId)
            }
        }
        mMenuField.isAccessible = false

        // setting menu for each BottomNavigationView
        val mPresenterField = BottomNavigationView::class.java.getDeclaredField("mPresenter")
        mPresenterField.isAccessible = true
        (mPresenterField.get(this) as BottomNavigationPresenter).apply {
            /**
             * mPresenter.setUpdateSuspended(true)
             * */
            BottomNavigationPresenter::class.java.getDeclaredMethod("setUpdateSuspended", Boolean::class.java)
                    .invoke(this, true)

            /**
             * mPresenter.setUpdateSuspended(false)
             * */
            BottomNavigationPresenter::class.java.getDeclaredMethod("setUpdateSuspended", Boolean::class.java)
                    .invoke(this, false)

            /**
             * mPresenter.updateMenuView(true)
             * */
            BottomNavigationPresenter::class.java.getDeclaredMethod("updateMenuView", Boolean::class.java)
                    .invoke(this, true)
        }
        mPresenterField.isAccessible = false
    }

    override var value: Int = navItems[initialPosition].id
        set(value) {
            field = value
            val navItem = navItems.find { it.id == value }!!
            viewList.forEach {
                if (it.selectedItemId != value) {

                    val mMenuField = BottomNavigationView::class.java.getDeclaredField("mMenu")
                    mMenuField.isAccessible = true
                    val mMenu = mMenuField.get(it) as Menu
                    val menuItem = mMenu.findItem(value)

                    if (menuItem != null) {
                        val mPresenterField = BottomNavigationView::class.java.getDeclaredField("mPresenter")
                        mPresenterField.isAccessible = true

                        val performItemAction = MenuBuilder::class.java.getDeclaredMethod(
                                "performItemAction",
                                MenuItem::class.java,
                                MenuPresenter::class.java,
                                Int::class.java
                        ).invoke(mMenu, menuItem, mPresenterField.get(it), 0) as Boolean

                        if (performItemAction) {
                            menuItem.isChecked = true
                        }

                        mMenuField.isAccessible = false
                        mPresenterField.isAccessible = false
                    }

                    it.selectedItemId = value
                }
            }
            navItem.hReaction(navItem)
        }

    override fun add(view: T) {
        super.add(view)
        view.setupMenu()
        view.setOnNavigationItemSelectedListener {
            val navItem = navItems[it.order]
            if (navItem.id == value && !navItem.isReselectable)
                return@setOnNavigationItemSelectedListener false
            value = it.itemId
            true
        }
    }

    fun select(navItem: NavItem) {
        value = navItem.id
    }

    fun select(navItemId: Int) {
        value = navItemId
    }

    class NavItem (val title: String,
                   val hReaction: (NavItem) -> Unit,
                   val id: Int,
                   val iconResId: Int = 0,
                   val isReselectable: Boolean = false) {
        var order: Int = 0
    }
}


class NavKuantumBuilder<T: BottomNavigationView> {
    var selected: NavKuantum.NavItem? = null

    val navItems = mutableListOf<NavKuantum.NavItem>()

    fun navItem(title: String,
                id: Int,
                iconResId: Int = 0,
                isReselectable: Boolean = false,
                onSelect: (NavKuantum.NavItem) -> Unit = {}): NavKuantum.NavItem {
        val navItem = NavKuantum.NavItem(title, onSelect, id, iconResId, isReselectable)
        navItems += navItem
        return navItem
    }

    operator fun NavKuantum.NavItem.not() = {
        selected = this
    }

    fun build(): NavKuantum<T> {
        val qNav = NavKuantum<T>(navItems)
        if (selected != null)
            qNav.select(selected!!)
        return qNav
    }
}

fun <T: BottomNavigationView> navKuantum(init: NavKuantumBuilder<T>.() -> Unit): NavKuantum<T> {
    val navKuantumBuilder = NavKuantumBuilder<T>()
    navKuantumBuilder.init()
    return navKuantumBuilder.build()
}