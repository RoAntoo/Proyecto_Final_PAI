package com.ramaccioni.api_clean_arch.core.output;

public interface IActivationCodeSender {
    void send(String email, String activationCode);
}
