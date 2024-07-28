package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates an invalid WMI for the specified country.
 *
 * This exception is thrown when the WMI does not match the expected country code.
 */
class InvalidWmiForCountryException(wmi: String) :
    InvalidVinException("Invalid country for the VIN. The WMI ($wmi) does not match any known country.")