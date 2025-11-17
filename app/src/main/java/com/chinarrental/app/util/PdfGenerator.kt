package com.chinarrental.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    fun generateBillPdf(
        context: Context,
        billNumber: String,
        customerName: String,
        customerPhone: String,
        items: List<BillItem>,
        totalAmount: Double,
        paidAmount: Double,
        date: Long
    ): File {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val fileName = "Bill_${billNumber}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(null), "bills/$fileName")
        file.parentFile?.mkdirs()

        val pdfWriter = PdfWriter(FileOutputStream(file))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        // Header
        document.add(
            Paragraph("CHINAR RENTAL")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20f)
                .setBold()
        )

        document.add(
            Paragraph("RENTAL INVOICE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16f)
        )

        document.add(Paragraph("\n"))

        // Bill Details
        document.add(Paragraph("Bill No: $billNumber").setFontSize(12f))
        document.add(Paragraph("Date: ${dateFormat.format(Date(date))}").setFontSize(12f))
        document.add(Paragraph("Customer: $customerName").setFontSize(12f))
        document.add(Paragraph("Phone: $customerPhone").setFontSize(12f))

        document.add(Paragraph("\n"))

        // Items Table
        val table = Table(UnitValue.createPercentArray(floatArrayOf(40f, 15f, 15f, 15f, 15f)))
            .useAllAvailableWidth()

        // Header
        table.addHeaderCell("Item")
        table.addHeaderCell("Qty")
        table.addHeaderCell("Days")
        table.addHeaderCell("Rate")
        table.addHeaderCell("Amount")

        // Items
        items.forEach { item ->
            table.addCell(item.name)
            table.addCell(item.quantity.toString())
            table.addCell(item.days.toString())
            table.addCell(String.format("%.2f", item.ratePerDay))
            table.addCell(String.format("%.2f", item.amount))
        }

        document.add(table)
        document.add(Paragraph("\n"))

        // Totals
        document.add(
            Paragraph("Total Amount: Rs. ${String.format("%.2f", totalAmount)}")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(14f)
                .setBold()
        )
        document.add(
            Paragraph("Paid Amount: Rs. ${String.format("%.2f", paidAmount)}")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12f)
        )
        document.add(
            Paragraph("Balance: Rs. ${String.format("%.2f", totalAmount - paidAmount)}")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(14f)
                .setBold()
        )

        document.add(Paragraph("\n\n"))
        document.add(
            Paragraph("Thank you for your business!")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10f)
        )

        document.close()
        return file
    }

    data class BillItem(
        val name: String,
        val quantity: Int,
        val days: Int,
        val ratePerDay: Double,
        val amount: Double
    )
}
