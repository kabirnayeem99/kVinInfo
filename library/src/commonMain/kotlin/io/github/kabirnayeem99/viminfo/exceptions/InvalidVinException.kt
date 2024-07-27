package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates an invalid VIN format.
 *
 * This exception is thrown when the VIN does not adhere to the expected format or structure.
 *
 * @param message A descriptive message explaining the reason for the invalid VIN.
 */
open class InvalidVinException(message: String) : Exception(message)