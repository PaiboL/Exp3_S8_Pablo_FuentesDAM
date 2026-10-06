package cl.duoc.senalsegura.provider

import android.net.Uri
import androidx.core.net.toUri

object AlertContract {
    const val AUTHORITY = "cl.duoc.senalsegura.alerts"
    const val PATH_ALERTS = "alerts"

    val CONTENT_URI: Uri = "content://$AUTHORITY/$PATH_ALERTS".toUri()

    const val COLUMN_ID = "id"
    const val COLUMN_TITLE = "title"
    const val COLUMN_MESSAGE = "message"
    const val COLUMN_CATEGORY = "category"
    const val COLUMN_SYMBOL = "symbol"
    const val COLUMN_SPEECH_TEXT = "speech_text"
    const val COLUMN_SUGGESTED_ACTION = "suggested_action"

    val columns: Array<String> = arrayOf(
        COLUMN_ID,
        COLUMN_TITLE,
        COLUMN_MESSAGE,
        COLUMN_CATEGORY,
        COLUMN_SYMBOL,
        COLUMN_SPEECH_TEXT,
        COLUMN_SUGGESTED_ACTION
    )

    fun alertUri(id: Int): Uri = CONTENT_URI.buildUpon()
        .appendPath(id.toString())
        .build()
}
