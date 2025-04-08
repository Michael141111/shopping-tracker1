package com.inventory.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.inventory.app.fragment.CategoriesFragment
import com.inventory.app.fragment.UnitsFragment

class CustomizableItemsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var fab: FloatingActionButton
    
    private lateinit var categoriesFragment: CategoriesFragment
    private lateinit var unitsFragment: UnitsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customizable_items)
        
        // تعيين العنوان
        title = "إدارة الفئات ووحدات القياس"
        
        // تهيئة المكونات
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        fab = findViewById(R.id.fab)
        
        // إنشاء الفراغمنتات
        categoriesFragment = CategoriesFragment()
        unitsFragment = UnitsFragment()
        
        // إعداد ViewPager
        val pagerAdapter = CustomizablePagerAdapter(this)
        viewPager.adapter = pagerAdapter
        
        // ربط TabLayout مع ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "الفئات"
                1 -> "وحدات القياس"
                else -> ""
            }
        }.attach()
        
        // إعداد زر الإضافة
        fab.setOnClickListener {
            val currentPosition = viewPager.currentItem
            when (currentPosition) {
                0 -> categoriesFragment.showAddCategoryDialog()
                1 -> unitsFragment.showAddUnitDialog()
            }
        }
    }
    
    inner class CustomizablePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> categoriesFragment
                1 -> unitsFragment
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
