package ru.hse.project.ecoapp.ui.main.map

import android.R.attr
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.tabs.TabLayout
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.BackPressedForFragments
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.model.FilterModel
import ru.hse.project.ecoapp.model.PlaceMark
import ru.hse.project.ecoapp.model.TrashTypes
import ru.hse.project.ecoapp.ui.main.map.addMarker.DialogFragmentAddMarker
import ru.hse.project.ecoapp.ui.main.map.marker.MarkerTypesAdapter
import ru.hse.project.ecoapp.ui.main.map.search.SearchViewModel
import ru.hse.project.ecoapp.util.ViewModelFactory
import ru.hse.project.ecoapp.util.ViewUtils
import java.math.RoundingMode


class MapFragment : Fragment(), BackPressedForFragments {

    private lateinit var root: View
    private lateinit var mapWrapper: YandexMapWrapper
    private lateinit var anchorView: View
    private lateinit var btnShowLocation: ConstraintLayout
    private lateinit var btnSearch: ConstraintLayout
    private lateinit var btnAddPoint: ConstraintLayout
    private lateinit var btnShowFilter: ConstraintLayout
    private lateinit var progressBarLocation: ProgressBar
    private lateinit var imageShowLocation: ImageView
    private lateinit var bottomSheetBehaviorSearch: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetBehaviorDetailsMarker: BottomSheetBehavior<ConstraintLayout>
    private lateinit var tabListenerOnMap: InputListener
    private val picasso = App.getComponent().getPicassoHttps()
    private var isFilterShowed: Boolean = false
    private var isAddingMarker = false
    private val repository = App.getComponent().getRepository()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        root = inflater.inflate(R.layout.fragment_map, container, false)
        bottomSheetBehaviorSearch =
            BottomSheetBehavior.from(root.findViewById(R.id.fragment_map_bottom_sheet_search))
        bottomSheetBehaviorDetailsMarker =
            BottomSheetBehavior.from(root.findViewById(R.id.fragment_map_bottom_sheet_details_marker))
        anchorView = root.findViewById(R.id.anchorForButtons)
        btnShowLocation = root.findViewById(R.id.map_btn_show_location)
        btnAddPoint = root.findViewById(R.id.map_btn_add_point)
        btnSearch = root.findViewById(R.id.map_btn_search)
        btnShowFilter = root.findViewById(R.id.map_btn_filter)
        progressBarLocation = root.findViewById(R.id.progress_bar_location)
        imageShowLocation = root.findViewById(R.id.image_show_location)

        val mapView = root.findViewById<MapView>(R.id.mapview)
        mapWrapper = YandexMapWrapper(mapView)
        createViewSearchContent()
        createViewMarkerContent()



