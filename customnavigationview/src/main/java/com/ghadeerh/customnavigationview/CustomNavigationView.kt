package com.ghadeerh.customnavigationview


import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*


class CustomNavigationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    companion object{
        private const val TAG = "CustomNavigationView"
    }

    private val typedArray  = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomNavView, 0, 0)
    private var mainLayoutRes = typedArray.getResourceId(R.styleable.CustomNavView_mainLayout, -1)
    private var menuRes = typedArray.getResourceId(R.styleable.CustomNavView_menu, -1)
    private var headerRes = typedArray.getResourceId(R.styleable.CustomNavView_header, -1)
    private var collapseMenuStyle = typedArray.getInt(R.styleable.CustomNavView_collapseMenuStyle, NavigationMenuStyle.Collapsed.STYLE_HIDDEN)
    private var expandMenuStyle = typedArray.getInt(R.styleable.CustomNavView_expandMenuStyle, NavigationMenuStyle.Expanded.STYLE_STANDARD)
    private var isExpanded = typedArray.getBoolean(R.styleable.CustomNavView_expanded, false)

    private val screenWidth: Int

    val demoItems = listOf(
        CustomMenuItem(0, "Item 1", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!, isSelected = true),
        CustomMenuItem(1, "Item 2", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(2, "Item 3", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(3, "Item 4", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(4, "Item 5", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(5, "Item 6", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(6, "Item 7", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(7, "Item 8", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(8, "Item 9", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
        CustomMenuItem(9, "Item 10", ContextCompat.getDrawable(context, R.drawable.ic_menu_24)!!),
    )

    private lateinit var root: View
    private lateinit var motionLayout: MotionLayout
    private lateinit var navLayout: ConstraintLayout
    private lateinit var headerContainer: FrameLayout
    private lateinit var mainLayout: ViewGroup
    private lateinit var ivToggle: ImageView
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var itemsAdapter: MenuItemsAdapter

    private var externalToggleView: View? = null      // developer can choose any view to toggle menu
    private var headerView: View? = null
    @SuppressLint("RestrictedApi")
    private var menu: Menu? = MenuBuilder(context)


    init {
        screenWidth = context.resources.displayMetrics.widthPixels
        initView()
    }

    private fun initView(){

        root = inflate(context, R.layout.layout_nav_view, this)
        motionLayout = root.findViewById(R.id.motionLayout)
        navLayout = motionLayout.findViewById(R.id.navLayout)
        headerContainer = root.findViewById(R.id.headerContainer)
        itemsRecyclerView = root.findViewById(R.id.recyclerView)
        ivToggle = root.findViewById(R.id.ivToggle)

        ivToggle.setOnClickListener{ handleToggleMenu() }
        ivToggle.setImageDrawable(ContextCompat.getDrawable(context, getAppropriateToggleIcon()))

        // Inflate header layout
        if(headerRes != -1){
            headerView = LayoutInflater.from(context).inflate(headerRes, null, false)
            headerContainer.addView(headerView)
        }

        //
        if(menuRes != -1){
            PopupMenu(context, this).menuInflater.inflate(menuRes, menu)
        }
        else if(isInEditMode){
            demoItems.forEach {
                val item = menu?.add(Menu.NONE, it.id, Menu.NONE, it.title)
                item?.icon = it.icon
                item?.isChecked = it.isSelected
            }
        }

        initConfigurations()

        if(collapseMenuStyle != NavigationMenuStyle.Collapsed.STYLE_HIDDEN){
            val initialMenuStyle = when(isExpanded){
                true -> expandMenuStyle
                false -> collapseMenuStyle
            }

            publishUI(initialMenuStyle)
        }
        else{
            hideMenu()
        }

        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        if(mainLayoutRes != -1){
            val parent = parent as ViewGroup
            mainLayout = parent.findViewById(mainLayoutRes)
            mainLayout.layoutTransition = LayoutTransition()
            mainLayout.layoutTransition.setDuration(500)
            mainLayout.setOnClickListener {
                if(isExpanded) handleToggleMenu()
            }
        }
    }

    private fun initConfigurations(){
        MenuConfigurations.menuBackgroundColor = ContextCompat.getColor(context, R.color.white)
        MenuConfigurations.iconTintColor = ContextCompat.getColor(context, R.color.grey_dark)
        MenuConfigurations.iconBackgroundTintColor = ContextCompat.getColor(context, R.color.grey_medium)
        MenuConfigurations.selectedIconTintColor = ContextCompat.getColor(context, R.color.white)
        MenuConfigurations.selectedIconBackgroundTintColor = ContextCompat.getColor(context, R.color.orange)
        MenuConfigurations.toggleButtonTintColor = ContextCompat.getColor(context, R.color.white)
        MenuConfigurations.toggleButtonBackgroundTintColor = ContextCompat.getColor(context, R.color.orange)
        MenuConfigurations.gridSpanCount = 2
        MenuConfigurations.gridBoxRadius = 6.0f

        val isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        MenuConfigurations.maxExpandWidth = if(isLandscape) screenWidth/3 else screenWidth/2
        MenuConfigurations.minExpandWidth = if(isLandscape) screenWidth/3 else screenWidth/2

        MenuConfigurations.collapseOnClickOutside = true
    }

    private fun getMenuItems(): MutableList<MenuItem>{
        return if(menu != null)
            menu!!.children.toMutableList()
        else
            mutableListOf()
    }

    private fun publishUI(menuStyle: Int){
        itemsAdapter = MenuItemsAdapter(itemsList = getMenuItems(), menuStyle = menuStyle)
        itemsRecyclerView.apply {
            layoutManager = when(menuStyle){
                        NavigationMenuStyle.Expanded.STYLE_GRID -> GridLayoutManager(context, MenuConfigurations.gridSpanCount)
                        else -> LinearLayoutManager(context)
                    }
            adapter = itemsAdapter
        }

        navLayout.minWidth = when(isExpanded){
            true -> MenuConfigurations.minExpandWidth
            false -> minimumWidth
        }

        // Change ivToggle position
        ivToggle.updateLayoutParams<ConstraintLayout.LayoutParams> {
            startToStart = if(isExpanded) ConstraintLayout.LayoutParams.UNSET else ConstraintLayout.LayoutParams.PARENT_ID
        }

        headerContainer.visibility = if(isExpanded) View.VISIBLE else View.GONE
    }

    private fun handleToggleMenu(){

        isExpanded = !isExpanded

        if(collapseMenuStyle == NavigationMenuStyle.Collapsed.STYLE_HIDDEN)
            motionLayout.visibility = View.VISIBLE
        when(collapseMenuStyle){
            NavigationMenuStyle.Collapsed.STYLE_HIDDEN -> {
                if(isExpanded) {
                    publishUI(expandMenuStyle)
                    showMenu()
                } else {
                    hideMenu()
                }
            }
            else -> {
                toggleMenuStyleTo(if(isExpanded) expandMenuStyle else collapseMenuStyle)
            }
        }
    }

    private fun toggleMenuStyleTo(menuStyle: Int){

        hideMenu()

        GlobalScope.launch (Dispatchers.IO){
            delay(motionLayout.getTransition(R.id.transition1).duration.toLong())
            withContext(Dispatchers.Main){
                publishUI(menuStyle)

                withContext(Dispatchers.IO){
                    delay(50)
                }
                showMenu()
            }
        }
    }

    private fun hideMenu(){
        val widthToTranslateOut = MenuConfigurations.maxExpandWidth
        motionLayout.getConstraintSet(R.id.end).setTranslationX(navLayout.id, -widthToTranslateOut.toFloat())
        motionLayout.transitionToEnd()

        if(::mainLayout.isInitialized){
            mainLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            mainLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToEnd = ConstraintLayout.LayoutParams.UNSET
            }
        }

    }

    private fun showMenu(){
        ivToggle.setImageDrawable(ContextCompat.getDrawable(context, getAppropriateToggleIcon()))
        motionLayout.transitionToStart()

        if(::mainLayout.isInitialized){
            mainLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            mainLayout.updateLayoutParams<ConstraintLayout.LayoutParams> {
                startToEnd = if(isExpanded) ConstraintLayout.LayoutParams.UNSET else root.id
            }
        }
    }

    private fun getAppropriateToggleIcon(): Int =
        when(isExpanded){

            true -> {
                when(collapseMenuStyle){
                    NavigationMenuStyle.Collapsed.STYLE_HIDDEN -> R.drawable.ic_arrow_back_24
                    else -> R.drawable.ic_menu_dots_24
                }
            }
            false -> {
                when(expandMenuStyle){
                    NavigationMenuStyle.Expanded.STYLE_STANDARD -> R.drawable.ic_menu_24
                    else -> R.drawable.ic_grid_view_24
                }
            }
        }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var widthSpec1 = widthSpec
        widthSpec1 = MeasureSpec.makeMeasureSpec(MenuConfigurations.maxExpandWidth, MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec1, heightSpec)
    }




    // ////////////////////////////////////////////////////
    // ****************** PUBLIC METHODS ******************
    // ////////////////////////////////////////////////////

    public fun setMenuBackgroundColor(color: Int){
        MenuConfigurations.menuBackgroundColor = color
        navLayout.backgroundTintList = ColorStateList.valueOf(MenuConfigurations.menuBackgroundColor)
    }

    public fun setOnItemClickListener(onMenuItemClickListener: OnMenuItemClickListener?){
        MenuConfigurations.onMenuItemClickListener = onMenuItemClickListener
    }

    public fun setHeader(header: View?){
        headerView = header
        headerContainer.removeAllViews()
        if(headerView != null){
            headerContainer.addView(headerView)
        }
    }

    public fun setMenu(menu: Menu?){
        this.menu = menu
        itemsAdapter.updateItems(getMenuItems())
    }

    public fun getMenu(): Menu? = this.menu


    public fun setExternalToggleView(view: View?){
        this.externalToggleView = view
        if(view == null)
            externalToggleView?.setOnClickListener(null)
        else
            externalToggleView?.setOnClickListener { handleToggleMenu() }
    }

    public fun setCollapseWhenClickOutside(isCollapse: Boolean){
        MenuConfigurations.collapseOnClickOutside = isCollapse
    }

    public fun setCollapseMenuStyle(collapseMenuStyle: Int){
        if(collapseMenuStyle in
            listOf(
                NavigationMenuStyle.Collapsed.STYLE_HIDDEN,
                NavigationMenuStyle.Collapsed.STYLE_ICONIFIED,
                NavigationMenuStyle.Collapsed.STYLE_ICONIFIED_NO_TITLE,
            ))
            this.collapseMenuStyle = collapseMenuStyle
    }

    public fun setExpandedMenuStyle(expandMenuStyle: Int){
        if(expandMenuStyle in
            listOf(
                NavigationMenuStyle.Expanded.STYLE_STANDARD,
                NavigationMenuStyle.Expanded.STYLE_GRID,
            ))
            this.expandMenuStyle = expandMenuStyle
    }


    public fun setGridSpanCount(spanCount: Int){
        MenuConfigurations.gridSpanCount = spanCount
    }

    public fun setGridBoxRadius(radius: Float){
        MenuConfigurations.gridBoxRadius = radius
    }

    public fun setIconTintColor(color: Int){
        MenuConfigurations.iconTintColor = color
    }

    public fun setIconBackgroundTintColor(color: Int){
        MenuConfigurations.iconBackgroundTintColor = color
    }

    public fun setSelectedIconTintColor(color: Int){
        MenuConfigurations.selectedIconTintColor = color
    }

    public fun setSelectedIconBackgroundTintColor(color: Int){
        MenuConfigurations.selectedIconBackgroundTintColor = color
    }

//    public fun setTextColor(color: Int){
//        Configuration.textColor = color
//    }
//
//    public fun setSelectedTextColor(color: Int){
//        Configuration.selectedTextColor = color
//    }

//    private fun setToggleButtonIcon(@SuppressLint("SupportAnnotationUsage") @DrawableRes drawable: Drawable){
//        Configuration.toggleButtonIcon = drawable
//    }

    public fun setToggleButtonTintColor(color: Int){
        MenuConfigurations.toggleButtonTintColor = color
        ivToggle.setColorFilter(MenuConfigurations.toggleButtonTintColor)
    }

    public fun setToggleButtonBackgroundTintColor(color: Int){
        MenuConfigurations.toggleButtonBackgroundTintColor = color
        ivToggle.backgroundTintList = ColorStateList.valueOf(MenuConfigurations.toggleButtonBackgroundTintColor)
    }

    public fun isExpanded() = isExpanded

    public fun toggleMenu(){
        handleToggleMenu()
    }
}