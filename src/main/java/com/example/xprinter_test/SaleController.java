package com.example.xprinter_test;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SaleController {

    private final SaleService saleService;
    private final ThermalPrinterService thermalPrinterService;

    public SaleController(SaleService saleService, ThermalPrinterService thermalPrinterService) {
        this.saleService = saleService;
        this.thermalPrinterService = thermalPrinterService;
    }

    @GetMapping("/sales")
    public String getSales(Model model) {
        model.addAttribute("sale", new Sale());
        model.addAttribute("sales", saleService.getAllSales());
        return "sales";
    }

    @PostMapping("/sales")
    public String saveSale(@ModelAttribute Sale sale) {
        saleService.saveSale(sale);
        return "redirect:/sales";
    }

    @PostMapping("/sales/{id}/print")
    public String printSale(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Sale sale = saleService.getSaleById(id);
            thermalPrinterService.printSaleReceipt(sale);
            redirectAttributes.addFlashAttribute("successMessage", "Receipt printed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Print failed: " + e.getMessage());
        }
        return "redirect:/sales";
    }

    @GetMapping("/printers")
    @ResponseBody
    public String[] listPrinters() {
        return thermalPrinterService.listPrinters();
    }
}
