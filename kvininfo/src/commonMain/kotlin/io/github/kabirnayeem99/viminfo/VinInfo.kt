package io.github.kabirnayeem99.viminfo

import io.github.kabirnayeem99.viminfo.data.getCountryFromWmi
import io.github.kabirnayeem99.viminfo.data.manufacturers
import io.github.kabirnayeem99.viminfo.data.years
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinRegionCharException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidWmiException
import io.github.kabirnayeem99.viminfo.exceptions.NoChecksumForEuException
import io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidWmiForCountryException
import io.github.kabirnayeem99.viminfo.network.NhtsaUsaApi
import kotlin.jvm.JvmStatic

/**
 * Represents a Vehicle Identification Number (VIN) and provides methods for parsing, validation, and extraction of relevant data.
 *
 * This class encapsulates information about a VIN, including its constituent parts and associated data. It offers functionalities to:
 * - Parse a VIN string into its components (WMI, VDS, VIS, Country, Region, Brand, Make, Model and so on).
 * - Validate the VIN based on basic format checks and optional NHTSA validation.
 * - Extract information such as year, manufacturer, and region.
 * - Generate a JSON representation of the VIN data.
 *
 * **Example:**
 * ```kotlin
 * val vin = "WBA3A5G59DNP26082"
 * val vinInfo = VinInfo.fromNumber(vin)
 * vinInfo.use { vi -> println(vi.year) } // 2013
 * ```
 * A VIN (Vehicle Identification Number) is a unique 17-character code assigned to every individual motor vehicle. It's like a fingerprint for a car, ensuring no two vehicles have the same identifier.
 *
 * For more information on decoding the VDS, use https://en.wikibooks.org/wiki/Vehicle_Identification_Numbers_(VIN_codes)
 *
 * **Note:** This class is designed for basic VIN processing and validation. For more complex VIN-related operations, consider using specialized libraries or databases.
 */
class VinInfo private constructor(private val normalizedNumber: String) : AutoCloseable {

    private val nhtsaUsaApi by lazy { NhtsaUsaApi(normalizedNumber) }

    private val validVinRegex = "^[a-zA-Z0-9]+$".toRegex()

    /**
     * Indicates whether the VIN passes a basic format check.
     *
     * This property performs a preliminary validation based on the VIN's length, adherence to a basic regular expression pattern, and the calculated check digit matching the VIN's check digit at the ninth position.
     *
     * It does not guarantee the VIN's overall validity or correctness, as the check digit position or VIN length might vary for certain VIN standards.
     */
    val isValid: Boolean
        get() = validVinRegex.matches(normalizedNumber) && normalizedNumber.length == 17 && (if (regionCode == "EU" || country == "United Kingdom") true else calculatedChecksum == checksum)

    val vinNumber: String
        get() = normalizedNumber

    /**
     * The World Manufacturer Identifier (WMI) part of the VIN.
     *
     * Extracts the first three characters of the normalized VIN as the WMI.
     *
     * @throws InvalidVinLengthException If the VIN is too short to extract the WMI.
     */
    val wmi: String
        get() = if (normalizedNumber.length >= 3) normalizedNumber.substring(
            0, 3
        ) else throw InvalidVinLengthException(normalizedNumber)

    /**
     * The Vehicle Descriptor Section (VDS) part of the VIN.
     *
     * Extracts characters 4 to 8 of the normalized VIN as the VDS.
     *
     * @throws InvalidVinLengthException If the VIN is too short to extract the VDS.
     */
    val vds: String
        get() = if (normalizedNumber.length >= 9) normalizedNumber.substring(
            3, 9
        ) else throw InvalidVinLengthException(normalizedNumber)

    /**
     * The Vehicle Identification Section (VIS) part of the VIN.
     *
     * Extracts characters 9 to 16 of the normalized VIN as the VIS.
     *
     * @throws InvalidVinLengthException If the VIN is too short to extract the VIS.
     */
    val vis: String
        get() = if (normalizedNumber.length >= 17) normalizedNumber.substring(
            9, 17
        ) else throw InvalidVinLengthException(normalizedNumber)


