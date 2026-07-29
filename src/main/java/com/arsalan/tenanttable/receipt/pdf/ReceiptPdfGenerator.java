package com.arsalan.tenanttable.receipt.pdf;

import com.arsalan.tenanttable.exception.PdfGenerationException;
import com.arsalan.tenanttable.receipt.dto.BusinessInfoDto;
import com.arsalan.tenanttable.receipt.dto.ReceiptItemDto;
import com.arsalan.tenanttable.receipt.dto.ReceiptResponseDto;
import org.openpdf.text.*;
import org.openpdf.text.Font;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class ReceiptPdfGenerator {

    // ---- Palette & Typography -------------------------------------------------
    private static final Color INK = new Color(30, 30, 30);
    private static final Color MUTED = new Color(110, 110, 110);
    private static final Color RULE = new Color(200, 200, 200);
    private static final Color ACCENT = new Color(20, 20, 20);

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 16, Font.BOLD, INK);
    private static final Font SUBTLE_FONT = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, MUTED);
    private static final Font SECTION_FONT = new Font(Font.HELVETICA, 9.5f, Font.BOLD, INK);
    private static final Font LABEL_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL, MUTED);
    private static final Font VALUE_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL, INK);
    private static final Font ITEM_NAME_FONT = new Font(Font.HELVETICA, 9.5f, Font.BOLD, INK);
    private static final Font ITEM_META_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL, MUTED);
    private static final Font TOTAL_LABEL_FONT = new Font(Font.HELVETICA, 10, Font.NORMAL, MUTED);
    private static final Font GRAND_TOTAL_FONT = new Font(Font.HELVETICA, 13, Font.BOLD, ACCENT);
    private static final Font FOOTER_TITLE_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, INK);
    private static final Font FOOTER_SUB_FONT = new Font(Font.HELVETICA, 8.5f, Font.NORMAL, MUTED);

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private void addRule(Document document, float spacingBefore, float spacingAfter)
            throws DocumentException {
        Paragraph holder = new Paragraph();
        holder.setSpacingBefore(spacingBefore);
        holder.setSpacingAfter(spacingAfter);
        document.add(holder);
    }

    private String money(BigDecimal amount) {
        return String.format("%.2f", amount);
    }

    private void addHeader(Document document, ReceiptResponseDto receipt) throws DocumentException {
        BusinessInfoDto business = receipt.getBusinessInfo();

        Paragraph businessName = new Paragraph(business.getBusinessName(), TITLE_FONT);
        businessName.setAlignment(Element.ALIGN_CENTER);
        businessName.setSpacingAfter(2f);
        document.add(businessName);

        Paragraph address = new Paragraph(business.getAddress(), SUBTLE_FONT);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        StringBuilder contactLine = new StringBuilder();
        if (business.getPhone() != null) {
            contactLine.append(business.getPhone());
        }
        if (business.getEmail() != null) {
            if (!contactLine.isEmpty()) contactLine.append("  |  ");
            contactLine.append(business.getEmail());
        }
        if (!contactLine.isEmpty()) {
            Paragraph contact = new Paragraph(contactLine.toString(), SUBTLE_FONT);
            contact.setAlignment(Element.ALIGN_CENTER);
            document.add(contact);
        }

        if (business.getGstNumber() != null && !business.getGstNumber().isBlank()) {
            Paragraph gst = new Paragraph("GSTIN: " + business.getGstNumber(), SUBTLE_FONT);
            gst.setAlignment(Element.ALIGN_CENTER);
            document.add(gst);
        }

        addRule(document, 8f, 6f);
    }

    private void addReceiptDetails(Document document, ReceiptResponseDto receipt) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f});

        addDetailCell(table, "Receipt No.", String.valueOf(receipt.getReceiptNumber()));
        addDetailCell(table, "Order No.", String.valueOf(receipt.getOrderNumber()));
        addDetailCell(table, "Date", DATE_FORMAT.withZone(ZoneId.systemDefault())
                .format(receipt.getPaymentDate()));
        addDetailCell(table, "Table", String.valueOf(receipt.getDiningTable()));
        addDetailCell(table, "Cashier", receipt.getCashier());

        document.add(table);
        addRule(document, 4f, 6f);
    }

    private void addDetailCell(PdfPTable table, String label, String value) {
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + ": ", LABEL_FONT));
        p.add(new Chunk(value == null ? "-" : value, VALUE_FONT));

        PdfPCell cell = new PdfPCell(p);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(3f);
        table.addCell(cell);
    }

    private void addItemsTable(Document document, ReceiptResponseDto receipt) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("ITEMS", SECTION_FONT);
        sectionTitle.setSpacingAfter(4f);
        document.add(sectionTitle);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4.2f, 1.1f, 1.4f, 1.5f});

        addTableHeaderCell(table, "Item", Element.ALIGN_LEFT);
        addTableHeaderCell(table, "Qty", Element.ALIGN_CENTER);
        addTableHeaderCell(table, "Price", Element.ALIGN_RIGHT);
        addTableHeaderCell(table, "Total", Element.ALIGN_RIGHT);

        boolean shaded = false;
        for (ReceiptItemDto item : receipt.getItems()) {
            Color rowColor = shaded ? new Color(247, 247, 247) : Color.WHITE;

            addItemCell(table, item.getItemName(), ITEM_NAME_FONT, Element.ALIGN_LEFT, rowColor);
            addItemCell(table, String.valueOf(item.getQuantity()), ITEM_META_FONT, Element.ALIGN_CENTER, rowColor);
            addItemCell(table, money(item.getUnitPrice()), ITEM_META_FONT, Element.ALIGN_RIGHT, rowColor);
            addItemCell(table, money(item.getTotalPrice()), ITEM_META_FONT, Element.ALIGN_RIGHT, rowColor);

            shaded = !shaded;
        }

        document.add(table);
        addRule(document, 6f, 6f);
    }

    private void addTableHeaderCell(PdfPTable table, String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, LABEL_FONT));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(RULE);
        cell.setBorderWidth(0.75f);
        cell.setPaddingBottom(4f);
        cell.setHorizontalAlignment(alignment);
        table.addCell(cell);
    }

    private void addItemCell(PdfPTable table, String text, Font font, int alignment, Color background) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(4f);
        cell.setPaddingBottom(4f);
        cell.setHorizontalAlignment(alignment);
        cell.setBackgroundColor(background);
        table.addCell(cell);
    }

    private void addTotals(Document document, ReceiptResponseDto receipt) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 1f});

        addTotalRow(table, "Subtotal", money(receipt.getSubtotal()), TOTAL_LABEL_FONT, VALUE_FONT, false);
        addTotalRow(table,
                "Coupon (" + receipt.getCoupon() + "%)",
                "-" + money(receipt.getCouponDiscountAmount()),
                TOTAL_LABEL_FONT, VALUE_FONT, false);
        addTotalRow(table,
                "Tax (" + receipt.getTaxRate().stripTrailingZeros() + "%)",
                money(receipt.getTaxAmount()),
                TOTAL_LABEL_FONT, VALUE_FONT, false);

        document.add(table);
        addRule(document, 4f, 6f);

        PdfPTable grandTotalTable = new PdfPTable(2);
        grandTotalTable.setWidthPercentage(100);
        grandTotalTable.setWidths(new float[]{1f, 1f});
        addTotalRow(grandTotalTable, "TOTAL", money(receipt.getTotalAmount()),
                GRAND_TOTAL_FONT, GRAND_TOTAL_FONT, true);
        document.add(grandTotalTable);

        addRule(document, 6f, 8f);
    }

    private void addTotalRow(PdfPTable table, String label, String value,
                             Font labelFont, Font valueFont, boolean emphasize) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPaddingTop(emphasize ? 3f : 1.5f);
        labelCell.setPaddingBottom(emphasize ? 3f : 1.5f);
        labelCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPaddingTop(emphasize ? 3f : 1.5f);
        valueCell.setPaddingBottom(emphasize ? 3f : 1.5f);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(valueCell);
    }

    private void addPaymentDetails(Document document, ReceiptResponseDto receipt) throws DocumentException {
        Paragraph payment = new Paragraph();
        payment.add(new Chunk("Payment Method: ", LABEL_FONT));
        payment.add(new Chunk(String.valueOf(receipt.getPaymentMethod()), VALUE_FONT));
        payment.setSpacingAfter(10f);
        document.add(payment);
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph thankYou = new Paragraph("Thank You!", FOOTER_TITLE_FONT);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        thankYou.setSpacingBefore(4f);
        document.add(thankYou);

        Paragraph visitAgain = new Paragraph("We hope to see you again soon", FOOTER_SUB_FONT);
        visitAgain.setAlignment(Element.ALIGN_CENTER);
        document.add(visitAgain);
    }

    // ---- Entry point ----------------------------------------------------------

    public byte[] generate(ReceiptResponseDto receipt) {
        Rectangle pageSize = new Rectangle(300f, 1000f);
        Document document = new Document(pageSize, 18, 18, 14, 18);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            addHeader(document, receipt);
            addReceiptDetails(document, receipt);
            addItemsTable(document, receipt);
            addTotals(document, receipt);
            addPaymentDetails(document, receipt);
            addFooter(document);

            document.close();
            return outputStream.toByteArray();

        } catch (DocumentException | IOException e) {
            throw new PdfGenerationException("Failed to generate receipt PDF.", e);
        } finally {
            if (document.isOpen()) document.close();
        }
    }
}