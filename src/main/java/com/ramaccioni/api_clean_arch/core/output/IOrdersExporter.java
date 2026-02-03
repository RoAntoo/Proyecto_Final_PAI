package com.ramaccioni.api_clean_arch.core.output;

import com.ramaccioni.api_clean_arch.core.model.Order;

import java.util.List;

public interface IOrdersExporter {
    String contentType();     // "text/csv"
    String fileExtension();   // "csv"
    byte[] export(List<Order> orders);
}