    /**
     * Validates the VIN against the NHTSA USA database.
     *
     * This function performs a comprehensive check for VIN validity by querying the NHTSA USA API.
     * It's specifically designed for validating US-based VIN numbers.
     *
     * @return A `Result` object indicating success with the VIN number if valid, or failure with an `InvalidVinException` if invalid.
     */
    suspend fun isValidByNhtsa() = nhtsaUsaApi.isValidByNhtsa()

    /**
     * The year of the vehicle as an integer.
     *
     * This property extracts the year from the VIN's year character and returns it as an integer.
     *
     * @throws InvalidVinYearException If the year character is invalid.
     */
    val year: Int
        get() = years[yearCharacter] ?: throw InvalidVinYearException(yearCharacter)


    /**
     * Calculates the check digit for the VIN.
     *
     * This property computes the check digit based on the provided VIN number using the specified algorithm.
     */
    val calculatedChecksum: Char
        get() {
            val map = "0123456789X"
            val weights = "8765432X098765432"

            val sum = normalizedNumber.indices.sumOf { index ->
                transliterate(normalizedNumber[index]) * map.indexOf(weights[index])
            }

            return map[sum % 11]
        }

    /**
     * Transliterates a character to a numeric value.
     *
     * This function maps a character to its corresponding numeric value based on a predefined mapping.
     *
     * @param char The character to be transliterated.
     * @return The numeric value corresponding to the character.
     */
    private fun transliterate(char: Char): Int {
        val map = "0123456789.ABCDEFGH..JKLMN.P.R..STUVWXYZ"
        return map.indexOf(char) % 10
    }


    private val regionMap = mapOf(
        "AF" to "Africa",
        "AS" to "Asia",
        "EU" to "Europe",
        "NA" to "North America",
        "OC" to "Oceania",
        "SA" to "South America"
    )

    /**
     * The region name associated with the VIN.
     *
     * This property retrieves the region name based on the calculated region code.
     *
     * @throws IllegalArgumentException If the region code is invalid or not found in the region map.
     */
    val region: String
        get() = regionMap[regionCode] ?: throw IllegalArgumentException("Wrong region code.")

    /**
     * The country associated with the VIN.
     *
     * This property retrieves the country based on the World Manufacturer Identifier (WMI) part of the VIN.
     *
     * @return The country name.
     * @throws InvalidWmiForCountryException If the WMI does not match the expected country code.
     */
    val country: String
        get() = getCountryFromWmi(wmi)

