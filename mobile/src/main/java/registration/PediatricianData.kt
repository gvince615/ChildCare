package registration

data class PediatricianData(
    var pedName: String = "",
    var pedOfficeName: String = "",
    var pedOfficeNum: String = "") {

  constructor() : this("", "", "")

}


