package com.inventory.app.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inventory.app.R
import com.inventory.app.adapter.PurchaseOrderAdapter
import com.inventory.app.data.Product
import com.inventory.app.data.PurchaseOrder
import com.inventory.app.data.PurchaseTransaction
import com.inventory.app.viewmodel.ProductViewModel
import com.inventory.app.viewmodel.PurchaseOrderViewModel
import com.inventory.app.viewmodel.PurchaseTransactionViewModel
import java.util.*

class PurchaseOrdersFragment : Fragment() {

    private lateinit var purchaseOrderViewModel: PurchaseOrderViewModel
    private lateinit var purchaseTransactionViewModel: PurchaseTransactionViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var adapter: PurchaseOrderAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    
    private val products = mutableMapOf<Int, Product>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase_orders, container, false)
        
        recyclerView = view.findViewById(R.id.purchaseOrdersRecyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        
        // إعداد ViewModels
        purchaseOrderViewModel = ViewModelProvider(this).get(PurchaseOrderViewModel::class.java)
        purchaseTransactionViewModel = ViewModelProvider(this).get(PurchaseTransactionViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        
        // الحصول على قائمة المنتجات
        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            productList.forEach { product ->
                products[product.id] = product
            }
            setupAdapter()
        }
        
        // إعداد أزرار التصفية
        setupFilterChips(view)
        
        // الحصول على زر الإضافة من النشاط الرئيسي
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            showAddPurchaseOrderDialog()
        }
        
        return view
    }
    
    private fun setupAdapter() {
        // إعداد RecyclerView
        adapter = PurchaseOrderAdapter(
            products,
            onCompleteClick = { purchaseOrder -> showCompletePurchaseDialog(purchaseOrder) },
            onCancelClick = { purchaseOrder -> confirmCancelPurchaseOrder(purchaseOrder) }
        )
        
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // مراقبة التغييرات في قائمة طلبات الشراء
        purchaseOrderViewModel.allPurchaseOrders.observe(viewLifecycleOwner) { purchaseOrders ->
            adapter.submitList(purchaseOrders)
            if (purchaseOrders.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }
    }
    
    private fun setupFilterChips(view: View) {
        val chipAll = view.findViewById<Chip>(R.id.chipAll)
        val chipPending = view.findViewById<Chip>(R.id.chipPending)
        val chipCompleted = view.findViewById<Chip>(R.id.chipCompleted)
        val chipCancelled = view.findViewById<Chip>(R.id.chipCancelled)
        
        chipAll.setOnClickListener {
            purchaseOrderViewModel.allPurchaseOrders.observe(viewLifecycleOwner) { purchaseOrders ->
                adapter.submitList(purchaseOrders)
            }
        }
        
        chipPending.setOnClickListener {
            purchaseOrderViewModel.getPurchaseOrdersByStatus("pending").observe(viewLifecycleOwner) { purchaseOrders ->
                adapter.submitList(purchaseOrders)
            }
        }
        
        chipCompleted.setOnClickListener {
            purchaseOrderViewModel.getPurchaseOrdersByStatus("completed").observe(viewLifecycleOwner) { purchaseOrders ->
                adapter.submitList(purchaseOrders)
            }
        }
        
        chipCancelled.setOnClickListener {
            purchaseOrderViewModel.getPurchaseOrdersByStatus("cancelled").observe(viewLifecycleOwner) { purchaseOrders ->
                adapter.submitList(purchaseOrders)
            }
        }
    }
    
    private fun showAddPurchaseOrderDialog() {
        if (products.isEmpty()) {
            Toast.makeText(requireContext(), "يرجى إضافة منتجات أولاً", Toast.LENGTH_SHORT).show()
            return
        }
        
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_purchase_order, null)
        
        val productSpinner = dialogView.findViewById<Spinner>(R.id.productSpinner)
        val quantityInput = dialogView.findViewById<EditText>(R.id.quantityInput)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        
        // إعداد محول القائمة المنسدلة للمنتجات
        val productsList = products.values.toList()
        val productAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            productsList.map { it.name }
        )
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productSpinner.adapter = productAdapter
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("إضافة طلب شراء جديد")
            .create()
        
        saveButton.setOnClickListener {
            val selectedProductPosition = productSpinner.selectedItemPosition
            if (selectedProductPosition < 0 || selectedProductPosition >= productsList.size) {
                Toast.makeText(requireContext(), "يرجى اختيار منتج", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val quantityText = quantityInput.text.toString().trim()
            if (quantityText.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى إدخال الكمية", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val quantity = quantityText.toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(requireContext(), "يرجى إدخال كمية صحيحة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val selectedProduct = productsList[selectedProductPosition]
            
            val purchaseOrder = PurchaseOrder(
                productId = selectedProduct.id,
                quantity = quantity,
                requestDate = System.currentTimeMillis(),
                status = "pending"
            )
            
            purchaseOrderViewModel.insert(purchaseOrder)
            dialog.dismiss()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showCompletePurchaseDialog(purchaseOrder: PurchaseOrder) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_complete_purchase, null)
        
        val productNameText = dialogView.findViewById<TextView>(R.id.productNameText)
        val purchasedQuantityInput = dialogView.findViewById<EditText>(R.id.purchasedQuantityInput)
        val purchaseDatePicker = dialogView.findViewById<DatePicker>(R.id.purchaseDatePicker)
        val hasExpiryDateCheckbox = dialogView.findViewById<CheckBox>(R.id.hasExpiryDateCheckbox)
        val expiryDateLabel = dialogView.findViewById<TextView>(R.id.expiryDateLabel)
        val expiryDatePicker = dialogView.findViewById<DatePicker>(R.id.expiryDatePicker)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        
        // إعداد البيانات الأولية
        val product = products[purchaseOrder.productId]
        productNameText.text = product?.name ?: "منتج غير معروف"
        purchasedQuantityInput.setText(purchaseOrder.quantity.toString())
        
        // إعداد تاريخ الشراء (اليوم الحالي)
        val calendar = Calendar.getInstance()
        purchaseDatePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )
        
        // إعداد تاريخ الانتهاء (بعد شهر من اليوم)
        calendar.add(Calendar.MONTH, 1)
        expiryDatePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            null
        )
        
        // إعداد مربع اختيار تاريخ الانتهاء
        hasExpiryDateCheckbox.setOnCheckedChangeListener { _, isChecked ->
            expiryDateLabel.visibility = if (isChecked) View.VISIBLE else View.GONE
            expiryDatePicker.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("تسجيل عملية شراء")
            .create()
        
        saveButton.setOnClickListener {
            val quantityText = purchasedQuantityInput.text.toString().trim()
            if (quantityText.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى إدخال الكمية", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val quantity = quantityText.toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(requireContext(), "يرجى إدخال كمية صحيحة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            // الحصول على تاريخ الشراء
            val purchaseCalendar = Calendar.getInstance()
            purchaseCalendar.set(
                purchaseDatePicker.year,
                purchaseDatePicker.month,
                purchaseDatePicker.dayOfMonth
            )
            val purchaseDate = purchaseCalendar.timeInMillis
            
            // الحصول على تاريخ الانتهاء إذا كان محدداً
            var expiryDate: Long? = null
            if (hasExpiryDateCheckbox.isChecked) {
                val expiryCalendar = Calendar.getInstance()
                expiryCalendar.set(
                    expiryDatePicker.year,
                    expiryDatePicker.month,
                    expiryDatePicker.dayOfMonth
                )
                expiryDate = expiryCalendar.timeInMillis
            }
            
            // تحديث حالة طلب الشراء
            val updatedPurchaseOrder = purchaseOrder.copy(status = "completed")
            purchaseOrderViewModel.update(updatedPurchaseOrder)
            
            // إنشاء سجل عملية الشراء
            val purchaseTransaction = PurchaseTransaction(
                orderId = purchaseOrder.id,
                purchaseDate = purchaseDate,
                quantity = quantity,
                expiryDate = expiryDate
            )
            purchaseTransactionViewModel.insert(purchaseTransaction)
            
            dialog.dismiss()
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun confirmCancelPurchaseOrder(purchaseOrder: PurchaseOrder) {
        AlertDialog.Builder(requireContext())
            .setTitle("تأكيد الإلغاء")
            .setMessage("هل أنت متأكد من رغبتك في إلغاء طلب الشراء هذا؟")
            .setPositiveButton("نعم") { _, _ ->
                val updatedPurchaseOrder = purchaseOrder.copy(status = "cancelled")
                purchaseOrderViewModel.update(updatedPurchaseOrder)
            }
            .setNegativeButton("لا", null)
            .show()
    }
}
