package com.fortie40.words_filtering_2

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var mainAdapter: MainActivityAdapter
    private lateinit var searchAdapter: SearchActivityAdapter
    private lateinit var names: List<String>
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getPreferences(Context.MODE_PRIVATE)
        getNames()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        searchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        // searchView.imeOptions = EditorInfo.IME_ACTION_GO
        // searchView.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.queryHint = getString(R.string.search_name)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                no_results_found.visibility = View.GONE
                val recent = getRecentSearches()
                searchAdapter = SearchActivityAdapter(recent)
                names_item.adapter = searchAdapter
                if (recent.isEmpty()) {
                    no_results_found.visibility = View.VISIBLE
                    no_results_found.text = getString(R.string.no_recent_search)
                }
            }
        }

        searchView.setOnCloseListener {
            no_results_found.visibility = View.GONE
            names_item.adapter = mainAdapter
            false
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchName(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //searchName(newText)
                return false
            }
        })
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (!searchView.isIconified) {
            searchView.setQuery("", false)
            searchView.clearFocus()
            searchView.isIconified = true
        } else {
            super.onBackPressed()
        }
    }

    private fun getNames() {
        names = arrayListOf(
            "Fortie40", "Java", "Kotlin", "C++", "PHP", "Javascript", "Objective-C", "Swift",
            "Groovy", "Haskell", "JQuery", "KRYPTON", "LotusScript", "Mortran", "NewLISP", "Orwell",
            "Hopscotch", "JScript", "AngelScript", "Bash", "Clojure", "C", "COBOL", "CSS",
            "Cybil", "Pascal", "Perl", "Smalltalk", "SQL", "Unicon", "Ubercode", "Fortran",
            "Hollywood", "SMALL", "Lisp", "PureScript", "R++", "XQuery", "YAML", "ZOPL"
        )

        mainAdapter = MainActivityAdapter(names)
        names_item.adapter = mainAdapter
    }

    private fun searchName(p0: String?) {
        search_progress.visibility = View.VISIBLE
        saveToRecentSearch(p0!!)
        searchAdapter.originalList = names
        searchAdapter.string = p0
        searchAdapter.filter.filter(p0.toLowerCase(Locale.getDefault())) {
            when(searchAdapter.itemCount) {
                0 -> {
                    no_results_found.visibility = View.VISIBLE
                    no_results_found.text = getString(R.string.no_results_found, p0)
                }
                else -> {
                    no_results_found.visibility = View.GONE
                }
            }
            Log.i("MainActivityA", searchAdapter.string!!)
            Log.i("MainActivity", p0)
            if (searchAdapter.string == "" || searchAdapter.string == p0) {
                Log.i("MainActivity", "done")
                search_progress.visibility = View.GONE
                searchView.clearFocus()
            }
        }
    }

    private fun saveToRecentSearch(name: String) {
        var position = sharedPref.getInt(POSITION, 0)
        if (position == 0 || position == 5) {
            position = 1
        } else {
            position++
        }

        val key = QUERY + "$position"
        with(sharedPref.edit()) {
            putString(key, name)
            apply()
        }

        with(sharedPref.edit()) {
            putInt(POSITION, position)
            apply()
        }
    }

    private fun getRecentSearches(): List<String> {
        val queryList = arrayListOf<String>()
        for (i in 1..5) {
            val query = sharedPref.getString(QUERY + "$i", "")
            if (query!!.isNotEmpty()) {
                queryList.add(query)
            }
        }

        return queryList
    }
}