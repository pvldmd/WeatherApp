package com.pvld.weatherapp.network.entities

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Rain {

    @SerializedName("1h")
    @Expose
    var _1h: Double? = null

}