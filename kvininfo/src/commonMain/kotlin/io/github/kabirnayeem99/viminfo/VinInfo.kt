package io.github.kabirnayeem99.viminfo

import io.github.kabirnayeem99.viminfo.data.getCountryFromWmi
import io.github.kabirnayeem99.viminfo.decode.VinChecksum
import io.github.kabirnayeem99.viminfo.decode.VinFormat
import io.github.kabirnayeem99.viminfo.decode.VinManufacturer
import io.github.kabirnayeem99.viminfo.decode.VinModelYear
import io.github.kabirnayeem99.viminfo.decode.VinRegion
import io.github.kabirnayeem99.viminfo.decode.VinSanitizer
import io.github.kabirnayeem99.viminfo.decode.VinValidator
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.NoChecksumForEuException
import io.github.kabirnayeem99.viminfo.network.NhtsaUsaApi
import io.ktor.client.engine.HttpClientEngine
import kotlin.jvm.JvmStatic

/**
 * Represents a Vehicle Identification Number (VIN) and provides methods for parsing, validation, and extraction of relevant data.
 *
 * This class is a thin facade over the decoders in the `decode` package (format, checksum, region,
 * model year and manufacturer) plus the NHTSA network lookups. It offers functionalities to:
 * - Parse a VIN string into its components (WMI, VDS, VIS, Country, Region, Brand, Make, Model and so on).
 * - Validate the VIN based on basic format checks and optional NHTSA validation.
 * - Extract information such as year, manufacturer, and region.
 * - Generate a JSON representation of the VIN data.
 *
 * **Example:**
 * ```kotlin
 * val vin = "WBA3A5G59DNP26082"
 * val vinInfo = VinInfo.fromNumber(vin).getOrThrow()
 * vinInfo.use { vi -> println(vi.year) } // 2013
 * ```
 * A VIN (Vehicle Identification Number) is a unique 17-character code assigned to every individual motor vehicle. It's like a fingerprint for a car, ensuring no two vehicles have the same identifier.
 *
 * For more information on decoding the VDS, use https://en.wikibooks.org/wiki/Vehicle_Identification_Numbers_(VIN_codes)
 *
 * **Note:** This class is designed for basic VIN processing and validation. For more complex VIN-related operations, consider using specialized libraries or databases.
 */
