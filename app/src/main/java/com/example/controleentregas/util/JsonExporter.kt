package com.example.controleentregas.util

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.controleentregas.data.BackupData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

object JsonExporter {

    fun export(context: Context, backupData: BackupData, nomeArquivo: String) {
        try {
            val jsonString = Json { prettyPrint = true }.encodeToString(backupData)
            
            val fos: FileOutputStream
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$nomeArquivo.json")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)!!
                fos = resolver.openOutputStream(uri) as FileOutputStream
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "$nomeArquivo.json")
                fos = FileOutputStream(file)
            }

            fos.write(jsonString.toByteArray())
            fos.close()
            Toast.makeText(context, "JSON salvo em Downloads!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao salvar JSON: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}