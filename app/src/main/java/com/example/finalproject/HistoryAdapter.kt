package com.example.finalproject

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val onDeleteClick: (HistoryItem) -> Unit
) : ListAdapter<HistoryItem, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view, onDeleteClick)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        itemView: View,
        private val onDeleteClick: (HistoryItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val tvToolName: TextView = itemView.findViewById(R.id.tv_tool_name)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        private val layoutInput: LinearLayout = itemView.findViewById(R.id.layout_input)
        private val tvInput: TextView = itemView.findViewById(R.id.tv_input)
        private val tvResult: TextView = itemView.findViewById(R.id.tv_result)
        private val ivStatus: ImageView = itemView.findViewById(R.id.iv_status)
        private val ivCopy: ImageView = itemView.findViewById(R.id.iv_copy)
        private val ivToolIcon: ImageView = itemView.findViewById(R.id.iv_tool_icon)

        fun bind(item: HistoryItem) {
            tvToolName.text = item.toolName
            tvTimestamp.text = formatTimestamp(item.timestamp)
            
            if (item.input.isNotBlank()) {
                layoutInput.visibility = View.VISIBLE
                tvInput.text = item.input
            } else {
                layoutInput.visibility = View.GONE
            }
            
            tvResult.text = item.output
            
            ivStatus.setImageResource(android.R.drawable.ic_menu_info_details)
            ivStatus.setColorFilter(ContextCompat.getColor(itemView.context, R.color.success_green))
            
            val iconRes = when (item.toolName) {
                "UUID Generator" -> android.R.drawable.ic_menu_compass
                "Hash Generator" -> android.R.drawable.ic_secure
                "Regex Tester" -> android.R.drawable.ic_menu_search
                "Base64 Coder" -> android.R.drawable.ic_menu_edit
                else -> android.R.drawable.ic_menu_info_details
            }
            ivToolIcon.setImageResource(iconRes)
            
            ivCopy.setOnClickListener {
                copyToClipboard(itemView.context, item.output)
            }
            
            itemView.setOnLongClickListener {
                onDeleteClick(item)
                true
            }
        }
        
        private fun copyToClipboard(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("History Result", text)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        
        private fun formatTimestamp(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            return when {
                diff < 60 * 1000 -> "Just now"
                diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} min ago"
                diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} hours ago"
                else -> "${diff / (24 * 60 * 60 * 1000)} days ago"
            }
        }
    }

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}