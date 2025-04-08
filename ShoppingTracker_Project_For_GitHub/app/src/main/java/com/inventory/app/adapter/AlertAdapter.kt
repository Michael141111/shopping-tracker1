package com.inventory.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inventory.app.R
import com.inventory.app.data.InventoryAlert
import com.inventory.app.data.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertAdapter(
    private val products: Map<Int, Product>
) : ListAdapter<InventoryAlert, AlertAdapter.AlertViewHolder>(AlertDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alert, parent, false)
        return AlertViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert)
    }

    inner class AlertViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productName)
        private val alertTypeTextView: TextView = itemView.findViewById(R.id.alertType)
        private val alertDetailsTextView: TextView = itemView.findViewById(R.id.alertDetails)
        private val alertDateTextView: TextView = itemView.findViewById(R.id.alertDate)

        fun bind(alert: InventoryAlert) {
            val product = products[alert.productId]
            productNameTextView.text = product?.name ?: "منتج غير معروف"
            
            val alertTypeText = when(alert.alertType) {
                "low_stock" -> "المخزون منخفض"
                "expiring_soon" -> "ينتهي قريباً"
                else -> alert.alertType
            }
            alertTypeTextView.text = alertTypeText
            
            val alertDetailsText = if (alert.alertType == "low_stock" && alert.threshold != null) {
                "الكمية أقل من ${alert.threshold} ${product?.unit ?: ""}"
            } else {
                "يرجى التحقق من المنتج"
            }
            alertDetailsTextView.text = alertDetailsText
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
            alertDateTextView.text = dateFormat.format(Date(alert.createdDate))
        }
    }

    class AlertDiffCallback : DiffUtil.ItemCallback<InventoryAlert>() {
        override fun areItemsTheSame(oldItem: InventoryAlert, newItem: InventoryAlert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InventoryAlert, newItem: InventoryAlert): Boolean {
            return oldItem == newItem
        }
    }
}
