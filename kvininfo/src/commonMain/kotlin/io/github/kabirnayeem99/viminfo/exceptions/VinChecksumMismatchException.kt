package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Thrown when a VIN's check digit (at position 9) does not match the calculated value.
 *
 * @param vin The VIN string with the mismatch.
 * @param expected The expected check digit character.
 * @param actual The actual check digit character found in the VIN.
 */
class VinChecksumMismatchException(
    val vin: String,
    val expected: Char,
    val actual: Char,
) : InvalidVinException(
    "VIN '$vin' has invalid check digit: expected '$expected' at position 9, found '$actual'."
)
