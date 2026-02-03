package com.ramaccioni.api_clean_arch.core.input;

import com.ramaccioni.api_clean_arch.core.dto.ExportFileDTO;
import com.ramaccioni.api_clean_arch.core.dto.FindOrdersDTO;

public interface IExportOrdersUseCaseInput {
    ExportFileDTO execute(FindOrdersDTO dto);
}
