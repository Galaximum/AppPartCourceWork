package ru.hse.project.ecoapp.ui.main.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import ru.hse.project.ecoapp.App
import ru.hse.project.ecoapp.R
import ru.hse.project.ecoapp.R.color
import ru.hse.project.ecoapp.R.drawable
import ru.hse.project.ecoapp.model.FilterModel
import ru.hse.project.ecoapp.model.LastPosition
import ru.hse.project.ecoapp.model.PlaceMark
import ru.hse.project.ecoapp.util.ImageUtils
import kotlin.math.cos
import kotlin.math.sqrt


class YandexMapWrapper(private val mapView: MapView) {
    private val repository = App.getComponent().getRepository()
    private val context = mapView.context
    private val collection = mapView.map.mapObjects.addClusterizedPlacemarkCollection {}
    private lateinit var dataMarkers: Set<PlaceMark>
    private var markerTapListener: MapObjectTapListener? = null
    private val userLocation =
        MapKitFactory.getInstance().createUserLocationLayer(mapView.mapWindow)
    private val locationManager = MapKitFactory.getInstance().createLocationManager()
    var isLocationShowed = false
    private var previousMarker: PlacemarkMapObject? = null
    private var pickerMarker: PlacemarkMapObject? = null
    private var pickerMarkerDraggListener: CameraListener? = null

    init {
        mapView.map.isRotateGesturesEnabled = true
        repository.initPlaceMarksResult.addOnSuccessListener {
            dataMarkers = it
            this.addDataOnMap(it)
        }
    }

    fun onStart() {
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
        //перемещаем карту
        val lastPosition = repository.user!!.lastPosition
        moveAnimatedTo(
            lastPosition.latitude, lastPosition.longtitude, lastPosition.zoom,
            animation = Animation(Animation.Type.SMOOTH, 0F)
        )
    }

    fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()

