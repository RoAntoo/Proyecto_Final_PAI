package com.ramaccioni.api_clean_arch.adapter.web.controller;

import com.ramaccioni.api_clean_arch.adapter.web.request.CreateUserRequest;
import com.ramaccioni.api_clean_arch.adapter.web.response.CreateUserResponse;
import com.ramaccioni.api_clean_arch.core.dto.ActivateUserDTO;
import com.ramaccioni.api_clean_arch.core.dto.RegisterUserDTO;
import com.ramaccioni.api_clean_arch.core.input.IActivateUserUseCaseInput;
import com.ramaccioni.api_clean_arch.core.input.IRegisterUserUseCaseInput;
import com.ramaccioni.api_clean_arch.core.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final IRegisterUserUseCaseInput registerUser;
    private final IActivateUserUseCaseInput activateUser;

    public UserController(IRegisterUserUseCaseInput registerUser, IActivateUserUseCaseInput activateUser) {
        this.registerUser = registerUser;
        this.activateUser = activateUser;
    }

    // POST /usuarios
    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest request) {
        RegisterUserDTO dto = new RegisterUserDTO(request.email(), request.password());
        User created = registerUser.execute(dto);

        CreateUserResponse response = new CreateUserResponse(
                created.getId(),
                created.getEmail(),
                created.getStatus(),
                created.getActivationCode(),
                created.getActivationExpiresAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // POST /usuarios/{id}/activacion/{codigo de activacion}
    @PostMapping("/{id}/activacion/{codigo}")
    public ResponseEntity<Void> activateUser(@PathVariable("id") Long id,
                                             @PathVariable("codigo") String codigo) {
        ActivateUserDTO dto = new ActivateUserDTO(id, codigo);
        activateUser.execute(dto);
        return ResponseEntity.noContent().build(); // 204
    }
}
