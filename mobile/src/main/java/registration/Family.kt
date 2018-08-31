package registration

data class Family(
    var familyId: String = "",
    var name: String = ""
) {

  constructor() : this("", "")
}
