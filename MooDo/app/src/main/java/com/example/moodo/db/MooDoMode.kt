package com.example.moodo.db

import java.util.Date

data class MooDoMode(var idx:Long, var user:MooDoUser,
                     var mdMode:Int, var createdDate: String,
                     var weather:Int, var mdDaily:String)
