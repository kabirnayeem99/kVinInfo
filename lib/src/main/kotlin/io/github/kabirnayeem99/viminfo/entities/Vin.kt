package io.github.kabirnayeem99.viminfo.entities

import io.github.kabirnayeem99.viminfo.data.manufacturers
import io.github.kabirnayeem99.viminfo.data.years

data class Vin(
    val number: String,
    private val normalizedNumber: String,
    val wmi: String,
    val vds: String,
    val vis: String,
    val isExtended: Boolean,
    private var vehicleInfo: Map<String, Any> = emptyMap(),
) {

    private val validVinRegex = "^[a-zA-Z0-9]+$".toRegex()

    fun isValid(number: String? = null): Boolean {
        val normalizedNumber = (number ?: this.number).normalize()
        return validVinRegex.matches(normalizedNumber) && normalizedNumber.length == 17
    }

    val year: Int
        get() = years[modelYear] ?: throw IllegalStateException("Invalid model year: $modelYear")

    val region: String
        get() {
            val regionId = number[0].toString()

            val regexAF = Regex("[A-H]", RegexOption.IGNORE_CASE)
            val regexAS = Regex("[J-R]", RegexOption.IGNORE_CASE)
            val regexEU = Regex("[S-Z]", RegexOption.IGNORE_CASE)
            val regexNA = Regex("[1-5]", RegexOption.IGNORE_CASE)
            val regexOC = Regex("[6-7]", RegexOption.IGNORE_CASE)
            val regexSA = Regex("[8-9]", RegexOption.IGNORE_CASE)

            return when {
                regexAF.containsMatchIn(regionId) -> "AF"
                regexAS.containsMatchIn(regionId) -> "AS"
                regexEU.containsMatchIn(regionId) -> "EU"
                regexNA.containsMatchIn(regionId) -> "NA"
                regexOC.containsMatchIn(regionId) -> "OC"
                regexSA.containsMatchIn(regionId) -> "SA"
                else -> throw Exception("Invalid region ID: $regionId")
            }
        }

    val manufacturer: String
        get() {
            return if (manufacturers.containsKey(this.wmi)) {
                manufacturers[this.wmi]
                    ?: throw Exception("Unknown World Manufacturer Identifier (WMI): ${this.wmi.uppercase()}")
            } else {
                val alternativeWmiId = this.wmi.substring(0, 2)
                if (manufacturers.containsKey(alternativeWmiId)) {
                    manufacturers[alternativeWmiId]
                        ?: throw Exception("Unknown World Manufacturer Identifier (WMI): ${alternativeWmiId.uppercase()}")
                } else {
                    throw Exception("Unknown World Manufacturer Identifier (WMI): ${this.wmi.uppercase()}")
                }
            }

        }

    val checksum: Char
        get() = if (region != "EU") normalizedNumber[8] else throw Exception("No CheckSum for the Europe region.")

    val assemblyPlant: Char
        get() = normalizedNumber.getOrNull(10)
            ?: throw Exception("Wrong VIN Number length: ${normalizedNumber.length}")

    val serialNumber: String
        get() = normalizedNumber.substring(12, 17)

    // Future<void> _fetchExtendedVehicleInfo() async {
    //    if (this._vehicleInfo.isEmpty && extended == true) {
    //      this._vehicleInfo = await NHTSA.decodeVinValues(this.number);
    //    }
    //  }
    //
    //  /// Get the Make of the vehicle from the NHTSA database if [extended] mode
    //  /// is enabled.
    //  Future<String> getMakeAsync() async {
    //    await _fetchExtendedVehicleInfo();
    //    return this._vehicleInfo['Make'];
    //  }
    //
    //  /// Get the Model of the vehicle from the NHTSA database if [extended] mode
    //  /// is enabled.
    //  Future<String> getModelAsync() async {
    //    await _fetchExtendedVehicleInfo();
    //    return this._vehicleInfo['Model'];
    //  }
    //
    //  /// Get the Vehicle Type from the NHTSA database if [extended] mode is
    //  /// enabled.
    //  Future<String> getVehicleTypeAsync() async {
    //    await _fetchExtendedVehicleInfo();
    //    return this._vehicleInfo['VehicleType'];
    //  }


    private val modelYear: Char
        get() = normalizedNumber.getOrNull(9) ?: throw Exception("Invalid VIN Length")


    override fun toString() = this.wmi + this.vds + this.vis

    companion object {

        private fun String.normalize() = uppercase().replaceAfter("-", "")

        fun fromNumber(number: String, isExtended: Boolean = false): Vin {
            val normalizedNumber = number.normalize()
            return Vin(
                number = number,
                isExtended = isExtended,
                normalizedNumber = normalizedNumber,
                wmi = normalizedNumber.substring(0, 3),
                vds = normalizedNumber.substring(3, 9),
                vis = normalizedNumber.substring(9, 17),
            )
        }
    }
}