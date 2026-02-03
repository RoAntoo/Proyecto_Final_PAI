package com.ramaccioni.api_clean_arch.adapter.export;

import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.output.IOrdersExporter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CsvOrdersExporter implements IOrdersExporter {

    private static final char SEP = ';';

    @Override
    public String contentType() {
        // Podés dejar "text/csv". Si querés ser más específico:
        // return "text/csv; charset=utf-8";
        return "text/csv";
    }

    @Override
    public String fileExtension() {
        return "csv";
    }

    @Override
    public byte[] export(List<Order> orders) {
        StringBuilder sb = new StringBuilder();

        // Header
        sb.append("id").append(SEP)
                .append("userId").append(SEP)
                .append("status").append(SEP)
                .append("amount").append(SEP)
                .append("createdAt").append(SEP)
                .append("updatedAt")
                .append('\n');

        // Rows
        for (Order o : orders) {
            sb.append(csv(o.getId())).append(SEP)
                    .append(csv(o.getUser() != null ? o.getUser().getId() : null)).append(SEP)
                    .append(csv(o.getStatus())).append(SEP)
                    .append(csv(o.getAmount())).append(SEP)
                    .append(csv(o.getCreatedAt())).append(SEP)
                    .append(csv(o.getUpdatedAt()))
                    .append('\n');
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String csv(Object value) {
        if (value == null) return "";

        String s = String.valueOf(value);

        boolean needQuotes =
                s.indexOf(SEP) >= 0 ||
                        s.contains("\"") ||
                        s.contains("\n") ||
                        s.contains("\r");

        if (needQuotes) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }

        return s;
    }
}
