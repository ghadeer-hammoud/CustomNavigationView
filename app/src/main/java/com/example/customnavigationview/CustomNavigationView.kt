package com.example.customnavigationview

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.CustomMenuItem
import com.example.MenuItemsAdapter
import com.example.NavigationMenuStyle
import com.example.OnMenuItemClickListener
import com.example.customnavigationrailview.R
import kotlinx.coroutines.*

class CustomNavigationView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {

    companion object{
        private const val TAG = "CustomNavigationView"
    }

    private val typedArray  = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomNavView, 0, 0)
    private val menuRes = typedArray.getResourceId(R.styleable.CustomNavView_android_entries, 0)
    private var menuStyle = typedArray.getInt(R.styleable.CustomNavView_menuStyle, NavigationMenuStyle.STYLE_ICONIFIED)

    private val screenWidth: Int

    val demoItems = listOf(
        CustomMenuItem(0, "Transactions", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(1, "Payments", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(2, "Sales", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(3, "Transactions", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(4, "History", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(5, "Returns", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(6, "Reports", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(7, "Profile", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(8, "Item 1", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(9, "Item 2", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
        CustomMenuItem(10, "Item 3", ContextCompat.getDrawable(context, R.drawable.ic_home_24)!!),
    )

    private lateinit var root: View
    private lateinit var motionLayout: MotionLayout
    private lateinit var ivToggle: ImageView
    private lateinit var itemsRecyclerView: RecyclerView
    private lateinit var itemsAdapter: MenuItemsAdapter
    public var onMenuItemClickListener: OnMenuItemClickListener? = null

    private var isExpanded: Boolean = false
    private var isLargeScreen: Boolean = true
    private var maxExpandWidth: Int = 0

    init {
        screenWidth = context.resources.displayMetrics.widthPixels
        maxExpandWidth = screenWidth/2
        isLargeScreen = isLargeScreen()
        initView()
    }

    private fun initView(){
        val menu = MenuBuilder(context)
        //MenuInflater(context).inflate(menuRes, menu)

        root = inflate(context, R.layout.layout_nav_view, this)
        motionLayout = root.findViewById(R.id.motionLayout)
        itemsRecyclerView = root.findViewById(R.id.recyclerView)
        ivToggle = root.findViewById(R.id.ivToggle)

        itemsRecyclerView.apply {
            layoutManager = when(menuStyle){
                NavigationMenuStyle.STYLE_BOXES -> GridLayoutManager(context, 3)
                else                            -> LinearLayoutManager(context)
            }
            //itemsAdapter = MenuItemsAdapter(itemsList = menu.children.map { CustomMenuItem(it.itemId, it.title.toString(), it.icon) }.toList(), menuStyle = menuStyle)
            itemsAdapter = MenuItemsAdapter(itemsList = demoItems, menuStyle = menuStyle, onMenuItemClickListener)
            adapter = itemsAdapter
        }

        ivToggle.setOnClickListener{ onToggleButtonClicked() }

        typedArray.recycle()
    }

    public fun setOnItemClickListener(onMenuItemClickListener: OnMenuItemClickListener?){
        this.onMenuItemClickListener = onMenuItemClickListener
        itemsAdapter.setOnItemClickListener(onMenuItemClickListener)
    }

    private fun onToggleButtonClicked(){

        isExpanded = !isExpanded
        ivToggle.setImageDrawable(ContextCompat.getDrawable(context, if(isExpanded) R.drawable.ic_arrow_back_24 else R.drawable.ic_menu_24))

        when(isExpanded){
            // Switch from iconified to standard/boxes
            true -> {
                when(isLargeScreen){
                    true -> {
                        menuStyle = NavigationMenuStyle.STYLE_BOXES
                        toggleMenuStyleTo(menuStyle)
                    }
                    false -> {
                        menuStyle = NavigationMenuStyle.STYLE_STANDARD
                        toggleMenuStyleTo(menuStyle)
                    }
                }
            }
            // Switch from standard/boxes to iconified
            false -> {
                menuStyle = NavigationMenuStyle.STYLE_ICONIFIED_NO_TITLE
                toggleMenuStyleTo(menuStyle)
            }
        }
    }

    private fun toggleMenuStyleTo(menuStyle: Int){

        val navLayout = motionLayout.findViewById<ConstraintLayout>(R.id.navLayout)
        val widthToTranslateOut = screenWidth/2 //navLayout.width
        motionLayout.getConstraintSet(R.id.end).setTranslationX(navLayout.id, -widthToTranslateOut.toFloat())

        motionLayout.transitionToEnd()
        itemsAdapter = MenuItemsAdapter(itemsList = demoItems, menuStyle = menuStyle, onMenuItemClickListener)
        GlobalScope.launch (Dispatchers.IO){

            delay(motionLayout.getTransition(R.id.transition1).duration.toLong())
            withContext(Dispatchers.Main){
                itemsRecyclerView.apply {
                    layoutManager = when(menuStyle){
                        NavigationMenuStyle.STYLE_BOXES -> GridLayoutManager(context, 3)
                        else -> LinearLayoutManager(context)

                    }
                    adapter = itemsAdapter
                }

                withContext(Dispatchers.IO){
                    delay(50)
                }
                motionLayout.transitionToStart()
            }
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        var widthSpec = widthSpec
        widthSpec = MeasureSpec.makeMeasureSpec(maxExpandWidth, MeasureSpec.AT_MOST)
        super.onMeasure(widthSpec, heightSpec)
    }

    private fun isLargeScreen(): Boolean{
        return context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }


    fun dpFromPx(px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun pxFromDp(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}