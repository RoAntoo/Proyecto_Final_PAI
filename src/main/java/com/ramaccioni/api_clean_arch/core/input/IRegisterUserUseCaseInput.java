package com.ramaccioni.api_clean_arch.core.input;

import com.ramaccioni.api_clean_arch.core.dto.RegisterUserDTO;
import com.ramaccioni.api_clean_arch.core.model.User;

public interface IRegisterUserUseCaseInput {
    User execute(RegisterUserDTO dto);
}
