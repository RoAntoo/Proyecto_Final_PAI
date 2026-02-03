package jobtest;

import com.ramaccioni.api_clean_arch.core.input.IExpirePendingUsersUseCaseInput;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;
import com.ramaccioni.api_clean_arch.core.usecase.ExpirePendingUsersUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

class JobTests {

    @Test
    void execute_ShouldCallRepoWithNowFromClock() {
        IUserRepository repo = Mockito.mock(IUserRepository.class);
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneId.of("UTC"));

        when(repo.expirePendingUsers(any())).thenReturn(2);

        IExpirePendingUsersUseCaseInput uc = new ExpirePendingUsersUseCase(repo, clock);

        int result = uc.execute();

        Assertions.assertEquals(2, result);
        verify(repo).expirePendingUsers(LocalDateTime.of(2026,1,1,10,0,0));
    }
}
