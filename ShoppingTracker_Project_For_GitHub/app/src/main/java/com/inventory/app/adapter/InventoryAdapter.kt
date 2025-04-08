package com.inventory.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inventory.app.R
import com.inventory.app.data.InventoryItem
import com.inventory.app.data.Product
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InventoryAdapter(
    private val products: Map<Int, Product>
) : ListAdapter<InventoryItem, InventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return InventoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val inventoryItem = getItem(position)
        holder.bind(inventoryItem)
    }

    inner class InventoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.availableQuantity)
        private val purchaseDateTextView: TextView = itemView.findViewById(R.id.purchaseDate)
        private val expiryDateTextView: TextView = itemView.findViewById(R.id.expiryDate)
        private val expiryDateLayout: View = itemView.findViewById(R.id.expiryDateLayout)

        fun bind(inventoryItem: InventoryItem) {
            val product = products[inventoryItem.productId]
            productNameTextView.text = product?.name ?: "منتج غير معروف"
            
            val unitName = product?.unit ?: ""
            quantityTextView.text = "${inventoryItem.quantity} ${unitName}"
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
            purchaseDateTextView.text = dateFormat.format(Date(inventoryItem.purchaseDate))
            
            if (inventoryItem.expiryDate != null) {
                expiryDateLayout.visibility = View.VISIBLE
                expiryDateTextView.text = dateFormat.format(Date(inventoryItem.expiryDate))
            } else {
                expiryDateLayout.visibility = View.GONE
            }
        }
    }

    class InventoryDiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
