package io.github.kabirnayeem99.viminfo.exceptions


class InvalidVinLengthException(vin: String) :
    Exception("Invalid VIN length: ${vin.length}. Expected length is 17 characters. Ensure that the VIN is 17 characters long, which is the standard length for VINs. Please check the VIN and try again.") {}