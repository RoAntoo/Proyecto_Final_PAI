package usecasetest;

import com.ramaccioni.api_clean_arch.core.dto.FindOrdersDTO;
import com.ramaccioni.api_clean_arch.core.enums.OrderStatus;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import com.ramaccioni.api_clean_arch.core.model.Order;
import com.ramaccioni.api_clean_arch.core.output.IOrderRepository;
import com.ramaccioni.api_clean_arch.core.usecase.FindOrdersUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOrdersUseCaseTest {

    @Mock
    private IOrderRepository orderRepository;

    private FindOrdersUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindOrdersUseCase(orderRepository);
    }

    @Test
    void testExecute_WithFilters_ShouldCallRepositoryWithCorrectParams() {
        Long userId = 1L;
        OrderStatus status = OrderStatus.PENDING;
        FindOrdersDTO dto = new FindOrdersDTO(userId, status);

        List<Order> expected = List.of(mock(Order.class));
        when(orderRepository.search(userId, status)).thenReturn(expected);

        List<Order> result = useCase.execute(dto);

        Assertions.assertSame(expected, result);

        verify(orderRepository, times(1)).search(userId, status);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testExecute_WithNullFilters_ShouldCallRepositoryWithNulls() {
        FindOrdersDTO dto = new FindOrdersDTO(null, null);

        List<Order> expected = List.of(); // o Collections.emptyList()
        when(orderRepository.search(null, null)).thenReturn(expected);

        List<Order> result = useCase.execute(dto);

        Assertions.assertSame(expected, result);

        verify(orderRepository, times(1)).search(null, null);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testExecute_NullDto_ShouldThrowMissingRequiredFieldException() {
        Assertions.assertThrows(MissingRequiredFieldException.class, () -> useCase.execute(null));

        verify(orderRepository, never()).search(any(), any());
        verifyNoMoreInteractions(orderRepository);
    }
}
