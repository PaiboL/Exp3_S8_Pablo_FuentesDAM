package cl.duoc.senalsegura.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import cl.duoc.senalsegura.data.AlertRepository
import cl.duoc.senalsegura.model.EmergencyAlert

class AlertContentProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        val selectedAlerts = when (uriMatcher.match(uri)) {
            ALERTS -> AlertRepository.alerts
            ALERT_ID -> listOfNotNull(AlertRepository.findById(uri.lastPathSegment?.toIntOrNull()))
            else -> throw IllegalArgumentException("URI de alertas no reconocida: $uri")
        }

        return MatrixCursor(AlertContract.columns).apply {
            selectedAlerts.forEach { alert ->
                addRow(
                    arrayOf(
                        alert.id,
                        alert.title,
                        alert.visibleMessage(),
                        alert.category.name,
                        alert.symbol,
                        alert.textForSpeech(),
                        (alert as? EmergencyAlert)?.actionDescription()
                    )
                )
            }
            context?.contentResolver?.let { resolver ->
                setNotificationUri(resolver, uri)
            }
        }
    }

    override fun getType(uri: Uri): String = when (uriMatcher.match(uri)) {
        ALERTS -> "vnd.android.cursor.dir/vnd.${AlertContract.AUTHORITY}.alert"
        ALERT_ID -> "vnd.android.cursor.item/vnd.${AlertContract.AUTHORITY}.alert"
        else -> throw IllegalArgumentException("URI de alertas no reconocida: $uri")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        throw UnsupportedOperationException("El proveedor de alertas es de solo lectura")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("El proveedor de alertas es de solo lectura")
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("El proveedor de alertas es de solo lectura")
    }

    private companion object {
        const val ALERTS = 1
        const val ALERT_ID = 2

        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AlertContract.AUTHORITY, AlertContract.PATH_ALERTS, ALERTS)
            addURI(AlertContract.AUTHORITY, "${AlertContract.PATH_ALERTS}/#", ALERT_ID)
        }
    }
}
