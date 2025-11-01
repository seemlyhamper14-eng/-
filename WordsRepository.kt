package com.example.arabicendings

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.ConcurrentHashMap

class WordsRepository(context: Context) {

    // We'll load CSV from assets/words_prepared.csv with lines: word,word_rev
    private val revIndex: MutableMap<String, MutableList<String>> = ConcurrentHashMap()
    private val wordsList: MutableList<Pair<String,String>> = mutableListOf()

    init {
        try {
            val ins = context.assets.open("words_prepared.csv")
            BufferedReader(InputStreamReader(ins, "UTF-8")).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    val ln = line!!.trim()
                    if (ln.isEmpty()) continue
                    // expect CSV: "word","word_rev"
                    val parts = ln.split(',').map { it.trim().trim('"') }
                    if (parts.size >= 2) {
                        val word = parts[0]
                        val rev = parts[1]
                        wordsList.add(Pair(word, rev))
                        // index prefixes of rev up to length 6 for quick lookup
                        val maxPrefix = minOf(rev.length, 6)
                        for (i in 1..maxPrefix) {
                            val pref = rev.substring(0, i)
                            val lst = revIndex.getOrPut(pref) { mutableListOf() }
                            lst.add(word)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun searchByReversedPrefix(revPrefix: String, limit: Int = 500): List<String> {
        val key = revPrefix.take(6) // we indexed up to 6
        val candidates = revIndex[key] ?: return listOf()
        // filter candidates by full prefix
        val results = candidates.filter { it.reversed().startsWith(revPrefix) }.distinct()
        return if (results.size > limit) results.subList(0, limit) else results
    }
}
