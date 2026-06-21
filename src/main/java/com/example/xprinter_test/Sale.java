package com.example.xprinter_test;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Data
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String itemName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;

    private LocalDateTime saleDate;
}
