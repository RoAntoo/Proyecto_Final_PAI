package com.ramaccioni.api_clean_arch.core.dto;

public record ExportFileDTO(
        String filename,
        String contentType,
        byte[] content
) {
}
