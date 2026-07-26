package com.arsalan.tenanttable.receipt.pdf;

import com.arsalan.tenanttable.exception.PdfGenerationException;
import com.arsalan.tenanttable.receipt.dto.BusinessInfoDto;
import com.arsalan.tenanttable.receipt.dto.ReceiptItemDto;
import com.arsalan.tenanttable.receipt.dto.ReceiptResponseDto;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


@Component
public class ReceiptPdfGenerator {
    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 12, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.HELVETICA, 11);
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 9);
    private static final Font BOLD_FONT = new Font(Font.HELVETICA, 11, Font.BOLD);

    private void addHeader(Document document, ReceiptResponseDto receipt)
            throws DocumentException {
        BusinessInfoDto business = receipt.getBusinessInfo();

        Paragraph businessName = new Paragraph(business.getBusinessName(), TITLE_FONT);
        businessName.setAlignment(Element.ALIGN_CENTER);
        document.add(businessName);

        Paragraph address = new Paragraph(business.getAddress(), NORMAL_FONT);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        if (business.getPhone() != null) {
            Paragraph phone = new Paragraph("Phone: " + business.getPhone(), NORMAL_FONT);
            phone.setAlignment(Element.ALIGN_CENTER);
            document.add(phone);
        }

        if (business.getEmail() != null) {
            Paragraph email = new Paragraph(business.getEmail(), NORMAL_FONT);
            email.setAlignment(Element.ALIGN_CENTER);
            document.add(email);
        }

        if (business.getGstNumber() != null && !business.getGstNumber().isBlank()) {
            Paragraph gst = new Paragraph("GSTIN: " + business.getGstNumber(), NORMAL_FONT);
            gst.setAlignment(Element.ALIGN_CENTER);
            document.add(gst);
        }

        document.add(createSeparator());
    }

    private Paragraph createSeparator() {
        Paragraph separator = new Paragraph(
                "------------------------------------------------",
                SMALL_FONT
        );
        separator.setAlignment(Element.ALIGN_CENTER);
        return separator;
    }

    private void addReceiptDetails(Document document, ReceiptResponseDto receipt)
            throws DocumentException {

        document.add(new Paragraph(
                "Receipt No : " + receipt.getReceiptNumber(),
                NORMAL_FONT
        ));

        document.add(new Paragraph(
                "Order No   : " + receipt.getOrderNumber(),
                NORMAL_FONT
        ));

        document.add(new Paragraph(
                "Date       : " + formatDate(receipt.getPaymentDate()),
                NORMAL_FONT
        ));

        document.add(new Paragraph(
                "Table      : " + receipt.getDiningTable(),
                NORMAL_FONT
        ));

        document.add(new Paragraph(
                "Cashier    : " + receipt.getCashier(),
                NORMAL_FONT
        ));

        document.add(createSeparator());
    }

    private String formatDate(Instant instant) {
        return DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a")
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

    private void addItemsTable(Document document, ReceiptResponseDto receipt)
            throws DocumentException {

        document.add(new Paragraph("Items", HEADER_FONT));
        document.add(createSeparator());

        for (ReceiptItemDto item : receipt.getItems()) {
            Paragraph itemName = new Paragraph(item.getItemName(), BOLD_FONT);
            document.add(itemName);

            Paragraph details = new Paragraph(
                    String.format(
                            "%d x %.2f = %.2f",
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getTotalPrice()
                    ),
                    NORMAL_FONT
            );

            document.add(details);
            document.add(Chunk.NEWLINE);
        }

        document.add(createSeparator());
    }

    private Paragraph createAmountRow(String label, BigDecimal amount) {

        Paragraph paragraph = new Paragraph(
                String.format(
                        "%-15s %.2f",
                        label + ":",
                        amount
                ),
                NORMAL_FONT
        );

        paragraph.setAlignment(Element.ALIGN_RIGHT);

        return paragraph;
    }

    private void addTotals(Document document, ReceiptResponseDto receipt)
            throws DocumentException {

        document.add(createAmountRow("Subtotal", receipt.getSubtotal()));
        document.add(createAmountRow(
                "Discount (" + receipt.getDiscountRate().stripTrailingZeros() + "%)",
                receipt.getDiscountAmount()
        ));
        document.add(createAmountRow(
                "Tax (" + receipt.getTaxRate().stripTrailingZeros() + "%)",
                receipt.getTaxAmount()
        ));

        document.add(createSeparator());

        Paragraph total = new Paragraph(
                String.format(
                        "TOTAL : %.2f",
                        receipt.getTotalAmount()
                ),
                HEADER_FONT
        );

        total.setAlignment(Element.ALIGN_RIGHT);

        document.add(total);

        document.add(createSeparator());
    }

    private void addPaymentDetails(Document document, ReceiptResponseDto receipt)
            throws DocumentException {

        document.add(new Paragraph(
                "Payment Method : " + receipt.getPaymentMethod(),
                NORMAL_FONT
        ));

        document.add(Chunk.NEWLINE);
    }

    private void addFooter(Document document)
            throws DocumentException {

        Paragraph thankYou = new Paragraph("Thank You!", HEADER_FONT);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        document.add(thankYou);

        Paragraph visitAgain = new Paragraph("Visit Again", NORMAL_FONT);
        visitAgain.setAlignment(Element.ALIGN_CENTER);
        document.add(visitAgain);
    }

    public byte[] generate(ReceiptResponseDto receipt) {
        // paper size 80mm
        Rectangle pageSize = new Rectangle(226f, 800f);
        Document document = new Document(pageSize, 10, 10, 10, 10);
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
