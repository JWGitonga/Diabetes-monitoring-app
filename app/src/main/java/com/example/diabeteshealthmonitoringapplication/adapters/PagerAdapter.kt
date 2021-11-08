package com.example.diabeteshealthmonitoringapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.diabeteshealthmonitoringapplication.R
import com.example.diabeteshealthmonitoringapplication.fragments.FragmentObject
import com.squareup.picasso.Picasso

class PagerAdapter (
private val meals: List<FragmentObject>,
private val context: Context,
private var listener: OnItemCLick?
) :
PagerAdapter() {
    interface OnItemCLick {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemCLick?) {
        this.listener = listener
    }

    override fun getCount(): Int {
        return meals.size
    }

    override fun isViewFromObject(view: View, o: Any): Boolean {
        return view == o
    }

//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val view: View = LayoutInflater.from(context).inflate(
//            R.layout.item_view_pager_header,
//            container,
//            false
//        )
//        val mealThumb: ImageView = view.findViewById(R.id.mealThumbHeader)
//        val mealName: TextView = view.findViewById(R.id.mealName)
//        val strMealThumb: String? = meals[position].strMealThumb
//        Picasso.get().load(strMealThumb).into(mealThumb)
//        val strMealName: String? = meals[position].strMeal
//        mealName.text = strMealName
//        container.addView(view, 0)
//        view.setOnClickListener {
//            if (position != POSITION_NONE) {
//                listener!!.onItemClick(position)
//            }
//        }
//        return view
//    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }


}