package com.example.xprinter_test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
public class ThermalPrinterService {

    // ESC/POS command bytes
    private static final byte[] INIT         = {0x1B, 0x40};
    private static final byte[] ALIGN_LEFT   = {0x1B, 0x61, 0x00};
    private static final byte[] ALIGN_CENTER = {0x1B, 0x61, 0x01};
    private static final byte[] BOLD_ON      = {0x1B, 0x45, 0x01};
    private static final byte[] BOLD_OFF     = {0x1B, 0x45, 0x00};
    private static final byte[] FULL_CUT     = {0x1D, 0x56, 0x00};
    private static final byte   LF           = 0x0A;

    @Value("${printer.name}")
    private String printerName;

    public void printSaleReceipt(Sale sale) throws Exception {
        PrintService printService = findPrinter();
        byte[] receipt = buildReceipt(sale);
        DocPrintJob job = printService.createPrintJob();
        Doc doc = new SimpleDoc(receipt, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);
        job.print(doc, new HashPrintRequestAttributeSet());
    }

    public String[] listPrinters() {
        return Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .map(PrintService::getName)
                .toArray(String[]::new);
    }

    private PrintService findPrinter() {
        return Arrays.stream(PrintServiceLookup.lookupPrintServices(null, null))
                .filter(s -> s.getName().toLowerCase().contains(printerName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Printer not found: \"" + printerName + "\". Visit /printers to see available printer names."));
    }

    private byte[] buildReceipt(Sale sale) throws Exception {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String div = "--------------------------------\n";

        out.write(INIT);

        // Header
        out.write(ALIGN_CENTER);
        out.write(div.getBytes("UTF-8"));
        out.write(BOLD_ON);
        out.write("MY STORE\n".getBytes("UTF-8"));
        out.write(BOLD_OFF);
        out.write("Sales Receipt\n".getBytes("UTF-8"));
        out.write(div.getBytes("UTF-8"));

        // Sale details
        out.write(ALIGN_LEFT);
        out.write(("Customer : " + sale.getCustomerName() + "\n").getBytes("UTF-8"));
        out.write(("Item     : " + sale.getItemName() + "\n").getBytes("UTF-8"));
        out.write(("Qty      : " + sale.getQuantity() + "\n").getBytes("UTF-8"));
        out.write(String.format("Unit     : Rs. %.2f\n", sale.getUnitPrice()).getBytes("UTF-8"));
        out.write(String.format("Total    : Rs. %.2f\n", sale.getTotalPrice()).getBytes("UTF-8"));
        out.write(("Date     : " + sale.getSaleDate().format(fmt) + "\n").getBytes("UTF-8"));

        // Footer
        out.write(ALIGN_CENTER);
        out.write(div.getBytes("UTF-8"));
        out.write("Thank You!\n".getBytes("UTF-8"));
        out.write(div.getBytes("UTF-8"));

        // Feed 3 lines then cut
        out.write(LF);
        out.write(LF);
        out.write(LF);
        out.write(FULL_CUT);

        return out.toByteArray();
    }
}
