package com.example

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.customnavigationrailview.R

class MenuItemsAdapter(private val itemsList: List<CustomMenuItem>,
                       private var menuStyle: Int,
                       private var onMenuItemClickListener: OnMenuItemClickListener?)
    : RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            when(menuStyle){
                NavigationMenuStyle.STYLE_ICONIFIED,
                NavigationMenuStyle.STYLE_ICONIFIED_NO_TITLE -> R.layout.item_menu_iconified
                NavigationMenuStyle.STYLE_STANDARD -> R.layout.item_menu_standard
                NavigationMenuStyle.STYLE_BOXES -> R.layout.item_menu_box
                else -> R.layout.item_menu_iconified
                           },
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun setOnItemClickListener(onMenuItemClickListener: OnMenuItemClickListener?){
        this.onMenuItemClickListener = onMenuItemClickListener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        fun bind(menuItem: CustomMenuItem){
            tvTitle.text = menuItem.title
            if(menuStyle == NavigationMenuStyle.STYLE_ICONIFIED_NO_TITLE)
                tvTitle.visibility = View.GONE
            ivIcon.setImageDrawable(menuItem.icon)

            itemView.setOnClickListener { onMenuItemClickListener?.onItemClicked(menuItem.id) }
        }
    }
}

interface OnMenuItemClickListener{
    fun onItemClicked(itemId: Int)
}