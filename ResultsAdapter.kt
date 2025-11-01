package com.example.arabicendings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResultsAdapter : RecyclerView.Adapter<ResultsAdapter.VH>() {

    private var items: List<String> = listOf()

    fun update(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv = view.findViewById<TextView>(R.id.tvWord)
        private val btnCopy = view.findViewById<ImageButton>(R.id.btnCopy)

        fun bind(word: String) {
            tv.text = word
            btnCopy.setOnClickListener {
                val cm = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("word", word))
                // feedback
                android.widget.Toast.makeText(itemView.context, "نسخت الكلمة", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
