package com.example.moodo.db

import java.util.Date

data class MooDoToDo(var idx:Long, var user:MooDoUser,
                     var tdList:String, var startDate:String,
                     var endDate:String, var tdCheck:String?,
                     var createdDate:Date?, var color:String)