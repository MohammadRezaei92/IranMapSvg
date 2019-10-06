package rezaei.mohammad.iranmap

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.richpath.RichPath
import com.richpath.RichPathView
import com.richpathanimator.RichPathAnimator

/**
 * This view shows an Iran map svg that able to animate and interact with user touches.
 * @author Mohammad Rezaei
 * @see <a href="https://github.com/MohammadRezaei92">Github</a>
 */
class IranMapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**
     * Default background color of provinces
     */
    var provinceBackgroundColor = Color.BLACK
    /**
     * Background color of provinces when they are active
     */
    var provinceActiveColor = Color.CYAN
    /**
     * Stroke color of provinces
     */
    var provinceStrokeColor = Color.WHITE
    /**
     * Make provinces clickable
     */
    var provinceSelectByClick = true
        set(value) {
            field = value
            if (value)
                iranPath.setOnPathClickListener {
                    activeProvince(it, withAnimate = true)
                } else {
                iranPath.setOnPathClickListener(null)
            }
        }
    /**
     * If true multi province can be selected
     * If false on province can be selected
     */
    var provinceCanMultiSelect = false
    /**
     * Animation duration for map animations and provinces animations
     */
    var mapAnimationDuration = 200L
    /**
     * If true map's provinces appear with an animation in first time
     */
    var mapAppearWithAnimation = false
    var mapAdjustViewBound = false
    private val paint = Paint()
    private lateinit var surfaceView: SurfaceView
    private lateinit var iranPath: RichPathView

    /**
     * Give list of selected provinces. see[Province]
     */
    var selectedProvinces: MutableList<Province> = mutableListOf()
    private val titles: MutableMap<RichPath?, String?> = mutableMapOf()

    init {
        addIranPathView()
        addSurfaceView()
        handleAttr(context, attrs)
    }

    private fun addIranPathView() {
        //Show a preview of map in edit mode
        if(isInEditMode){
            val imageView = ImageView(context)
            imageView.layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT
            )
            imageView.setImageResource(R.drawable.iran)
            imageView.adjustViewBounds = mapAdjustViewBound
            addView(imageView)
            return
        }
        //Add rich path view for show map svg
        iranPath = RichPathView(context)
        iranPath.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT
            , ViewGroup.LayoutParams.MATCH_PARENT
        )
        iranPath.adjustViewBounds = mapAdjustViewBound
        iranPath.setVectorDrawable(R.drawable.iran)
        addView(iranPath)
    }

    private fun addSurfaceView() {
        //Add surface view for draw titles on map
        surfaceView = SurfaceView(context)
        surfaceView.layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT
            , ViewGroup.LayoutParams.MATCH_PARENT
        )
        addView(surfaceView)
    }

    private fun handleAttr(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.IranMapView, 0, 0)

        try {
            provinceBackgroundColor =
                typedArray.getColor(R.styleable.IranMapView_imProvinceBackgroundColor, Color.BLACK)
            provinceActiveColor = typedArray.getColor(
                R.styleable.IranMapView_imProvinceActiveBackgroundColor,
                Color.CYAN
            )
            provinceStrokeColor =
                typedArray.getColor(R.styleable.IranMapView_imProvinceStrokeColor, Color.WHITE)
            provinceSelectByClick =
                typedArray.getBoolean(R.styleable.IranMapView_imProvinceSelectByClick, true)
            provinceCanMultiSelect =
                typedArray.getBoolean(R.styleable.IranMapView_imProvinceMultiSelect, false)
            mapAnimationDuration =
                typedArray.getInt(R.styleable.IranMapView_imAnimationDuration, 200).toLong()
            mapAppearWithAnimation =
                typedArray.getBoolean(R.styleable.IranMapView_imMapAppearWithAnimation, false)
            mapAdjustViewBound = typedArray.getBoolean(R.styleable.IranMapView_imAdjustViewBound, false)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            typedArray.recycle()
        }

    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initMap()
    }

    private fun initMap() {
        //Apply default attrs to all provinces
        iranPath.findAllRichPaths().forEach {
            it.fillColor = provinceBackgroundColor
            it.strokeColor = provinceStrokeColor
            //Ready provinces for play animation
            if (mapAppearWithAnimation) {
                it.scaleX = 5f
                it.scaleY = 5f
                it.fillAlpha = 0f
                it.strokeAlpha = 0f
            }
        }
        //Appear provinces with Scale,Alpha animation
        if (mapAppearWithAnimation)
            Province.values().toMutableList().shuffled().forEachIndexed { index, province ->
                RichPathAnimator.animate(iranPath.findRichPathByName(province.name))
                    .startDelay(25.times(index).toLong())
                    .interpolator(DecelerateInterpolator())
                    .scale(1f)
                    .fillAlpha(1f)
                    .strokeAlpha(1f)
                    .duration(mapAnimationDuration)
                    .start()
            }

    }

    /**
     * َActivate a province by given properties
     * @param [province] enum of iran provinces. see[Province]
     * @param [withBackgroundColor] background color of province in active mode
     * @param [withStrokeColor] stroke color of province in active mode
     * @param [withAnimate] activate with animation
     * @throws EnumConstantNotPresentException if province not found
     */
    @JvmOverloads
    fun activeProvince(
        province: Province
        , withBackgroundColor: Int? = null
        , withStrokeColor: Int? = null
        , withAnimate: Boolean = false
    ) {

        val provincePath = iranPath.findRichPathByName(province.name)
        activeProvince(provincePath, withBackgroundColor, withStrokeColor, withAnimate)

    }

    /**
     * Deactivate given province
     * @param [province] enum of iran provinces. see[Province]
     * @param [withAnimate] activate with animation
     * @throws EnumConstantNotPresentException if province not found
     */
    @JvmOverloads
    fun deActiveProvince(province: Province, withAnimate: Boolean = false) {
        val provincePath = iranPath.findRichPathByName(province.name)
        deActiveProvince(provincePath, withAnimate)
    }

    private fun activeProvince(
        provincePath: RichPath?
        , withBackgroundColor: Int? = null
        , withStrokeColor: Int? = null
        , withAnimate: Boolean = false
    ) {

        //Deactivate selected provinces in single select mode
        if (!provinceCanMultiSelect)
            Province.values().filter { it.name != provincePath?.name }.forEach {
                deActiveProvince(it)
            }

        provincePath?.let {
            //If province is active now, deactivate it.
            if (selectedProvinces.contains(Province.valueOf(it.name))) {
                deActiveProvince(it, withAnimate)
            } else {//Activate province
                RichPathAnimator.animate(it)
                    .interpolator(AccelerateDecelerateInterpolator())
                    .duration(if (withAnimate) mapAnimationDuration else 0)
                    .scale(1.1f, 1f)
                    .fillColor(it.fillColor, withBackgroundColor ?: provinceActiveColor)
                    .strokeColor(it.strokeColor, withStrokeColor ?: provinceStrokeColor)
                    .start()
                selectedProvinces.add(Province.valueOf(it.name))
            }
        } ?: kotlin.run {
            throw EnumConstantNotPresentException(Province::class.java,"Province not found.")
        }
    }

    private fun deActiveProvince(provincePath: RichPath?, withAnimate: Boolean = false) {
        provincePath?.let {
            RichPathAnimator.animate(it)
                .interpolator(AccelerateDecelerateInterpolator())
                .duration(if (withAnimate) mapAnimationDuration else 0)
                .scale(1.1f, 1f)
                .fillColor(provinceBackgroundColor)
                .strokeColor(provinceStrokeColor)
                .start()
            selectedProvinces.remove(Province.valueOf(it.name))
        } ?: kotlin.run {
            throw EnumConstantNotPresentException(Province::class.java,"Province not found.")
        }
    }

    /**
     * Add a title to a province by given properties
     * @param [province] enum of iran provinces. see[Province]
     * @param [title] text you want to add to province
     * @param [typeface] type face of title
     * @param [textColor] text color of title
     */
    fun addTitle(
        province: Province,
        title: String?,
        typeface: Typeface = Typeface.DEFAULT,
        textColor: Int? = null
    ) {
        if (title == null)
            return

        //Init paint for draw title
        paint.color = textColor ?: Color.BLACK
        paint.textSize = 30f
        paint.typeface = typeface

        val provincePath = iranPath.findRichPathByName(province.name)
        //Add title to title list if not exist
        if (!titles.contains(provincePath))
            titles[provincePath] = title

        //Refresh view to draw new titles
        surfaceView.invalidate()

    }

    /**
     * Remove title from a province
     * @param [province] enum of iran provinces. see[Province]
     */
    fun removeTitle(province: Province) {
        val provincePath = iranPath.findRichPathByName(province.name)
        titles.remove(provincePath)

        surfaceView.invalidate()

    }

    inner class SurfaceView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
    ) : View(context, attrs, defStyleAttr) {
        init {
            //Force layout to call onDraw method
            setWillNotDraw(false)
        }

        @SuppressLint("DrawAllocation")
        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            titles.forEach {
                val provinceBounds = RectF()
                val textBounds = Rect()
                //Find title bounds
                paint.getTextBounds(it.value ?: "", 0, it.value?.length ?: 0, textBounds)
                //Find province bounds
                it.key?.computeBounds(provinceBounds, true)
                //Draw text on center of province
                canvas?.drawText(
                    it.value ?: ""
                    , provinceBounds.centerX().minus(textBounds.width().div(2))
                    , provinceBounds.centerY(), paint
                )

            }
        }
    }

}