package com.example.moodo.db

import java.util.Date

data class MooDoToDo(var idx:Long, var user:MooDoUser,
                     var tdList:String, var startDate:Date,
                     var endDate:Date, var tdCheck:String = "N",
                     var createdDate:Date?)