class VinInfo private constructor(
    private val normalizedNumber: String,
    private val nhtsaEngine: HttpClientEngine? = null,
) : AutoCloseable {

    private var _nhtsaUsaApi: NhtsaUsaApi? = null
    private val nhtsaUsaApi: NhtsaUsaApi
        get() {
            if (_nhtsaUsaApi == null) _nhtsaUsaApi = NhtsaUsaApi(normalizedNumber, nhtsaEngine)
            return _nhtsaUsaApi!!
        }

    /**
     * Indicates whether the VIN's format is valid: exactly 17 characters from the allowed
     * alphabet (digits and letters except I, O and Q).
     *
     * This does not check the check digit; see [isCheckDigitValid] and [isValid].
     */
    val isFormatValid: Boolean
        get() = VinFormat.isValid(normalizedNumber)

    /**
     * Whether the check digit (position 9) is mandatory for this VIN's region.
     *
     * Per the standard the check digit is compulsory only in North America (USA, Canada) and
     * China; it is optional everywhere else (e.g. Europe).
     *
     * @throws io.github.kabirnayeem99.viminfo.exceptions.InvalidVinRegionCharException If the first character is not a valid region code.
     */
    val isCheckDigitRequired: Boolean
        get() = VinRegion.requiresCheckDigit(normalizedNumber)

    /**
     * Whether the check digit (position 9) matches the value computed from the rest of the VIN.
     */
    val isCheckDigitValid: Boolean
        get() = VinChecksum.matches(normalizedNumber)

    /**
     * Indicates whether the VIN is valid.
     *
     * The format must be valid, and where the check digit is mandatory for the region
     * ([isCheckDigitRequired]) it must also match. Regions that do not require a check digit are
     * considered valid on format alone.
     */
    val isValid: Boolean
        get() = isFormatValid && (!isCheckDigitRequired || isCheckDigitValid)

    val vinNumber: String
        get() = normalizedNumber

    /**
     * The World Manufacturer Identifier (WMI) part of the VIN.
     *
     * Extracts the first three characters of the normalized VIN as the WMI.
     */
    val wmi: String
        get() = normalizedNumber.substring(0, 3)

    /**
     * The Vehicle Descriptor Section (VDS) part of the VIN.
     *
     * Extracts characters 4 to 8 of the normalized VIN as the VDS.
     */
    val vds: String
        get() = normalizedNumber.substring(3, 9)

    /**
     * The Vehicle Identification Section (VIS) part of the VIN.
     *
     * Extracts characters 9 to 16 of the normalized VIN as the VIS.
     */
    val vis: String
        get() = normalizedNumber.substring(9, 17)

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
     * The model year of the vehicle as an integer.
     *
     * The model-year code (position 10) repeats every 30 years, so it is ambiguous on its own.
     * Position 7 selects the cycle: a numeric position 7 means the 1980–2009 cycle, an alphabetic
     * position 7 means the 2010–2039 cycle.
     *
     * @throws io.github.kabirnayeem99.viminfo.exceptions.InvalidVinYearException If the year character is not a valid model-year code.
     */
    val year: Int
        get() = VinModelYear.decode(normalizedNumber)

    /**
     * The expected check digit (9th character) for the VIN.
     *
     * Computed with the ISO 3779 weighted-sum algorithm.
     */
    val calculatedChecksum: Char
        get() = VinChecksum.calculate(normalizedNumber)

    /**
     * The region name associated with the VIN.
     *
     * @throws IllegalArgumentException If the region code is invalid or not found.
     */
    val region: String
        get() = VinRegion.name(normalizedNumber)

    /**
     * The country associated with the VIN.
     *
     * This property retrieves the country based on the World Manufacturer Identifier (WMI) part of the VIN.
     *
     * @return The country name, or null if it cannot be determined.
     */
    val country: String?
        get() = runCatching { getCountryFromWmi(wmi) }.getOrNull()

    /**
     * The region code associated with the VIN.
     *
     * @throws io.github.kabirnayeem99.viminfo.exceptions.InvalidVinRegionCharException If the first character is not a valid region code.
     */
    val regionCode: String
        get() = VinRegion.code(normalizedNumber)

    /**
     * The manufacturer of the vehicle.
     *
     * Determines the manufacturer from the WMI. For small-volume makers (WMI 3rd character `9`)
     * the extended identifier in positions 12–14 is also considered.
     *
     * @return The manufacturer name, or null if it cannot be determined.
     */
    val manufacturer: String?
        get() = runCatching { VinManufacturer.resolve(normalizedNumber) }.getOrNull()

    /**
     * Whether the VIN belongs to a small-volume manufacturer (fewer than 500 vehicles/year),
     * indicated by a `9` in the third WMI position.
     */
    val isSmallVolumeManufacturer: Boolean
        get() = VinManufacturer.isSmallVolume(normalizedNumber)

    /**
     * The checksum character of the VIN.
     *
     * This property extracts the checksum character from the ninth position of the normalized VIN for regions other than EU.
     *
     * @throws NoChecksumForEuException If the region is EU, which doesn't have a checksum character.
     */
    val checksum: Char
        get() = if (regionCode != "EU") normalizedNumber[VinChecksum.CHECK_DIGIT_INDEX]
        else throw NoChecksumForEuException()

    /**
     * The assembly plant code character.
     *
     * This property extracts the assembly plant code character from the eleventh position of the normalized VIN.
     */
    val assemblyPlant: Char
        get() = normalizedNumber[10]

    /**
     * The serial number part of the VIN.
     *
     * For high-volume manufacturers this is positions 12–17 (the last six characters). For
     * small-volume manufacturers, positions 12–14 carry the manufacturer identifier, so the serial
     * number is only positions 15–17 (the last three characters).
     */
    val serialNumber: String
        get() = if (isSmallVolumeManufacturer) normalizedNumber.substring(14, 17)
        else normalizedNumber.substring(11, 17)

    /**
     * Retrieves the make of the vehicle from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The make of the vehicle.
     * @throws io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getMakeFromNhtsa(): String = nhtsaUsaApi.getMakeValue()

    /**
     * Retrieves the model of the vehicle from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The model of the vehicle.
     * @throws io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getModelFromNhtsa(): String = nhtsaUsaApi.getModelValue()

    /**
     * Retrieves the vehicle type from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The vehicle type.
     * @throws io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getVehicleTypeFromNhtsa(): String = nhtsaUsaApi.getVehicleTypeValue()

    /**
     * Retrieves the body class of the vehicle from the NHTSA USA API.
     *
     * **Note:** This function is optimized for US-based VIN numbers. Results for other regions might be less accurate.
     *
     * @return The body class of the vehicle.
     * @throws io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException If the decoded value cannot be found.
     */
    suspend fun getBodyClassFromNhtsa(): String = nhtsaUsaApi.getBodyClassValue()

    /**
     * Returns a string representation of the VIN.
     */
    override fun toString() = normalizedNumber

    /**
     * Converts the decoded VIN information to a JSON string using the NHTSA USA API.
     *
     * @return The decoded VIN information as a JSON string.
     * @throws io.github.kabirnayeem99.viminfo.exceptions.NhtsaDatabaseFailedException If an error occurs during serialization.
     */
    suspend fun toJsonString() = nhtsaUsaApi.toStringAsJson()

    companion object {

        /**
         * Creates a `VinInfo` object from a randomly generated valid VIN.
         *
         * The generated VIN has a correct check digit, a valid region code, and a valid
         * model-year code. See [VinGenerator.generate] for details.
         *
         * @return A `VinInfo` backed by a freshly generated valid VIN.
         */
        @JvmStatic
        fun random(): VinInfo = fromNumber(VinGenerator.generate()).getOrThrow()

        /**
         * Creates a `VinInfo` object from a given number string.
         *
         * **Example:**
         * ```kotlin
         * val vin = "WBA3A5G59DNP26082"
         * val result = VinInfo.fromNumber(vin)
         * if (result.isSuccess) {
         *     val vinInfo = result.getOrThrow()
         *     // use vinInfo
         * }
         * ```
         *
         * @param number The VIN number as a string.
         * @return A `Result` object containing a `VinInfo` instance if valid, or a failure with an exception if invalid.
         */
        @JvmStatic
        fun fromNumber(number: String): Result<VinInfo> {
            val sanitized = VinSanitizer.sanitize(number)
            val error = VinValidator.validate(sanitized)
            if (error != null) return Result.failure(error)
            return Result.success(VinInfo(normalizedNumber = sanitized))
        }

        internal fun fromNumberWithEngine(number: String, engine: HttpClientEngine): Result<VinInfo> {
            val sanitized = VinSanitizer.sanitize(number)
            val error = VinValidator.validate(sanitized)
            if (error != null) return Result.failure(error)
            return Result.success(VinInfo(normalizedNumber = sanitized, nhtsaEngine = engine))
        }

        /**
         * Extracts information from a VIN and executes a lambda function with the extracted data.
         *
         * **Example:**
         * ```kotlin
         * "WBA3A5G59DNP26082".withVinInfo {
         *     println(year)  // 2013
         *     println(region)  // Europe
         *     println(manufacturer)  // BMW AG
         * }
         * ```
         *
         * @receiver The VIN string from which to extract information.
         * @param info A lambda function that receives a `VinInfo` instance as its receiver.
         */
        fun String.withVinInfo(info: VinInfo.() -> Unit) {
            val result = fromNumber(this)
            val vinInfo = result.getOrElse {
                it.printStackTrace()
                return
            }
            try {
                vinInfo.info()
            } finally {
                vinInfo.close()
            }
        }
    }

    /**
     * Closes the current instance of the class.
     *
     * This function releases any resources held by the object, such as network connections.
     */
    override fun close() {
        _nhtsaUsaApi?.close()
    }
}
