package com.example.controleentregas.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.controleentregas.data.EntregaEntity
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun export(context: Context, entregas: List<EntregaEntity>, nomeArquivo: String) {
        if (entregas.isEmpty()) {
            Toast.makeText(context, "Nenhuma entrega para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        var y = 40f

        val paint = Paint()
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Relatório de Entregas em Aberto", 20f, y, paint)
        y += 40f

        paint.textSize = 12f
        paint.isFakeBoldText = false

        entregas.forEach { entrega ->
            // Aqui idealmente teríamos os nomes, mas por enquanto usamos os IDs
            val text = "ID: ${entrega.id}, Cliente: ${entrega.clienteId}, Bairro: ${entrega.bairroId}, Data: ${entrega.data}, Valor: R$ ${String.format("%.2f", entrega.valor)}"
            canvas.drawText(text, 20f, y, paint)
            y += 20f
            if (y > 800) { // Page break
                document.finishPage(page)
                page = document.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }
        }
        
        val total = entregas.sumOf { it.valor }
        paint.isFakeBoldText = true
        y+= 20f
        canvas.drawText("Total: R$ ${String.format("%.2f", total)}", 20f, y, paint)

        document.finishPage(page)

        savePdf(context, document, nomeArquivo)
    }

    private fun savePdf(context: Context, document: PdfDocument, nomeArquivo: String) {
        try {
            val fos: FileOutputStream
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$nomeArquivo.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)!!
                fos = resolver.openOutputStream(uri) as FileOutputStream
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "$nomeArquivo.pdf")
                fos = FileOutputStream(file)
            }

            document.writeTo(fos)
            fos.close()
            document.close()
            Toast.makeText(context, "PDF salvo em Downloads!", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erro ao salvar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}