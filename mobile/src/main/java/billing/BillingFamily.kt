package billing

class BillingFamily(
    val familyName: String,
    var familyId: String,
    val children: ArrayList<BillingChildDataModel>
) {
  constructor() : this("", "", ArrayList())
}