        val newPosition = mapView.map.cameraPosition
        repository.user?.updateLastPosition(
            LastPosition(
                newPosition.target.latitude,
                newPosition.target.longitude,
                newPosition.zoom
            )
        )
    }


    fun setTapListener(listener: MapObjectTapListener) {
        markerTapListener = listener
    }

    fun initializeUserLocation(): Task<Void> {
        userLocation.isVisible = true
        userLocation.isHeadingEnabled = true

        userLocation.setObjectListener(object : UserLocationObjectListener {
            override fun onObjectUpdated(userLocationView: UserLocationView, event: ObjectEvent) {}
            override fun onObjectRemoved(p0: UserLocationView) {}

            override fun onObjectAdded(userLocationView: UserLocationView) {
                val bitmap = ImageUtils.getBitmap(context, R.drawable.user_lock)
                val provider =
                    ImageProvider.fromBitmap(Bitmap.createScaledBitmap(bitmap, 50, 50, false))
                val style = IconStyle().setAnchor(PointF(0.5f, 0.5f))
                    .setRotationType(RotationType.NO_ROTATION)
                    .setZIndex(0f)
                userLocationView.arrow.setIcon(provider, style)
                userLocationView.pin.setIcon(provider, style)

                userLocationView.accuracyCircle.fillColor =
                    context.resources.getColor(color.accuracy_circle_color)
            }
        })

        val task = TaskCompletionSource<Void>()

        locationManager.requestSingleUpdate(object : LocationListener {
            override fun onLocationStatusUpdated(p0: LocationStatus) {
                if (p0 == LocationStatus.NOT_AVAILABLE) {
                    task.setException(Exception(""))
                }
            }

            override fun onLocationUpdated(p0: Location) {
                mapView.map.move(
                    CameraPosition(p0.position, 17F, 0F, 0F),
                    Animation(Animation.Type.LINEAR, 1F),
                    null
                )
                isLocationShowed = true
                task.setResult(null)
            }
        })

        return task.task
    }

    fun showUserLocation() {
        mapView.map.move(
            CameraPosition(userLocation.cameraPosition()!!.target, 17F, 0F, 0F),
            Animation(Animation.Type.LINEAR, 1F),
            null
        )
    }

    fun moveAnimatedTo(
        latitude: Double,
        longitude: Double,
        zoom: Float = DEFAULT_ZOOM,
        azimuth: Float = 0F,
        tilt: Float = 0F,
        animation: Animation = Animation(Animation.Type.LINEAR, 1F),
        callback: com.yandex.mapkit.map.Map.CameraCallback? = null

    ) {
        mapView.map.move(
            CameraPosition(Point(latitude, longitude), zoom, azimuth, tilt),
            animation,
            callback
        )
    }

    private fun addDataOnMap(
        data: Set<PlaceMark>
    ) {

        val bitmapMain = Bitmap.createScaledBitmap(
            ImageUtils.getBitmap(context, drawable.trash_marker_icon),
            WIDTH_BITMAP_MARKER,
            HEIGHT_BITMAP_MARKER,
            false
        )

        val bitmapFavorite = Bitmap.createScaledBitmap(
            ImageUtils.getBitmap(context, drawable.trash_favorite_marker_icon),
            WIDTH_BITMAP_MARKER,
            HEIGHT_BITMAP_MARKER,
            false
        )

        val style = IconStyle().setAnchor(PointF(0.5f, 0.5f))
            .setRotationType(RotationType.NO_ROTATION)
            .setZIndex(0f)


        val collectionOfMarkers = collection.addPlacemarks(data.map { x ->
            Point(x.latitude, x.longitude)
        }, ImageProvider.fromBitmap(bitmapMain), style)

        for (i in data.indices) {
            (collectionOfMarkers[i] as PlacemarkMapObject).apply {
                this.userData = data.elementAt(i)
                if (data.elementAt(i).isFavorite) {
                    this.setIcon(ImageProvider.fromBitmap(bitmapFavorite))
                }
                markerTapListener?.let { this.addTapListener(it) }
            }
        }

        collection.userData = collectionOfMarkers
        // отображаем все маркеры
        collection.clusterPlacemarks(20.0, 17)
    }


    fun updateMapWithFilter(filers: List<FilterModel>) {
        val paper = filers[0]
        val glass = filers[1]
        val plastic = filers[2]
        val metal = filers[3]
        previousMarker = null

        collection.clear()
        val filteredMarkers = dataMarkers.filter {
            val firstState = (it.isPaper || !paper.isChecked)
            val secondState = (it.isGlass || !glass.isChecked)
            val thirdState = (it.isPlastic || !plastic.isChecked)
            val fourthState = (it.isMetal || !metal.isChecked)
            return@filter firstState && secondState && thirdState && fourthState
        }
        repository.filteredData = filteredMarkers.toSet()
        addDataOnMap(filteredMarkers.toSet())
    }

    fun changeMarkerOnMap(marker: PlacemarkMapObject): Task<Void> {
        return changeMarker(marker)
    }

    fun changeMarkerOnMap(id: String): Task<Void> {
        val marker =
            (collection.userData as List<PlacemarkMapObject>).first { x -> (x.userData as PlaceMark).id == id }
        return changeMarker(marker)
    }


    private fun changeMarker(marker: PlacemarkMapObject): Task<Void> {

        val data = marker.userData as PlaceMark
        if (data.isFavorite) {
            //to un favorite
            val task = repository.favoritePlaceMarks!!.deleteFavorite(data)
            task.addOnSuccessListener {
                data.isFavorite = !data.isFavorite
                setNewImage(marker, WIDTH_BITMAP_MARKER, HEIGHT_BITMAP_MARKER)
            }
            return task

        } else {
            // to favorite
            val task = repository.favoritePlaceMarks!!.addFavorite(data)
            task.addOnSuccessListener {
                data.isFavorite = !data.isFavorite
                setNewImage(marker, WIDTH_BITMAP_MARKER, HEIGHT_BITMAP_MARKER)
            }
            return task
        }
    }

    fun openMarker(obj: PlacemarkMapObject) {
        closedMarker()
        previousMarker = obj
        setNewImage(obj, WIDTH_BITMAP_MARKER + 20, HEIGHT_BITMAP_MARKER + 20)
    }

    fun closedMarker() {
        if (previousMarker != null) {
            setNewImage(previousMarker!!, WIDTH_BITMAP_MARKER, HEIGHT_BITMAP_MARKER)
        }
    }

    private fun setNewImage(obj: PlacemarkMapObject, width: Int, height: Int) {
        val bit = if ((obj.userData as PlaceMark).isFavorite) {
            ImageUtils.getBitmap(context, drawable.trash_favorite_marker_icon)
        } else {
            ImageUtils.getBitmap(context, drawable.trash_marker_icon)
        }

        obj.setIcon(
            ImageProvider.fromBitmap(
                Bitmap.createScaledBitmap(
                    bit,
                    width,
                    height,
                    false
                )
            )
        )
    }


    fun startPickNewMarker() {
        val bitmap = ImageUtils.getBitmap(context, drawable.trash_marker_icon)
        // collection.clear()
        collection.isVisible = false
        pickerMarker = mapView.map.mapObjects.addPlacemark(
            mapView.map.cameraPosition.target,
            ImageProvider.fromBitmap(
                Bitmap.createScaledBitmap(
                    bitmap,
                    WIDTH_BITMAP_MARKER + 30,
                    HEIGHT_BITMAP_MARKER + 30,
                    false
                )
            )
        )

        pickerMarkerDraggListener =
            CameraListener { _, cameraPosition, _, _ ->
                pickerMarker!!.geometry = cameraPosition.target
            }
        mapView.map.addCameraListener(pickerMarkerDraggListener!!)
    }


    fun endPickNewMarker(): Point {
        val point = pickerMarker!!.geometry
        mapView.map.mapObjects.remove(pickerMarker!!)
        mapView.map.removeCameraListener(pickerMarkerDraggListener!!)
        pickerMarker = null
        pickerMarkerDraggListener = null
        collection.isVisible = true
        return point
    }

    fun getDistanceMetersToMarker(point: Point): Double {
        val userPoint = userLocation.cameraPosition()?.target ?: return 0.0
        val lon: Double = userPoint.longitude
        val lat: Double = userPoint.latitude
        val lon2: Double = point.longitude
        val lat2: Double = point.latitude
        val result = 111.2 * sqrt(
            (lon - lon2) * (lon - lon2) + (lat - lat2) * cos(Math.PI * lon / 180) * (lat - lat2) * cos(
                Math.PI * lon / 180
            )
        )

        return result*1000

    }

    companion object {
        const val DEFAULT_ZOOM = 15F
        const val WIDTH_BITMAP_MARKER = 90
        const val HEIGHT_BITMAP_MARKER = 120

        fun initialize(context: Context, apiKey: String) {
            MapKitFactory.setApiKey(apiKey)
            MapKitFactory.initializeBackgroundDownload(context, null)
        }
    }
}


