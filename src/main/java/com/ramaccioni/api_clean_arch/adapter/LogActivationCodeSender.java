package com.ramaccioni.api_clean_arch.adapter;

import com.ramaccioni.api_clean_arch.core.output.IActivationCodeSender;
import org.springframework.stereotype.Component;

@Component
public class LogActivationCodeSender implements IActivationCodeSender {

    @Override
    public void send(String email, String activationCode) {
        System.out.println("[ActivationCode] email=" + email + " code=" + activationCode);
    }
}