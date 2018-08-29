package registration

data class ChildData(
    var family: Family?,
    var child: Child?,
    var guardians: ArrayList<Guardian>?,
    var medications: ArrayList<Medication>?,
    var pediatrician: Pediatrician?,
    var billing: Billing?
) {

  constructor() : this(null, null, null, null, null, null)
}

