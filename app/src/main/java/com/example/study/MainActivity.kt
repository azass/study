package com.example.study

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

class MainActivity : AppCompatActivity(),
    NormalMenuFragment.OnBlockMenuSelectListener,
    BlockMenuFragment.OnBlockSheetSelectListener,
    OnBlockItemSelectListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, MenuFragment.newInstance(), "normalMenuList")
                .commit()
        }
    }

    override fun onBlockMenuSelected(blockMenu: BlockMenu) {
        val blockMenuFragment = BlockMenuFragment.newInstance(blockMenu.blocksheetList)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, blockMenuFragment, "blockMenu")
            .addToBackStack(null)
            .commit()
    }

    override fun onBlockSheetSelected(blockSheet: BlockSheet) {
        val blockSheetFragment = BlockSheetFragment.newInstance(blockSheet)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, blockSheetFragment, "blockSheet")
            .addToBackStack(null)
            .commit()
    }

    override fun onBlockItemSelected(blockItem: BlockItem) {
        val blockItemFragment = BlockItemFragment.newInstance(blockItem)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, blockItemFragment, "blockItem")
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> false
    }
}
