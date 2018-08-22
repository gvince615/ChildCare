package registration

data class Pediatrician(
    var pedName: String = "",
    var pedOfficeName: String = "",
    var pedOfficeNumber: String = "") {

  constructor() : this("", "", "")

}


