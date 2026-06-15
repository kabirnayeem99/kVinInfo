package io.github.kabirnayeem99.viminfo.exceptions

class UnknownManufacturerException(wmi: String) : VinException(
    "Unknown WMI: '${wmi.uppercase()}'."
)
