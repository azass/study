package com.example.study.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.study.*
import com.example.study.app.abs.*
import com.example.study.app.eba.EbaFragment
import com.example.study.app.eba.EbaMemoTitlesFragment
import com.example.study.app.eba.EbaWebFragment
import com.example.study.app.eba.EbaWritingDrillListFragment
import com.example.study.model.Quotation
import com.example.study.model.abs.AbstractionBlock
import com.example.study.model.abs.BlockItem
import com.example.study.model.abs.BlockSheet
import com.example.study.model.abs.BlockSheetManager
import com.example.study.persistence.BlockStudyDatabase
import com.example.study.persistence.QuotationsDao
import com.example.study.persistence.abs.*
import com.example.study.persistence.eba.EbaDailyWebLogsDao
import com.example.study.persistence.eba.EbaTotalWebLogsDao
import com.example.study.persistence.eba.EbaWebLogsDao
import com.example.study.persistence.eba.EbaWebMemoDao
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(),
    AbsGridFragment.OnAbstractionBlockSelectListener,
    AbsIndexFragment.OnBlockSheetSelectListener,
    OnBlockItemSelectListener, OnLoadFragmentListener,
    NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var blockSheetManager: BlockSheetManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (hasPermission()) {
            setContentView(R.layout.activity_main)
            loadActionBarToggle()
            // Navigation Draw View 準備
            nav_view.setNavigationItemSelectedListener(this)
            FirebaseApp.initializeApp(this)

            blockSheetManager = BlockSheetManager()
            blockSheetManager.setup(this)

            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.content_main, ResultFragment(), "normalMenuList")
                    .commit()
            }
        }
    }

    private fun hasPermission() : Boolean{
        // パーミッションを持っているか確認
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // 持っていないならパーミッションを要求
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            return false
        }

        return true
    }

    override fun loadActionBarToggle() {
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_option, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun loadAbstractionBlockMenu() {
        blockSheetManager.changeStatus(ABSTRACTION_MENU_STATUS)

        replaceAbsScreen(AbsGridFragment.newInstance(blockSheetManager), "normalMenuList")
    }

    override fun loadForgettingFragment() {
        blockSheetManager.changeStatus(FORGETTING_LIST_STATUS)

        replaceAbsScreen(AbsForgettingsFragment.newInstance(blockSheetManager), "forgettings")
    }

    override fun loadWastingFragment() {
        blockSheetManager.changeStatus(WASTING_LIST_STATUS)

        replaceAbsScreen(AbsWastingsFragment.newInstance(blockSheetManager), "wastings")
    }

    override fun loadBlockItemListFragment() {
        when (blockSheetManager.previouStatus) {
            BLOCK_SHEET_STATUS -> onBlockSheetSelected(blockSheetManager.selectedBlockSheet!!)
            FORGETTING_LIST_STATUS -> loadForgettingFragment()
            WASTING_LIST_STATUS -> loadWastingFragment()
        }
    }

    override fun onAbstractionBlockSelected(abstractionBlock: AbstractionBlock) {
        blockSheetManager.changeStatus(ABSTRACTION_BLOCK_STATUS)
        blockSheetManager.selectedAbstractionBlock = abstractionBlock
        val abstractionBlockFragment = AbsIndexFragment.newInstance(abstractionBlock)

        replaceAbsScreen(abstractionBlockFragment, "selectedAbstractionBlock")
    }

    override fun onBlockSheetSelected(blockSheet: BlockSheet) {
        blockSheetManager.changeStatus(BLOCK_SHEET_STATUS)
        blockSheetManager.selectedBlockSheet = blockSheet
        val blockSheetFragment = BlockSheetFragment.newInstance(blockSheetManager)

        replaceAbsScreen(blockSheetFragment, "selectedBlockSheet")
    }

    override fun onBlockItemSelected(index: Int, blockItem: BlockItem) {
        blockSheetManager.changeStatus(BLOCK_ITEM_STATUS)
        blockSheetManager.selectedBlockItemListIndex = index
        val blockItemFragment = BlockSheetItemFragment.newInstance(blockSheetManager)

        replaceAbsScreen(blockItemFragment, "selectedBlockItem")
    }

    override fun loadMenuFragment() {
        blockSheetManager.init()

        replaceAbsScreen(AbsMenuFragment.newInstance(blockSheetManager), "normalMenuList")
    }

    //
    fun replaceAbsScreen(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, fragment, tag)
            .commit()
    }

    override fun loadBack() {
        val status = blockSheetManager.status
        blockSheetManager.status = 0
        when (status) {
            ABSTRACTION_MENU_STATUS -> loadMenuFragment()
            ABSTRACTION_BLOCK_STATUS -> loadAbstractionBlockMenu()
            BLOCK_SHEET_STATUS -> onAbstractionBlockSelected(blockSheetManager.selectedAbstractionBlock!!)
            BLOCK_ITEM_STATUS -> loadBlockItemListFragment()
            FORGETTING_LIST_STATUS -> loadMenuFragment()
            WASTING_LIST_STATUS -> loadMenuFragment()
        }
    }

    override fun loadQuotationEditor(quotation: Quotation) {
        replaceMainScreen(QuotationEditorFragment.newInstance(quotation), "quotation")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle bottom_nav_abs view item clicks here.
        when (item.itemId) {
            R.id.nav_abstraction_block_sheet -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, AbsFragment(), "eba")
                    .commit()
                loadMenuFragment()
            }
            R.id.nav_eba -> {
//                val builder = CustomTabsIntent.Builder()
//                val chromeIntent = builder.build()
//                chromeIntent.launchUrl(this, Uri.parse("https://eba.learning-ware.jp"))
//                startActivity(chromeIntent.intent)
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, EbaFragment(), "eba")
                    .commit()
                replaceEbaScreen(EbaWebFragment.newInstance(), "ebaWeb")
            }
            R.id.nav_result -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, ResultFragment(), "result")
                    .commit()
            }
            R.id.nav_tbc -> {

            }
            R.id.nav_quotation -> {
                replaceMainScreen(QuotationsFragment(), "quotation")
            }
            R.id.nav_send -> {

            }
            R.id.navigation_home -> loadMenuFragment()
            R.id.navi_web -> replaceEbaScreen(EbaWebFragment.newInstance(), "navi_web")
            R.id.navi_memo -> replaceEbaScreen(EbaMemoTitlesFragment(), "navi_memo")
            R.id.navi_100 -> replaceEbaScreen(EbaWritingDrillListFragment(), "navi_100")
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun replaceMainScreen(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content_main, fragment, tag)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            loadBack()
            true
        }
        R.id.exportQ -> {
            val db = BlockStudyDatabase(this).writableDatabase
            val json = blockSheetManager.exportJson(db)
            outputFile("abstr_blck_sheet", ".json", json)
            db.close()
            Toast.makeText(this, "完了しました", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.exportLog -> {
            val db = BlockStudyDatabase(this).writableDatabase

            val absAnswerLogDao = AbsAnswerLogDao(db)
            absAnswerLogDao.export()
            val absCardAnswerLogDao = AbsCardAnswerLogDao(db)
            absCardAnswerLogDao.export()
            val absDailyResultsDao = AbsDailyResultsDao(db)
            absDailyResultsDao.export()
            val absTotalResultsDao = AbsTotalResultsDao(db)
            absTotalResultsDao.export()
            val recentResultsDao = AbsRecentResultsDao(db)
            recentResultsDao.export()

            val ebaDailyWebLogsDao = EbaDailyWebLogsDao(db)
            ebaDailyWebLogsDao.export()
            val ebaTotalWebLogsDao = EbaTotalWebLogsDao(db)
            ebaTotalWebLogsDao.export()
            val ebaWebLogsDao = EbaWebLogsDao(db)
            ebaWebLogsDao.export()
            val WebMemoDao = EbaWebMemoDao(db)
            WebMemoDao.export()

            val quotationsDao = QuotationsDao(db)
            quotationsDao.export()

            db.close()
            Toast.makeText(this, "完了しました", Toast.LENGTH_SHORT).show()
            true
        }
        R.id.importLog -> {
            val db = BlockStudyDatabase(this).writableDatabase
            val absDailyResultsDao = AbsDailyResultsDao(db)
            absDailyResultsDao.import()
            val absAnswerLogDao = AbsAnswerLogDao(db)
            absAnswerLogDao.import()
            val absCardAnswerLogDao = AbsCardAnswerLogDao(db)
            absCardAnswerLogDao.import()
            val absRecentResultsDao = AbsRecentResultsDao(db)
            absRecentResultsDao.import()
            val absTotalResultsDao = AbsTotalResultsDao(db)
            absTotalResultsDao.import()
            val ebaDailyWebLogsDao = EbaDailyWebLogsDao(db)
            ebaDailyWebLogsDao.import()
            val ebaTotalWebLogsDao = EbaTotalWebLogsDao(db)
            ebaTotalWebLogsDao.import()
            val ebaWebLogsDao = EbaWebLogsDao(db)
            ebaWebLogsDao.import()
            val ebaWebMemoDao = EbaWebMemoDao(db)
            ebaWebMemoDao.import()

//            db.close()
            Toast.makeText(this, "完了しました", Toast.LENGTH_SHORT).show()
            true
        }
        else -> false
    }

    //
    override fun replaceEbaScreen(fragment: Fragment, tag: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.eba_layout, fragment, tag)
            .commit()
    }

}