    /**
     * The region code associated with the VIN.
     *
     * This property extracts the region code from the first character of the normalized VIN.
     *
     * @throws InvalidVinLengthException If the VIN is too short.
     * @throws InvalidVinRegionCharException If the first character is not a valid region code.
     */
    val regionCode: String
        get() {
            val regionId =
                normalizedNumber.getOrNull(0)?.toString() ?: throw InvalidVinLengthException(
                    normalizedNumber
                )

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
                else -> throw InvalidVinRegionCharException(regionId)
            }
        }

    /**
     * The manufacturer of the vehicle.
     *
     * This property determines the manufacturer based on the World Manufacturer Identifier (WMI) part of the VIN.
     * It first checks for a full WMI match, then attempts to find a match based on the first two characters of the WMI.
     *
     * @throws InvalidVinLengthException If the VIN is too short to extract the WMI.
     * @throws InvalidWmiException If the WMI or its shortened version is not found in the manufacturers map.
     */
    val manufacturer: String
        get() {
            if (wmi.isBlank()) throw InvalidVinLengthException(normalizedNumber)
            return if (manufacturers.containsKey(this.wmi)) {
                manufacturers[this.wmi] ?: throw InvalidWmiException(wmi)
            } else {
                val alternativeWmiId = this.wmi.substring(0, 2)
                if (manufacturers.containsKey(alternativeWmiId)) {
                    manufacturers[alternativeWmiId] ?: throw InvalidWmiException(wmi)
                } else {
                    throw InvalidWmiException(wmi)
                }
            }

        }

    /**
     * The checksum character of the VIN.
     *
     * This property extracts the checksum character from the ninth position of the normalized VIN for regions other than EU.
     *
     * @throws NoChecksumForEuException If the region is EU, which doesn't have a checksum character.
     */
    val checksum: Char
        get() = if (regionCode != "EU") normalizedNumber[8] else throw NoChecksumForEuException()

    /**
     * The assembly plant code character.
     *
     * This property extracts the assembly plant code character from the eleventh position of the normalized VIN.
     *
     * @throws InvalidVinLengthException If the VIN is too short to extract the assembly plant code character.
     */
    val assemblyPlant: Char
        get() = normalizedNumber.getOrNull(10) ?: throw InvalidVinLengthException(normalizedNumber)

    /**
     * The serial number part of the VIN.
     *
     * This property extracts the last five characters of the normalized VIN as the serial number.
     */
    val serialNumber: String
        get() = normalizedNumber.substring(12, 17)

    /**
     * Retrieves the make of the vehicle from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The make of the vehicle.
     * @throws NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getMakeFromNhtsa(): String = nhtsaUsaApi.getMakeValue()

    /**
     * Retrieves the model of the vehicle from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The model of the vehicle.
     * @throws NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getModelFromNhtsa(): String = nhtsaUsaApi.getModelValue()

    /**
     * Retrieves the vehicle type from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The vehicle type.
     * @throws NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getVehicleTypeFromNhtsa(): String = nhtsaUsaApi.getVehicleTypeValue()

    /**
     * Retrieves the body class of the vehicle from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The body class of the vehicle.
     * @throws NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getBodyClassFromNhtsa(): String = nhtsaUsaApi.getBodyClassValue()


    /**
     * The year character of the VIN.
     *
     * This property extracts the year character from the tenth position of the normalized VIN.
     *
     * @throws InvalidVinLengthException If the VIN is too short to extract the year character.
     */
    private val yearCharacter: Char
        get() = normalizedNumber.getOrNull(9) ?: throw InvalidVinLengthException(normalizedNumber)


    /**
     * Returns a string representation of the VIN.
     *
     * This function concatenates the WMI, VDS, and VIS parts of the VIN to form the complete VIN.
     */
    override fun toString() = this.wmi + this.vds + this.vis

    /**
     * Converts the decoded VIN information to a JSON string using the NHTSA USA API.
     *
     * @return The decoded VIN information as a JSON string.
     * @throws NhtsaDatabaseFailedException If an error occurs during serialization.
     */
    suspend fun toStringAsJson() = nhtsaUsaApi.toStringAsJson()

    companion object {

        private fun String.normalize() = uppercase().replace("-", "")

        /**
         * Creates a `VinInfo` object from a given number string.
         *
         * This function parses the provided number string and extracts relevant information
         * based on its length. The extracted information is stored in the returned `VinInfo` object.
         *
         * **Example:**
         * ```kotlin
         * val vin = "WBA3A5G59DNP26082"
         * val vinInfo = VinInfo.fromNumber(vin)
         * ```
         *
         * @param number The VIN number as a string.
         * @return A `VinInfo` object containing parsed information from the number string.
         * @throws InvalidVinLengthException if the VIN number is Blank
         */
        @JvmStatic
        fun fromNumber(number: String): VinInfo {
            if (number.isBlank()) throw InvalidVinLengthException(number)
            val normalizedNumber = number.normalize()
            return VinInfo(normalizedNumber = normalizedNumber)
        }

        /**
         * Extracts information from a VIN (Vehicle Identification Number) and executes a lambda function with the extracted data.
         *
         * **Example:**
         * ```kotlin
         * val vin = "WBA3A5G59DNP26082"
         * val vinInfo = VinInfo.fromNumber(vin)
         * "WBA3A5G59DNP26082".withVinInfo {
         *     println(year)  // 2013
         *     println(region)  // Europe
         *     println(manufacturer)  // BMW AG
         *     println(getMakeFromNhtsa())  // BMW
         *     println(getModelFromNhtsa())  // 328i
         * }
         * ```
         *
         * @receiver The VIN string from which to extract information.
         * @param info A lambda function that receives a `VinInfo` instance as its receiver.
         *             This lambda can be used to access and manipulate the extracted VIN information.
         * @throws InvalidVinLengthException if the VIN number is Blank
         */
        fun String.withVinInfo(info: VinInfo.() -> Unit) =
            fromNumber(this).use { vinInfo -> vinInfo.info() }

    }


    /**
     * Closes the current instance of the class.
     *
     * This function releases any resources held by the object, such as network connections.
     */
    override fun close() {
        nhtsaUsaApi.close()
    }
}