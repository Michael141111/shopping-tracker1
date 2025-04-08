package com.inventory.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.inventory.app.R
import com.inventory.app.data.Product
import com.inventory.app.data.PurchaseOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PurchaseOrderAdapter(
    private val products: Map<Int, Product>,
    private val onCompleteClick: (PurchaseOrder) -> Unit,
    private val onCancelClick: (PurchaseOrder) -> Unit
) : ListAdapter<PurchaseOrder, PurchaseOrderAdapter.PurchaseOrderViewHolder>(PurchaseOrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase_order, parent, false)
        return PurchaseOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: PurchaseOrderViewHolder, position: Int) {
        val purchaseOrder = getItem(position)
        holder.bind(purchaseOrder)
    }

    inner class PurchaseOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productName)
        private val quantityTextView: TextView = itemView.findViewById(R.id.orderQuantity)
        private val dateTextView: TextView = itemView.findViewById(R.id.orderDate)
        private val statusTextView: TextView = itemView.findViewById(R.id.orderStatus)
        private val completeButton: Button = itemView.findViewById(R.id.completeButton)
        private val cancelButton: Button = itemView.findViewById(R.id.cancelButton)

        fun bind(purchaseOrder: PurchaseOrder) {
            val product = products[purchaseOrder.productId]
            productNameTextView.text = product?.name ?: "منتج غير معروف"
            
            val unitName = product?.unit ?: ""
            quantityTextView.text = "${purchaseOrder.quantity} ${unitName}"
            
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
            dateTextView.text = dateFormat.format(Date(purchaseOrder.requestDate))
            
            val statusText = when(purchaseOrder.status) {
                "pending" -> "قيد الانتظار"
                "completed" -> "مكتمل"
                "cancelled" -> "ملغي"
                else -> purchaseOrder.status
            }
            statusTextView.text = statusText
            
            // إظهار أو إخفاء الأزرار حسب حالة الطلب
            val buttonsVisible = purchaseOrder.status == "pending"
            completeButton.visibility = if (buttonsVisible) View.VISIBLE else View.GONE
            cancelButton.visibility = if (buttonsVisible) View.VISIBLE else View.GONE
            
            completeButton.setOnClickListener {
                onCompleteClick(purchaseOrder)
            }
            
            cancelButton.setOnClickListener {
                onCancelClick(purchaseOrder)
            }
        }
    }

    class PurchaseOrderDiffCallback : DiffUtil.ItemCallback<PurchaseOrder>() {
        override fun areItemsTheSame(oldItem: PurchaseOrder, newItem: PurchaseOrder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PurchaseOrder, newItem: PurchaseOrder): Boolean {
            return oldItem == newItem
        }
    }
}
