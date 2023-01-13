package com.ghadeerh.customnavigationview

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MenuItemsAdapter(private val itemsList: MutableList<CustomMenuItem>,
                       private var menuStyle: Int)
    : RecyclerView.Adapter<MenuItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            when(menuStyle){
                NavigationMenuStyle.Collapsed.STYLE_ICONIFIED,
                NavigationMenuStyle.Collapsed.STYLE_ICONIFIED_NO_TITLE -> R.layout.item_menu_iconified
                NavigationMenuStyle.Expanded.STYLE_STANDARD -> R.layout.item_menu_standard
                NavigationMenuStyle.Expanded.STYLE_GRID -> R.layout.item_menu_grid
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItemsList: List<CustomMenuItem>){
        itemsList.clear()
        itemsList.addAll(newItemsList)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

        fun bind(menuItem: CustomMenuItem){
            tvTitle.text = menuItem.title
            tvTitle.isSelected = true
            if(menuStyle == NavigationMenuStyle.Collapsed.STYLE_ICONIFIED_NO_TITLE)
                tvTitle.visibility = View.GONE
            ivIcon.setImageDrawable(menuItem.icon)


            when(menuItem.isSelected){
                true -> {
                    ivIcon.setColorFilter(MenuConfigurations.selectedIconTintColor)
                    when(menuStyle){
                        NavigationMenuStyle.Collapsed.STYLE_ICONIFIED,
                        NavigationMenuStyle.Collapsed.STYLE_ICONIFIED_NO_TITLE ->{
                            ivIcon.backgroundTintList = ColorStateList.valueOf(MenuConfigurations.selectedIconBackgroundTintColor)
                            tvTitle.setTextColor(ColorStateList.valueOf(MenuConfigurations.selectedIconBackgroundTintColor))
                        }
                        else ->{
                            itemView.rootView.backgroundTintList = ColorStateList.valueOf(MenuConfigurations.selectedIconBackgroundTintColor)
                            tvTitle.setTextColor(ColorStateList.valueOf(MenuConfigurations.selectedIconTintColor))
                        }

                    }
                }
                false -> {
                    ivIcon.setColorFilter(MenuConfigurations.iconTintColor)
                    when(menuStyle){
                        NavigationMenuStyle.Collapsed.STYLE_ICONIFIED,
                        NavigationMenuStyle.Collapsed.STYLE_ICONIFIED_NO_TITLE ->{
                            ivIcon.backgroundTintList = ColorStateList.valueOf(MenuConfigurations.iconBackgroundTintColor)
                            tvTitle.setTextColor(ColorStateList.valueOf(MenuConfigurations.iconBackgroundTintColor))
                        }
                        else ->{
                            itemView.rootView.backgroundTintList = ColorStateList.valueOf(MenuConfigurations.iconBackgroundTintColor)
                            tvTitle.setTextColor(ColorStateList.valueOf(MenuConfigurations.iconTintColor))
                        }
                    }

                }
            }

            if(menuStyle in listOf(
                    NavigationMenuStyle.Expanded.STYLE_STANDARD,
                    NavigationMenuStyle.Expanded.STYLE_GRID
            )){
                val drawable: GradientDrawable = itemView.background as GradientDrawable
                drawable.cornerRadius = MenuConfigurations.gridBoxRadius
            }
            itemView.setOnClickListener { MenuConfigurations.onMenuItemClickListener?.onItemClicked(menuItem.id) }
        }
    }
}

interface OnMenuItemClickListener{
    fun onItemClicked(itemId: Int)
}