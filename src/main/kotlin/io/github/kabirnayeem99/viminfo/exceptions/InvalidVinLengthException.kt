package io.github.kabirnayeem99.viminfo.exceptions

class InvalidVinLengthException(vin: String) : VinException(
    "Invalid VIN length: ${vin.length}. Expected 17 characters."
)
