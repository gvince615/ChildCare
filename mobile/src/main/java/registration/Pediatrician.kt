package registration

data class Pediatrician(
    var pedName: String = "",
    var pedOfficeName: String = "",
    var pedOfficeNum: String = "") {

  constructor() : this("", "", "")

}


