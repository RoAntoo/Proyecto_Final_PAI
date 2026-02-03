package com.ramaccioni.api_clean_arch.core.input;

import com.ramaccioni.api_clean_arch.core.dto.ActivateUserDTO;

public interface IActivateUserUseCaseInput {
    void execute(ActivateUserDTO dto);
}
