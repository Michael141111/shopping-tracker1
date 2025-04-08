package com.inventory.app.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.inventory.app.R
import com.inventory.app.viewmodel.InventoryItemViewModel
import com.inventory.app.viewmodel.ProductViewModel

class InventoryTrackingFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_inventory_tracking, container, false)
        
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        
        // إعداد ViewPager
        val pagerAdapter = InventoryPagerAdapter(requireActivity())
        viewPager.adapter = pagerAdapter
        
        // ربط TabLayout مع ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "المخزون الحالي"
                1 -> "المنتجات المنتهية"
                2 -> "التنبيهات"
                else -> ""
            }
        }.attach()
        
        return view
    }
    
    inner class InventoryPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CurrentInventoryFragment()
                1 -> ExpiringItemsFragment()
                2 -> AlertsFragment()
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}

class CurrentInventoryFragment : Fragment() {
    
    private lateinit var inventoryItemViewModel: InventoryItemViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var emptyView: TextView
    
    private val products = mutableMapOf<Int, com.inventory.app.data.Product>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_current_inventory, container, false)
        
        recyclerView = view.findViewById(R.id.inventoryRecyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        
        // إعداد ViewModels
        inventoryItemViewModel = ViewModelProvider(this).get(InventoryItemViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        
        // إعداد RecyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        
        // الحصول على قائمة المنتجات
        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            productList.forEach { product ->
                products[product.id] = product
            }
            setupAdapter()
        }
        
        // إعداد أزرار التصفية
        setupFilterChips(view)
        
        return view
    }
    
    private fun setupAdapter() {
        val adapter = com.inventory.app.adapter.InventoryAdapter(products)
        recyclerView.adapter = adapter
        
        // مراقبة التغييرات في قائمة عناصر المخزون
        inventoryItemViewModel.allInventoryItems.observe(viewLifecycleOwner) { inventoryItems ->
            adapter.submitList(inventoryItems)
            if (inventoryItems.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }
    }
    
    private fun setupFilterChips(view: View) {
        val chipAll = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipAll)
        val chipStarch = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipStarch)
        val chipProtein = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipProtein)
        val chipStorage = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipStorage)
        val chipLegumes = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipLegumes)
        
        // تنفيذ وظائف التصفية حسب الفئة
        // (سيتم تنفيذها في المستقبل)
    }
}

class ExpiringItemsFragment : Fragment() {
    
    private lateinit var inventoryItemViewModel: InventoryItemViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var emptyView: TextView
    
    private val products = mutableMapOf<Int, com.inventory.app.data.Product>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expiring_items, container, false)
        
        recyclerView = view.findViewById(R.id.expiringItemsRecyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        
        // إعداد ViewModels
        inventoryItemViewModel = ViewModelProvider(this).get(InventoryItemViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        
        // إعداد RecyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        
        // الحصول على قائمة المنتجات
        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            productList.forEach { product ->
                products[product.id] = product
            }
            setupAdapter()
        }
        
        return view
    }
    
    private fun setupAdapter() {
        val adapter = com.inventory.app.adapter.InventoryAdapter(products)
        recyclerView.adapter = adapter
        
        // الحصول على المنتجات التي ستنتهي خلال الشهر القادم
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.MONTH, 1)
        val oneMonthFromNow = calendar.timeInMillis
        
        // مراقبة التغييرات في قائمة المنتجات المنتهية
        inventoryItemViewModel.getExpiringItems(oneMonthFromNow).observe(viewLifecycleOwner) { expiringItems ->
            adapter.submitList(expiringItems)
            if (expiringItems.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }
    }
}

class AlertsFragment : Fragment() {
    
    private lateinit var inventoryAlertViewModel: com.inventory.app.viewmodel.InventoryAlertViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var emptyView: TextView
    
    private val products = mutableMapOf<Int, com.inventory.app.data.Product>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alerts, container, false)
        
        recyclerView = view.findViewById(R.id.alertsRecyclerView)
        emptyView = view.findViewById(R.id.emptyView)
        
        // إعداد ViewModels
        inventoryAlertViewModel = ViewModelProvider(this).get(com.inventory.app.viewmodel.InventoryAlertViewModel::class.java)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        
        // إعداد RecyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        
        // الحصول على قائمة المنتجات
        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            productList.forEach { product ->
                products[product.id] = product
            }
            setupAdapter()
        }
        
        // إعداد أزرار التصفية
        setupFilterChips(view)
        
        return view
    }
    
    private fun setupAdapter() {
        val adapter = com.inventory.app.adapter.AlertAdapter(products)
        recyclerView.adapter = adapter
        
        // مراقبة التغييرات في قائمة التنبيهات
        inventoryAlertViewModel.allAlerts.observe(viewLifecycleOwner) { alerts ->
            adapter.submitList(alerts)
            if (alerts.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        }
    }
    
    private fun setupFilterChips(view: View) {
        val chipAllAlerts = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipAllAlerts)
        val chipLowStock = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipLowStock)
        val chipExpiringSoon = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipExpiringSoon)
        
        chipAllAlerts.setOnClickListener {
            inventoryAlertViewModel.allAlerts.observe(viewLifecycleOwner) { alerts ->
                (recyclerView.adapter as com.inventory.app.adapter.AlertAdapter).submitList(alerts)
            }
        }
        
        chipLowStock.setOnClickListener {
            inventoryAlertViewModel.getAlertsByType("low_stock").observe(viewLifecycleOwner) { alerts ->
                (recyclerView.adapter as com.inventory.app.adapter.AlertAdapter).submitList(alerts)
            }
        }
        
        chipExpiringSoon.setOnClickListener {
            inventoryAlertViewModel.getAlertsByType("expiring_soon").observe(viewLifecycleOwner) { alerts ->
                (recyclerView.adapter as com.inventory.app.adapter.AlertAdapter).submitList(alerts)
            }
        }
    }
}
