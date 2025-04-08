package com.inventory.app.fragment

import android.app.AlertDialog
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
import com.inventory.app.adapter.ProductAdapter
import com.inventory.app.data.Product
import com.inventory.app.viewmodel.ProductViewModel

class ProductsFragment : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    
    // مخزن للفئات ووحدات القياس القابلة للتخصيص
    private val categories = mutableListOf("نشويات", "بروتينات", "خزين", "بقوليات")
    private val units = mutableListOf("كيلو", "وحدة", "جرام", "سم", "لتر")
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)
        
        recyclerView = view.findViewById(R.id.productsRecyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        
        // إعداد RecyclerView
        adapter = ProductAdapter(
            onEditClick = { product -> showAddEditProductDialog(product) },
            onDeleteClick = { product -> confirmDeleteProduct(product) }
        )
        
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        // إعداد ViewModel
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        
        // مراقبة التغييرات في قائمة المنتجات
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
            if (products.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }
        
        // إعداد أزرار التصفية
        setupFilterChips(view)
        
        // الحصول على زر الإضافة من النشاط الرئيسي
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            showAddEditProductDialog(null)
        }
        
        return view
    }
    
    private fun setupFilterChips(view: View) {
        val chipAll = view.findViewById<Chip>(R.id.chipAll)
        val chipStarch = view.findViewById<Chip>(R.id.chipStarch)
        val chipProtein = view.findViewById<Chip>(R.id.chipProtein)
        val chipStorage = view.findViewById<Chip>(R.id.chipStorage)
        val chipLegumes = view.findViewById<Chip>(R.id.chipLegumes)
        
        chipAll.setOnClickListener {
            productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                adapter.submitList(products)
            }
        }
        
        chipStarch.setOnClickListener {
            productViewModel.getProductsByCategory("نشويات").observe(viewLifecycleOwner) { products ->
                adapter.submitList(products)
            }
        }
        
        chipProtein.setOnClickListener {
            productViewModel.getProductsByCategory("بروتينات").observe(viewLifecycleOwner) { products ->
                adapter.submitList(products)
            }
        }
        
        chipStorage.setOnClickListener {
            productViewModel.getProductsByCategory("خزين").observe(viewLifecycleOwner) { products ->
                adapter.submitList(products)
            }
        }
        
        chipLegumes.setOnClickListener {
            productViewModel.getProductsByCategory("بقوليات").observe(viewLifecycleOwner) { products ->
                adapter.submitList(products)
            }
        }
    }
    
    private fun showAddEditProductDialog(product: Product?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_edit_product, null)
        
        val nameInput = dialogView.findViewById<EditText>(R.id.productNameInput)
        val priceInput = dialogView.findViewById<EditText>(R.id.productPriceInput)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
        val unitSpinner = dialogView.findViewById<Spinner>(R.id.unitSpinner)
        val addCategoryButton = dialogView.findViewById<Button>(R.id.addCategoryButton)
        val addUnitButton = dialogView.findViewById<Button>(R.id.addUnitButton)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
        
        // إعداد محولات القوائم المنسدلة
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter
        
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unitSpinner.adapter = unitAdapter
        
        // إذا كان التعديل، املأ البيانات الحالية
        if (product != null) {
            nameInput.setText(product.name)
            priceInput.setText(product.price.toString())
            categorySpinner.setSelection(categories.indexOf(product.category))
            unitSpinner.setSelection(units.indexOf(product.unit))
        }
        
        // إنشاء مربع الحوار
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (product == null) "إضافة منتج جديد" else "تعديل المنتج")
            .create()
        
        // إعداد زر إضافة فئة جديدة
        addCategoryButton.setOnClickListener {
            showAddNewItemDialog("فئة") { newCategory ->
                categories.add(newCategory)
                categoryAdapter.notifyDataSetChanged()
                categorySpinner.setSelection(categories.size - 1)
            }
        }
        
        // إعداد زر إضافة وحدة قياس جديدة
        addUnitButton.setOnClickListener {
            showAddNewItemDialog("وحدة قياس") { newUnit ->
                units.add(newUnit)
                unitAdapter.notifyDataSetChanged()
                unitSpinner.setSelection(units.size - 1)
            }
        }
        
        // إعداد زر الحفظ
        saveButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val priceText = priceInput.text.toString().trim()
            
            if (name.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val price = priceText.toDoubleOrNull()
            if (price == null) {
                Toast.makeText(requireContext(), "يرجى إدخال سعر صحيح", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            val category = categories[categorySpinner.selectedItemPosition]
            val unit = units[unitSpinner.selectedItemPosition]
            
            if (product == null) {
                // إضافة منتج جديد
                val newProduct = Product(
                    name = name,
                    price = price,
                    category = category,
                    unit = unit
                )
                productViewModel.insert(newProduct)
            } else {
                // تحديث منتج موجود
                val updatedProduct = product.copy(
                    name = name,
                    price = price,
                    category = category,
                    unit = unit
                )
                productViewModel.update(updatedProduct)
            }
            
            dialog.dismiss()
        }
        
        // إعداد زر الإلغاء
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun showAddNewItemDialog(itemType: String, onSave: (String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_new_item, null)
        
        val newItemInput = dialogView.findViewById<EditText>(R.id.newItemInput)
        val saveButton = dialogView.findViewById<Button>(R.id.saveNewItemButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelNewItemButton)
        
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("إضافة $itemType جديدة")
            .create()
        
        saveButton.setOnClickListener {
            val newItem = newItemInput.text.toString().trim()
            if (newItem.isNotEmpty()) {
                onSave(newItem)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "يرجى إدخال اسم", Toast.LENGTH_SHORT).show()
            }
        }
        
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
    
    private fun confirmDeleteProduct(product: Product) {
        AlertDialog.Builder(requireContext())
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من رغبتك في حذف ${product.name}؟")
            .setPositiveButton("نعم") { _, _ ->
                productViewModel.delete(product)
            }
            .setNegativeButton("لا", null)
            .show()
    }
}
