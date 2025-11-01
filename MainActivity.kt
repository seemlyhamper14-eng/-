package com.example.arabicendings

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var btn: Button
    private lateinit var rv: RecyclerView
    private lateinit var adapter: ResultsAdapter
    private lateinit var dbHelper: WordsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        input = findViewById(R.id.inputEnd)
        btn = findViewById(R.id.btnSearch)
        rv = findViewById(R.id.rvResults)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = ResultsAdapter()
        rv.adapter = adapter

        dbHelper = WordsRepository(this)

        btn.setOnClickListener {
            performSearch()
        }

        input.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else false
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Do live search for 1+ chars
                if (s != null && s.length >= 1) {
                    performSearch()
                } else {
                    adapter.update(listOf())
                }
            }
        })
    }

    private fun performSearch() {
        val end = input.text.toString().trim()
        if (end.isEmpty()) {
            Toast.makeText(this, "اكتب آخر حرفين أو أكثر", Toast.LENGTH_SHORT).show()
            return
        }
        val rev = reverseString(end)
        CoroutineScope(Dispatchers.IO).launch {
            val results = dbHelper.searchByReversedPrefix(rev, limit = 500)
            withContext(Dispatchers.Main) {
                adapter.update(results)
            }
        }
    }

    private fun reverseString(s: String) = s.reversed()
}