        tabListenerOnMap = object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_HIDDEN
                bottomSheetBehaviorSearch.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            override fun onMapLongTap(p0: Map, p1: Point) {}

        }
        mapView.map.addInputListener(tabListenerOnMap)


        btnShowLocation.setOnClickListener {
            if (mapWrapper.isLocationShowed) {
                mapWrapper.showUserLocation()
            } else {
                val locationManager: LocationManager =
                    requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                } else {

                    imageShowLocation.visibility = View.INVISIBLE
                    progressBarLocation.visibility = View.VISIBLE
                    mapWrapper.initializeUserLocation().addOnSuccessListener {
                        imageShowLocation.visibility = View.VISIBLE
                        progressBarLocation.visibility = View.INVISIBLE
                    }
                }
            }
        }

        btnSearch.setOnClickListener {
            ViewUtils.bottomNavigationHidden(requireActivity(), 150)
            if (bottomSheetBehaviorDetailsMarker.state == BottomSheetBehavior.STATE_HIDDEN) {
                ViewUtils.updateAnchor(anchorView, R.id.fragment_map_bottom_sheet_search)
            }
            bottomSheetBehaviorSearch.state = BottomSheetBehavior.STATE_EXPANDED
        }


        btnAddPoint.setOnClickListener {
            mapWrapper.startPickNewMarker()
            isAddingMarker = true
            hiddenAllViews()
            val btnConfirmPosition =
                root.findViewById<ConstraintLayout>(R.id.btn_confirm_posittion_add_marker)
            btnConfirmPosition.setOnClickListener {
                // возвращаем все обратно
                // получаем координаты
                val point = mapWrapper.endPickNewMarker()
                isAddingMarker = false
                showAllViews()

                DialogFragmentAddMarker(point).show(parentFragmentManager, "ADD_FRAG")
            }
        }

        val filterModelPaper =
            FilterModel(isWhat = TrashTypes.PAPER, color = R.color.color_filter_paper)
        val filterModelGlass =
            FilterModel(isWhat = TrashTypes.GLASS, color = R.color.color_filter_glass)
        val filterModelPlastic =
            FilterModel(isWhat = TrashTypes.PLASTIC, color = R.color.color_filter_plastic)
        val filterModelMetal =
            FilterModel(isWhat = TrashTypes.METAL, color = R.color.color_filter_metal)


        btnShowFilter.setOnClickListener {
            val imageBtnFilters = root.findViewById<ImageView>(R.id.map_image_btn_show_filters)
            val layFilterButtons = root.findViewById<ConstraintLayout>(R.id.filter_buttons)


            layFilterButtons.animate().setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(p0: Animator?) {
                    if (!isFilterShowed) {
                        layFilterButtons.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(p0: Animator?) {
                    if (isFilterShowed) {
                        layFilterButtons.visibility = View.GONE
                    }
                    isFilterShowed = !isFilterShowed
                }

                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationRepeat(p0: Animator?) {}
            })


            if (isFilterShowed) {
                layFilterButtons.animate()
                    .translationY((layFilterButtons.height).toFloat()).duration = 300
                imageBtnFilters.animate().rotation(0F).duration = 300
            } else {
                layFilterButtons.animate()
                    .translationY(0F).duration = 300
                imageBtnFilters.animate().rotation(180F).duration = 300
            }


            val filters = arrayListOf(
                filterModelPaper,
                filterModelGlass,
                filterModelPlastic,
                filterModelMetal
            )

            val btnPaperFilter = root.findViewById<ConstraintLayout>(R.id.btn_filter_paper)
            val btnGlassFilter = root.findViewById<ConstraintLayout>(R.id.btn_filter_glass)
            val btnPlasticFilter = root.findViewById<ConstraintLayout>(R.id.btn_filter_plastic)
            val btnMetalFilter = root.findViewById<ConstraintLayout>(R.id.btn_filter_metal)

            val paperView = root.findViewById<CardView>(R.id.cardview_filter_paper)
            val glassView = root.findViewById<CardView>(R.id.cardview_filter_glass)
            val plasticView = root.findViewById<CardView>(R.id.cardview_filter_plastic)
            val metalView = root.findViewById<CardView>(R.id.cardview_filter_metal)

            btnPaperFilter.setOnClickListener(
                clickListenerOnTypeFilter(
                    paperView,
                    filterModelPaper,
                    filters
                )
            )
            btnGlassFilter.setOnClickListener(
                clickListenerOnTypeFilter(
                    glassView,
                    filterModelGlass,
                    filters
                )
            )
            btnPlasticFilter.setOnClickListener(
                clickListenerOnTypeFilter(
                    plasticView,
                    filterModelPlastic,
                    filters
                )
            )
            btnMetalFilter.setOnClickListener(
                clickListenerOnTypeFilter(
                    metalView,
                    filterModelMetal,
                    filters
                )
            )
        }


        return root
    }


    private fun clickListenerOnTypeFilter(
        view: CardView,
        filterModel: FilterModel,
        filters: List<FilterModel>
    ): View.OnClickListener {

        return View.OnClickListener {
            filterModel.isChecked = !filterModel.isChecked
            bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_HIDDEN
            if (!filterModel.isChecked) {
                //disable
                view.setCardBackgroundColor(requireContext().resources.getColor(R.color.default_gray_filter))
                mapWrapper.updateMapWithFilter(filters)
            } else {
                //enable
                view.setCardBackgroundColor(requireContext().resources.getColor(filterModel.color))
                mapWrapper.updateMapWithFilter(filters)
            }
        }
    }


    private fun createViewSearchContent() {
        val searchView = root.findViewById<SearchView>(R.id.sheet_search_search_view)
        val searchRecycler = root.findViewById<RecyclerView>(R.id.sheet_search_recycler)
        val searchTab = root.findViewById<TabLayout>(R.id.sheet_search_tab)
        val viewModel = ViewModelProvider(this, ViewModelFactory(requireContext()))
            .get(SearchViewModel::class.java)
        viewModel.setContext(requireContext())


        val defaultSearchViewState = {
            // searchView.setQuery(null, false)
            searchView.isIconified = true
            searchView.isIconified = true

        }
        // обработчик нажатия на search view
        searchView.setOnClickListener { searchView.isIconified = false }

        // обработчик ввода текста в search view
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(text: String): Boolean = false

            override fun onQueryTextChange(text: String): Boolean {
                viewModel.changeSearchText(text)
                return false
            }
        })

        // установка клика на поле
        viewModel.setClickListener { x: Double, y: Double ->
            bottomSheetBehaviorSearch.state = BottomSheetBehavior.STATE_COLLAPSED
            mapWrapper.moveAnimatedTo(x, y, 16F)
            bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.setOnClickHiddenBtnListener { id: String ->
            return@setOnClickHiddenBtnListener mapWrapper.changeMarkerOnMap(
                id
            )
        }
        // установка действующего адаптера
        searchRecycler.adapter = viewModel.getActiveAdapter()


        // изменение действвующей вскладки
        // устанавливаем другой адаптер
        viewModel.changePage.observe(viewLifecycleOwner, Observer {
            val onPageChanged = it ?: return@Observer
            searchRecycler.adapter = onPageChanged
        })

        // обработка нажатия на другую вкладку
        searchTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                // Устанавливаем поиск в изначальное положение
                defaultSearchViewState.invoke()
                // вызываем собыбытие смены страницы
                viewModel.changePage()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


        // обработка поведения нижнего экрана
        bottomSheetBehaviorSearch.addBottomSheetCallback(
            object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        defaultSearchViewState.invoke()
                        if (bottomSheetBehaviorDetailsMarker.state == BottomSheetBehavior.STATE_HIDDEN) {
                            if (!isAddingMarker) {
                                ViewUtils.bottomNavigationShowed(requireActivity(), 100)
                            }
                        }
                    }

                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        viewModel.bottomViewStateExpanded()
                        root.findViewById<AppBarLayout>(R.id.appBarSearch).setExpanded(true)
                    }

                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })


    }


    private fun createViewMarkerContent() {
        // переводим экран в режим скрытия для маркеров
        bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_HIDDEN
        // обработка поведения всплывающего меню для маркеров
        bottomSheetBehaviorDetailsMarker.addBottomSheetCallback(object : BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        if (!isAddingMarker) {
                            ViewUtils.bottomNavigationShowed(
                                requireActivity(),
                                100
                            )
                        }
                        mapWrapper.closedMarker()
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> root.findViewById<AppBarLayout>(R.id.appBarMarker)
                        .setExpanded(true)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        // обработка нажатия на маркер
        mapWrapper.setTapListener { obj, point ->
            mapWrapper.openMarker(obj as PlacemarkMapObject)

            // показываем всплявающее меню
            bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_COLLAPSED
            // Перепривязываем якорь
            val p = anchorView.layoutParams as CoordinatorLayout.LayoutParams
            p.anchorId = R.id.fragment_map_bottom_sheet_details_marker
            anchorView.layoutParams = p
            // Убираем нижнее меню
            ViewUtils.bottomNavigationHidden(requireActivity(), 150)

            val title = root.findViewById<TextView>(R.id.marker_details_title)
            val recyclerTypes = root.findViewById<RecyclerView>(R.id.recycler_types_marker)
            val address = root.findViewById<TextView>(R.id.marker_details_address)
            val distance = root.findViewById<TextView>(R.id.marker_distance_to_marker)
            val image = root.findViewById<ImageView>(R.id.marker_details_picture)
            val btnFavorite = root.findViewById<ConstraintLayout>(R.id.marker_details_btn_favorite)
            val btnFavoriteImage =
                root.findViewById<ImageView>(R.id.marker_details_btn_favorite_image)
            val btnFavoriteText = root.findViewById<TextView>(R.id.marker_details_btn_favorite_text)
            val btnWasHere = root.findViewById<ConstraintLayout>(R.id.marker_details_btn_i_was_here)

            // снаряжаем view даттой
            val data = obj.userData as PlaceMark
            title.text = data.title
            address.text = data.address
            picasso.load(data.image).fit().centerCrop().error(R.drawable.image_holder).into(image)

            // определяем какие типы можно утилизировать в данном месте
            val recyclerDataType = arrayListOf<Int>()
            val recyclerDataColor = arrayListOf<Int>()
            if (data.isPaper) {
                recyclerDataType.add(R.string.type_paper)
                recyclerDataColor.add(R.color.color_type_marker_paper)
            }
            if (data.isGlass) {
                recyclerDataType.add(R.string.type_glass)
                recyclerDataColor.add(R.color.color_type_marker_glass)
            }
            if (data.isPlastic) {
                recyclerDataType.add(R.string.type_plastic)
                recyclerDataColor.add(R.color.color_type_marker_plastic)
            }
            if (data.isMetal) {
                recyclerDataType.add(R.string.type_metal)
                recyclerDataColor.add(R.color.color_type_marker_metal)
            }
            // подключаем adapter
            recyclerTypes.adapter = MarkerTypesAdapter(recyclerDataType, recyclerDataColor)


            if (data.isFavorite) {
                btnFavoriteText.setText(R.string.btn_is_favorite)
                btnFavoriteImage.setImageResource(R.drawable.icon_fav)
            } else {
                btnFavoriteImage.setImageResource(R.drawable.icon_non_fav)
                btnFavoriteText.setText(R.string.btn_is_not_favorite)
            }

            // кнопка добавить в избранное
            btnFavorite.setOnClickListener {
                if (data.isFavorite) {
                    btnFavoriteImage.setImageResource(R.drawable.icon_non_fav)
                    btnFavoriteText.setText(R.string.btn_is_not_favorite)
                } else {
                    btnFavoriteImage.setImageResource(R.drawable.icon_fav)
                    btnFavoriteText.setText(R.string.btn_is_favorite)

                }
                mapWrapper.changeMarkerOnMap(obj)
            }

            val distanceValue: Double = mapWrapper.getDistanceMetersToMarker(point)

            when {
                distanceValue / 1000 >= 1.0 -> {
                    val formattedValue =
                        (distanceValue / 1000.0).toBigDecimal().setScale(1, RoundingMode.UP)
                            .toDouble()
                    distance.text =
                        formattedValue.toString() + " " + requireContext().resources.getString(R.string.distance_kilometers)
                }
                distanceValue != 0.0 -> {
                    val formattedValue = distanceValue.toInt()
                    distance.text =
                        formattedValue.toString() + " " + requireContext().resources.getString(R.string.distance_meters)

                }
                else -> {
                    distance.text =
                        "- " + requireContext().resources.getString(R.string.distance_kilometers)
                }
            }

            btnWasHere.setOnClickListener {
                if (distanceValue != 0.0 && distanceValue < MAX_DISTANCE_TO_MARKER) {
                    repository.user?.updateUserRating(data.id)?.addOnSuccessListener {
                        Toast.makeText(
                            context,
                            requireContext().resources.getString(R.string.successful_visited_marker),
                            Toast.LENGTH_SHORT
                        ).show()
                    }?.addOnFailureListener {
                        Toast.makeText(
                            context,
                            requireContext().resources.getString(R.string.place_was_visited_earlier),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        requireContext().resources.getString(R.string.error_user_not_near_marker),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            true
        }
    }


    private fun hiddenAllViews() {
        bottomSheetBehaviorSearch.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_HIDDEN
        root.findViewById<CardView>(R.id.cardview_filter).apply {
            this.animate().translationX(this.width * -1.2F).duration = DURATION_HIDDEN_ALL_VIEWS
        }

        root.findViewById<ConstraintLayout>(R.id.filter_buttons).apply {
            this.animate().translationX(this.width * -1.2F).duration = DURATION_HIDDEN_ALL_VIEWS
        }

        root.findViewById<CardView>(R.id.cardview_add_point).apply {
            this.animate().translationX(this.width * 1.2F).duration = DURATION_HIDDEN_ALL_VIEWS

        }
        root.findViewById<CardView>(R.id.cardview_show_location).apply {
            this.animate().translationX(this.width * 1.2F).duration = DURATION_HIDDEN_ALL_VIEWS

        }
        root.findViewById<CardView>(R.id.cardview_search).apply {
            this.animate().translationX(this.width * 1.2F).duration = DURATION_HIDDEN_ALL_VIEWS

        }
        root.findViewById<CardView>(R.id.cardview_confitm_position_add_marker).apply {
            this.animate().translationY(0F).duration = DURATION_HIDDEN_ALL_VIEWS
        }

        ViewUtils.bottomNavigationHidden(requireActivity(), DURATION_HIDDEN_ALL_VIEWS)


    }

    private fun showAllViews() {
        root.findViewById<CardView>(R.id.cardview_filter).apply {
            this.animate().translationX(0F).duration = DURATION_SHOWED_ALL_VIEWS
        }

        root.findViewById<ConstraintLayout>(R.id.filter_buttons).apply {
            this.animate().translationX(0F).duration = DURATION_SHOWED_ALL_VIEWS
        }

        root.findViewById<CardView>(R.id.cardview_add_point).apply {
            this.animate().translationX(0F).duration = DURATION_SHOWED_ALL_VIEWS

        }
        root.findViewById<CardView>(R.id.cardview_show_location).apply {
            this.animate().translationX(0F).duration = DURATION_SHOWED_ALL_VIEWS

        }
        root.findViewById<CardView>(R.id.cardview_search).apply {
            this.animate().translationX(0F).duration = DURATION_SHOWED_ALL_VIEWS

        }
        root.findViewById<CardView>(R.id.cardview_confitm_position_add_marker).apply {
            this.animate().translationY(this.height * -2F).duration = DURATION_SHOWED_ALL_VIEWS
        }

        ViewUtils.bottomNavigationShowed(requireActivity(), DURATION_SHOWED_ALL_VIEWS)
    }


    override fun onStart() {
        mapWrapper.onStart()
        super.onStart()
    }

    override fun onStop() {
        mapWrapper.onStop()
        super.onStop()
    }

    override fun onBackPressed(): Boolean {

        return when {
            bottomSheetBehaviorSearch.state == BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheetBehaviorSearch.state = BottomSheetBehavior.STATE_COLLAPSED
                false
            }
            bottomSheetBehaviorDetailsMarker.state == BottomSheetBehavior.STATE_EXPANDED -> {
                bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_COLLAPSED
                false
            }
            bottomSheetBehaviorDetailsMarker.state == BottomSheetBehavior.STATE_COLLAPSED -> {
                bottomSheetBehaviorDetailsMarker.state = BottomSheetBehavior.STATE_HIDDEN
                false
            }

            isAddingMarker -> {
                mapWrapper.endPickNewMarker()
                showAllViews()
                isAddingMarker = false
                false
            }
            else -> {
                true
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val x = 10

    }

    companion object {
        const val MAX_DISTANCE_TO_MARKER: Double = 20.0
        const val DURATION_HIDDEN_ALL_VIEWS: Long = 300
        const val DURATION_SHOWED_ALL_VIEWS: Long = 300

    }
}