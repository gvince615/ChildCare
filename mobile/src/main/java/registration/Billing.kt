package registration

class Billing(
    var billingCycle: String,
    var billingType: String,
    var flatRateAmount: String,
    var hourlyRateAmount: String,
    var hourlyMaxBillableHours: String,
    var hourlyMinBillableTime: String,
    var hourlyRoundUpRule: String,
    var discountPercent: String,
    var discountType: String

) {

  constructor() : this("", "", "", "", "", "", "", "", "")
}
