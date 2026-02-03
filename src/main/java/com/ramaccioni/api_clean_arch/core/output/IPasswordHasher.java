package com.ramaccioni.api_clean_arch.core.output;

public interface IPasswordHasher {
    String hash(String rawPassword);
}
