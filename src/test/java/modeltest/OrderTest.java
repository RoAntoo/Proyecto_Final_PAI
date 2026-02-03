package modeltest;

import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidOrderTransitionException;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private User mockUser;

    private Clock fixedClock;
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final BigDecimal VALID_AMOUNT = new BigDecimal("199.90");

    @BeforeEach
    void setUp() {
        // Fijamos fecha: 2026-05-20 10:00:00 UTC
        fixedClock = Clock.fixed(Instant.parse("2026-05-20T10:00:00Z"), UTC);
    }

    @Test
    void testCreateOrder_HappyPath() {
        // Act
        Order order = Order.create(mockUser, VALID_AMOUNT, fixedClock);

        // Assert
        Assertions.assertNotNull(order);
        Assertions.assertEquals(OrderStatus.PENDING, order.getStatus());
        Assertions.assertEquals(VALID_AMOUNT, order.getAmount());
        Assertions.assertEquals(mockUser, order.getUser());

        // Verificamos fechas exactas
        LocalDateTime expectedTime = LocalDateTime.of(2026, 5, 20, 10, 0, 0);
        Assertions.assertEquals(expectedTime, order.getCreatedAt());
        Assertions.assertEquals(expectedTime, order.getUpdatedAt());
    }

    @Test
    void testOrderTransitions_HappyPath_Approval() {
        // Arrange
        Order order = Order.create(mockUser, VALID_AMOUNT, fixedClock);

        // PENDING -> PROCESSING
        order.startProcessing(fixedClock);
        Assertions.assertEquals(OrderStatus.PROCESSING, order.getStatus());

        // PROCESSING -> APPROVED - Avanzamos el reloj simulado 1 hora
        Clock futureClock = Clock.fixed(Instant.parse("2026-05-20T11:00:00Z"), UTC);
        order.approve(futureClock);

        // Assert
        Assertions.assertEquals(OrderStatus.APPROVED, order.getStatus());
        Assertions.assertEquals(
                LocalDateTime.of(2026, 5, 20, 11, 0, 0),
                order.getUpdatedAt(),
                "El updatedAt deberia actualizarse al cambiar de estado"
        );
    }

    @Test
    void testOrderTransitions_HappyPath_Rejection() {
        // Arrange
        Order order = Order.create(mockUser, VALID_AMOUNT, fixedClock);
        order.startProcessing(fixedClock); // Debe estar en processing antes de rechazar

        // Act
        order.reject(fixedClock);

        // Assert
        Assertions.assertEquals(OrderStatus.REJECTED, order.getStatus());
    }

    @Test
    void testOrderTransitions_HappyPath_Cancellation() {
        // Arrange
        Order order = Order.create(mockUser, VALID_AMOUNT, fixedClock);

        // PENDING -> CANCELLED (directo desde pending)
        order.cancel(fixedClock);

        // Assert
        Assertions.assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }

    @Test
    void testInvalidTransitions_ShouldThrowException() {
        Order order = Order.create(mockUser, VALID_AMOUNT, fixedClock);

        // Intento ilegal: PENDING -> APPROVED
        Assertions.assertThrows(InvalidOrderTransitionException.class, () ->
                order.approve(fixedClock)
        );

        // Intento ilegal: CANCELLED -> PROCESSING
        order.cancel(fixedClock);
        Assertions.assertThrows(InvalidOrderTransitionException.class, () ->
                order.startProcessing(fixedClock)
        );
    }
}