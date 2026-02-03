package exporttest;

import com.ramaccioni.api_clean_arch.adapter.export.CsvOrdersExporter;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

class CSVExportTest {

    @Test
    void export_ShouldIncludeHeaderAndRows() {
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneId.of("UTC"));
        User u = User.create("a@a.com", "hash", 30, clock);
        Order o = Order.create(u, new BigDecimal("100.50"), clock);

        CsvOrdersExporter exporter = new CsvOrdersExporter();
        byte[] bytes = exporter.export(List.of(o));
        String csv = new String(bytes, StandardCharsets.UTF_8);

        Assertions.assertTrue(csv.startsWith("id;userId;status;amount;createdAt;updatedAt\n"));
        
        Assertions.assertTrue(csv.contains(";PENDING;100.50;"));
    }
}
