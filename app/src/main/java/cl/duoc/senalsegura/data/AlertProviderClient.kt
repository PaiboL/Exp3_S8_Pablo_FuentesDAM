package cl.duoc.senalsegura.data

import android.content.Context
import androidx.core.database.getStringOrNull
import cl.duoc.senalsegura.model.AccessibleAlert
import cl.duoc.senalsegura.model.AlertCategory
import cl.duoc.senalsegura.model.EmergencyAlert
import cl.duoc.senalsegura.model.InformationAlert
import cl.duoc.senalsegura.provider.AlertContract

object AlertProviderClient {

    fun queryAlerts(context: Context): Result<List<AccessibleAlert>> = runCatching {
        val alerts = mutableListOf<AccessibleAlert>()

        context.contentResolver.query(
            AlertContract.CONTENT_URI,
            AlertContract.columns,
            null,
            null,
            "${AlertContract.COLUMN_TITLE} ASC"
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(AlertContract.COLUMN_ID)
            val titleIndex = cursor.getColumnIndexOrThrow(AlertContract.COLUMN_TITLE)
            val messageIndex = cursor.getColumnIndexOrThrow(AlertContract.COLUMN_MESSAGE)
            val categoryIndex = cursor.getColumnIndexOrThrow(AlertContract.COLUMN_CATEGORY)
            val symbolIndex = cursor.getColumnIndexOrThrow(AlertContract.COLUMN_SYMBOL)
            val actionIndex = cursor.getColumnIndexOrThrow(AlertContract.COLUMN_SUGGESTED_ACTION)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(idIndex)
                val title = cursor.getString(titleIndex)
                val message = cursor.getString(messageIndex)
                val category = AlertCategory.valueOf(cursor.getString(categoryIndex))
                val symbol = cursor.getString(symbolIndex)
                val suggestedAction = cursor.getStringOrNull(actionIndex)

                alerts += if (suggestedAction != null) {
                    EmergencyAlert(id, title, message, category, suggestedAction)
                } else {
                    InformationAlert(id, title, message, category, symbol)
                }
            }
        } ?: error("El ContentProvider no entregó un cursor")

        alerts.toList()
    }
}
