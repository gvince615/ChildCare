package registration

data class Pediatrician(
    var pediatricianName: String = "",
    var pediatricianOfficeName: String = "",
    var pediatricianOfficeNumber: String = "") {

  constructor() : this("", "", "")

}


