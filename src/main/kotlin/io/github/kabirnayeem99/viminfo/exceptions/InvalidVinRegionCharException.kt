package io.github.kabirnayeem99.viminfo.exceptions

class InvalidVinRegionCharException(regionChar: String) : VinException(
    "Invalid region character: '$regionChar'. Expected A-H, J-R, S-Z, or 1-9."
)
