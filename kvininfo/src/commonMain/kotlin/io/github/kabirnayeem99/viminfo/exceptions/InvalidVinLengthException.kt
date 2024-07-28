package io.github.kabirnayeem99.viminfo.exceptions


/**
 * Indicates an invalid VIN length.
 *
 * This exception is thrown when the VIN does not have the expected length of 17 characters.
 *
 * @param vin The invalid VIN with incorrect length.
 */
class InvalidVinLengthException(vin: String) :
    InvalidVinException("Invalid VIN length: ${vin.length}. Expected length is 17 characters. Ensure that the VIN is 17 characters long, which is the standard length for VINs. Please check the VIN and try again.")