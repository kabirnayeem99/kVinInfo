package io.github.kabirnayeem99.viminfo.exceptions

/**
 * Indicates an invalid World Manufacturer Identifier (WMI).
 *
 * This exception is thrown when the WMI part of the VIN cannot be matched to a known manufacturer.
 *
 * @param wmi The invalid WMI.
 */
class InvalidWmiException(wmi: String) : InvalidVinException(
    "Invalid WMI: '$wmi'. The WMI is the first three or sometimes two characters of the VIN and identifies the vehicle manufacturer. Please verify the WMI and try again."
)