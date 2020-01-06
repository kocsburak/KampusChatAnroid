package com.xva.kampuschat.utils

import com.xva.kampuschat.entities.Department
import com.xva.kampuschat.entities.University

class EventBusHelper {


    internal class sendUniversity(var university: University)
    internal class sendDepartment(var department:Department)
    internal class publishFragment(var name:String)

}