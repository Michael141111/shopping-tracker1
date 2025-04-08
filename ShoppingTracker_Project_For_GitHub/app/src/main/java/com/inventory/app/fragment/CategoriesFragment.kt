package com.inventory.app.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.inventory.app.R
import com.inventory.app.adapter.CategoryAdapter
import com.inventory.app.data.Category
import com.inventory.app.viewmodel.CategoryViewModel

class CategoriesFragment : Fragment() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customizable_items, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.emptyView)

        // إعداد RecyclerView
        adapter = CategoryAdapter(
            onEditClick = { category -> showEditCategoryDialog(category) },
            onDeleteClick = { category -> confirmDeleteCategory(category) }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // إعداد ViewModel
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        // مراقبة التغييرات في قائمة الفئات
        categoryViewModel.allCategories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
            if (categories.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }

        return view
    }

    fun showAddCategoryDialog() {
        showCategoryDialog(null)
    }

    private fun showEditCategoryDialog(category: Category) {
        showCategoryDialog(category)
    }

    private fun showCategoryDialog(category: Category?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_new_item, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.newItemInput)
        
        // إذا كان التعديل، املأ البيانات الحالية
        if (category != null) {
            nameInput.setText(category.name)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (category == null) "إضافة فئة جديدة" else "تعديل الفئة")
            .create()

        dialogView.findViewById<View>(R.id.saveNewItemButton).setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى إدخال اسم الفئة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (category == null) {
                // إضافة فئة جديدة
                val newCategory = Category(name = name)
                categoryViewModel.insert(newCategory)
            } else {
                // تحديث فئة موجودة
                val updatedCategory = category.copy(name = name)
                categoryViewModel.update(updatedCategory)
            }

            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.cancelNewItemButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun confirmDeleteCategory(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من رغبتك في حذف فئة ${category.name}؟")
            .setPositiveButton("نعم") { _, _ ->
                categoryViewModel.delete(category)
            }
            .setNegativeButton("لا", null)
            .show()
    }
}
