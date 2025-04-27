package com.manisoft.scraprushapp.utils.locationpickerutils.locationsmodel

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

/** The northeast and southwest points that delineate the outer bounds of a map.  */
class Bounds : Serializable {
    /** The northeast corner of the bounding box.  */
    var northeast: LatLng? = null

    /** The southwest corner of the bounding box.  */
    var southwest: LatLng? = null
    override fun toString(): String {
        return String.format("[%s, %s]", northeast, southwest)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}