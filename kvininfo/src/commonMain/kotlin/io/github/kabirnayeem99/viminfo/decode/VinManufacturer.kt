package io.github.kabirnayeem99.viminfo.decode

import io.github.kabirnayeem99.viminfo.data.manufacturers
import io.github.kabirnayeem99.viminfo.exceptions.InvalidVinLengthException
import io.github.kabirnayeem99.viminfo.exceptions.InvalidWmiException

/**
 * Manufacturer resolution from the WMI, including small-volume makers.
 */
internal object VinManufacturer {

    /** Marker digit in WMI position 3 for makers producing fewer than 500 vehicles/year. */
    private const val SMALL_VOLUME_MARKER = '9'

    /** Inclusive index range (positions 12–14) holding the extended id for small-volume makers. */
    private const val EXTENDED_ID_START = 11
    private const val EXTENDED_ID_END = 14

    private const val WMI_LENGTH = 3

    /** The 3-character WMI of [vin]. */
    private fun wmi(vin: String): String =
        if (vin.length >= WMI_LENGTH) vin.substring(0, WMI_LENGTH)
        else throw InvalidVinLengthException(vin)

    /** Whether [vin]'s WMI denotes a small-volume manufacturer (3rd character is `9`). */
    fun isSmallVolume(vin: String): Boolean {
        val wmi = wmi(vin)
        return wmi.length == WMI_LENGTH && wmi[2] == SMALL_VOLUME_MARKER
    }

    /**
     * Resolves the manufacturer name.
     *
     * For small-volume makers (WMI 3rd character `9`), positions 12–14 form an extended identifier
     * appended to the WMI. Resolution order: extended id, then full WMI, then the 2-character WMI
     * prefix used by high-volume makers.
     *
     * @throws InvalidVinLengthException If the VIN is too short to extract the WMI.
     * @throws InvalidWmiException If no manufacturer matches.
     */
    fun resolve(vin: String): String {
        val wmi = wmi(vin)
        if (isSmallVolume(vin)) {
            if (vin.length < EXTENDED_ID_END) throw InvalidVinLengthException(vin)
            val extendedId = wmi + vin.substring(EXTENDED_ID_START, EXTENDED_ID_END)
            manufacturers[extendedId]?.let { return it }
        }
        manufacturers[wmi]?.let { return it }
        manufacturers[wmi.substring(0, 2)]?.let { return it }
        throw InvalidWmiException(wmi)
    }
}
