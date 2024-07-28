package io.github.kabirnayeem99.viminfo.network.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NhtsaDecodeVinDto(
    @SerialName("Count") val count: Long? = null,
    @SerialName("Message") val message: String? = null,
    @SerialName("Results") val results: List<Result?>? = null,
    @SerialName("SearchCriteria") val searchCriteria: String? = null
) {
    @Serializable
    data class Result(
        @SerialName("Value") val value: String? = null,
        @SerialName("ValueId") val valueId: String? = null,
        @SerialName("Variable") val variable: String? = null,
        @SerialName("VariableId") val variableId: Long? = null
    )

    companion object {
        const val ERROR_TEXT_VARIABLE_ID = 191L
        const val MAKE_VARIABLE_ID = 26L
        const val MODEL_VARIABLE_ID = 28L
        const val VEHICLE_TYPE_VARIABLE_ID = 39L
        const val BODY_CLASS_VARIABLE_ID = 5L
    }
}