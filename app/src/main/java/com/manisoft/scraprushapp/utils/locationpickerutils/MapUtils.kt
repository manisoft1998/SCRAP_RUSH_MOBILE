package com.manisoft.scraprushapp.utils.locationpickerutils

import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil


object MapUtils {

    fun drawCurveOnMap(googleMap: GoogleMap, latLng1: LatLng, latLng2: LatLng, context: Context) {
        //Adding marker is optional here, you can move out from here.
        googleMap.addMarker(
            MarkerOptions().position(latLng1).icon(BitmapDescriptorFactory.defaultMarker())
        )
        googleMap.addMarker(
            MarkerOptions().position(latLng2).icon(BitmapDescriptorFactory.defaultMarker())
        )

        val k = 0.1 //curve radius
        var h = SphericalUtil.computeHeading(latLng1, latLng2)
        val d: Double
        val p: LatLng?

        //The if..else block is for swapping the heading, offset and distance
        //to draw curve always in the upward direction
        if (h < 0) {
            d = SphericalUtil.computeDistanceBetween(latLng2, latLng1)
            h = SphericalUtil.computeHeading(latLng2, latLng1)
            //Midpoint position
            p = SphericalUtil.computeOffset(latLng2, d * 0.5, h)
        } else {
            d = SphericalUtil.computeDistanceBetween(latLng1, latLng2)

            //Midpoint position
            p = SphericalUtil.computeOffset(latLng1, d * 0.5, h)
        }

        //Apply some mathematics to calculate position of the circle center
        val x = (1 - k * k) * d * 0.5 / (2 * k)
        val r = (1 + k * k) * d * 0.5 / (2 * k)

        val c = SphericalUtil.computeOffset(p, x, h + 90.0)

        //Calculate heading between circle center and two points
        val h1 = SphericalUtil.computeHeading(c, latLng1)
        val h2 = SphericalUtil.computeHeading(c, latLng2)

        //Calculate positions of points on circle border and add them to polyline options
        val numberOfPoints = 1000 //more numberOfPoints more smooth curve you will get
        val step = (h2 - h1) / numberOfPoints

        //Create PolygonOptions object to draw on map
        val polygon = PolygonOptions()

        //Create a temporary list of LatLng to store the points that's being drawn on map for curve
        val temp = arrayListOf<LatLng>()

        //iterate the numberOfPoints and add the LatLng to PolygonOptions to draw curve
        //and save in temp list to add again reversely in PolygonOptions
        for (i in 0 until numberOfPoints) {
            val latlng = SphericalUtil.computeOffset(c, r, h1 + i * step)
            polygon.add(latlng) //Adding in PolygonOptions
            temp.add(latlng)    //Storing in temp list to add again in reverse order
        }

        //iterate the temp list in reverse order and add in PolygonOptions
        for (i in (temp.size - 1) downTo 1) {
            polygon.add(temp[i])
        }

        polygon.strokeColor(Color.BLACK)
        polygon.strokeWidth(12f)
        polygon.strokePattern(listOf(Dash(20f), Gap(10f))) //Skip if you want solid line
        googleMap.addPolygon(polygon)

        temp.clear() //clear the temp list

        getZoomLevel(context, latLng1, latLng2, googleMap)
    }

    private fun getZoomLevel(
        context: Context,
        latLng1: LatLng,
        latLng2: LatLng,
        googleMap: GoogleMap
    ) {
        val builder = LatLngBounds.Builder()

        val customMarkerLocation1 = LatLng(latLng1.latitude, latLng1.longitude)
        val customMarkerLocation2 = LatLng(latLng2.latitude, latLng2.longitude)
        builder.include(customMarkerLocation1)
        builder.include(customMarkerLocation2)

        val bounds = builder.build()
        val width: Int = context.resources.displayMetrics.widthPixels
        val height: Int = context.resources.displayMetrics.heightPixels
        val padding = (width * 0.15).toInt() // offset from edges of the map 15% of screen
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding))
    }

  /*  private fun midPoint(lat1: Double, long1: Double, lat2: Double, long2: Double): LatLng {
        return LatLng((lat1 + lat2) / 2, (long1 + long2) / 2)
    }

    private fun angleBetweenCoordinate(
        lat1: Double, long1: Double, lat2: Double,
        long2: Double
    ): Double {
        val dLon = long2 - long1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - (sin(lat1)
                * cos(lat2) * cos(dLon))
        var brng = atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        brng = 360 - brng
        return brng
    }*/

}