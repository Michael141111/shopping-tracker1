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
import com.inventory.app.adapter.UnitAdapter
import com.inventory.app.data.Unit
import com.inventory.app.viewmodel.UnitViewModel

class UnitsFragment : Fragment() {

    private lateinit var unitViewModel: UnitViewModel
    private lateinit var adapter: UnitAdapter
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
        adapter = UnitAdapter(
            onEditClick = { unit -> showEditUnitDialog(unit) },
            onDeleteClick = { unit -> confirmDeleteUnit(unit) }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // إعداد ViewModel
        unitViewModel = ViewModelProvider(this).get(UnitViewModel::class.java)

        // مراقبة التغييرات في قائمة وحدات القياس
        unitViewModel.allUnits.observe(viewLifecycleOwner) { units ->
            adapter.submitList(units)
            if (units.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }

        return view
    }

    fun showAddUnitDialog() {
        showUnitDialog(null)
    }

    private fun showEditUnitDialog(unit: Unit) {
        showUnitDialog(unit)
    }

    private fun showUnitDialog(unit: Unit?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_new_item, null)

        val nameInput = dialogView.findViewById<EditText>(R.id.newItemInput)
        
        // إذا كان التعديل، املأ البيانات الحالية
        if (unit != null) {
            nameInput.setText(unit.name)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle(if (unit == null) "إضافة وحدة قياس جديدة" else "تعديل وحدة القياس")
            .create()

        dialogView.findViewById<View>(R.id.saveNewItemButton).setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى إدخال اسم وحدة القياس", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (unit == null) {
                // إضافة وحدة قياس جديدة
                val newUnit = Unit(name = name)
                unitViewModel.insert(newUnit)
            } else {
                // تحديث وحدة قياس موجودة
                val updatedUnit = unit.copy(name = name)
                unitViewModel.update(updatedUnit)
            }

            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.cancelNewItemButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun confirmDeleteUnit(unit: Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("تأكيد الحذف")
            .setMessage("هل أنت متأكد من رغبتك في حذف وحدة القياس ${unit.name}؟")
            .setPositiveButton("نعم") { _, _ ->
                unitViewModel.delete(unit)
            }
            .setNegativeButton("لا", null)
            .show()
    }
}
