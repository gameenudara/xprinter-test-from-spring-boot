package com.example.xprinter_test;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public void saveSale(Sale sale) {
        sale.setTotalPrice(sale.getQuantity() * sale.getUnitPrice());
        sale.setSaleDate(LocalDateTime.now());
        saleRepository.save(sale);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found: " + id));
    }
}
