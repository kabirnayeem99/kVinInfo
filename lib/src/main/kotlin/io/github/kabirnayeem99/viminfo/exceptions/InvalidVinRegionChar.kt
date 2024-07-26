package io.github.kabirnayeem99.viminfo.exceptions

class InvalidVinRegionChar(regionChar: String) :
    Exception("Invalid region ID: '$regionChar'. The first character of the VIN should be within the ranges A-H, J-R, S-Z, or 1-9 to indicate a valid region. Please check the VIN and try again.")
