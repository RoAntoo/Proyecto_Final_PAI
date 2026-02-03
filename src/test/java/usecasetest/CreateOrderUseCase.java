package usecasetest;

import com.ramaccioni.api_clean_arch.core.dto.CreateOrderDTO;
import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.enums.UserStatus;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidOrderAmountException;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserNotActiveException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserNotFoundException;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.model.User;
import com.ramaccioni.api_clean_arch.core.output.IOrderRepository;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;
import com.ramaccioni.api_clean_arch.core.usecase.CreateOrderUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock private IUserRepository userRepository;
    @Mock private IOrderRepository orderRepository;

    private Clock fixedClock;
    private CreateOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-05-20T10:00:00Z"), ZoneId.of("UTC"));
        useCase = new CreateOrderUseCase(userRepository, orderRepository, fixedClock);
    }

    @Test
    void testExecute_HappyPath() {
        // Arrange
        Long userId = 1L;
        BigDecimal amount = new BigDecimal("100.50");
        CreateOrderDTO dto = new CreateOrderDTO(userId, amount);

        // Usuario ACTIVE
        User mockUser = mock(User.class);
        when(mockUser.isActive()).thenReturn(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Order result = useCase.execute(dto);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(OrderStatus.PENDING, result.getStatus());

        // Verificamos decimales
        Assertions.assertEquals(0, new BigDecimal("100.50").compareTo(result.getAmount()));

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testExecute_UserNotActive() {
        // Arrange
        Long userId = 1L;
        CreateOrderDTO dto = new CreateOrderDTO(userId, new BigDecimal("100.00"));

        User mockUser = mock(User.class);
        when(mockUser.isActive()).thenReturn(false);
        when(mockUser.getStatus()).thenReturn(UserStatus.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act & Assert
        Assertions.assertThrows(UserNotActiveException.class, () -> useCase.execute(dto));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void testExecute_UserNotFound() {
        CreateOrderDTO dto = new CreateOrderDTO(99L, BigDecimal.TEN);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> useCase.execute(dto));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void testExecute_InvalidAmount() {
        CreateOrderDTO dto = new CreateOrderDTO(1L, BigDecimal.ZERO);

        Assertions.assertThrows(InvalidOrderAmountException.class, () -> useCase.execute(dto));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void testExecute_MissingRequiredFields_NullDto() {
        Assertions.assertThrows(MissingRequiredFieldException.class, () -> useCase.execute(null));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testExecute_MissingRequiredFields_NullUserId() {
        CreateOrderDTO dto = new CreateOrderDTO(null, new BigDecimal("10.00"));
        Assertions.assertThrows(MissingRequiredFieldException.class, () -> useCase.execute(dto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testExecute_MissingRequiredFields_NullAmount() {
        CreateOrderDTO dto = new CreateOrderDTO(1L, null);
        Assertions.assertThrows(MissingRequiredFieldException.class, () -> useCase.execute(dto));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testExecute_InvalidAmount_Negative() {
        CreateOrderDTO dto = new CreateOrderDTO(1L, new BigDecimal("-1"));
        Assertions.assertThrows(InvalidOrderAmountException.class, () -> useCase.execute(dto));
        verify(orderRepository, never()).save(any());
    }

    // Test para comprobar el redondeo en el usecase
    @Test
    void testExecute_AmountIsScaledToTwoDecimals() {
        Long userId = 1L;
        CreateOrderDTO dto = new CreateOrderDTO(userId, new BigDecimal("10.555")); // 10.56

        User mockUser = mock(User.class);
        when(mockUser.isActive()).thenReturn(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        Order order = useCase.execute(dto);

        Assertions.assertEquals(0, new BigDecimal("10.56").compareTo(order.getAmount()));
    }

}
