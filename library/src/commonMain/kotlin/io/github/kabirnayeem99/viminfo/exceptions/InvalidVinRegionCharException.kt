package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates an invalid region character in the VIN.
 *
 * This exception is thrown when the first character of the VIN (region ID) does not match a valid region code.
 *
 * @param regionChar The invalid region character.
 */
class InvalidVinRegionCharException(regionChar: String) :
    InvalidVinException("Invalid region ID: '$regionChar'. The first character of the VIN should be within the ranges A-H, J-R, S-Z, or 1-9 to indicate a valid region. Please check the VIN and try again.")
