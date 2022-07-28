package com.kazuki.fakecameraclient.data

enum class AlertType {
    SUCCESS, WARNING, ERROR, UNKNOWN, NONE
}

data class Alert(
    val type: AlertType = AlertType.UNKNOWN,
    val title: String = "",
    val detail: String = "",
    val confirmAction: Pair<String, () -> Unit> = Pair("", {}),
    val dismissAction: Pair<String, () -> Unit>? = null,
)

object AlertPrefab {
    val None = Alert(AlertType.NONE)
}