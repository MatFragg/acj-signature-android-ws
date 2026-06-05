package com.acj.acjsignature.mobile.androidws.dto.request;

import com.acj.acjsignature.mobile.androidws.util.Constants;
import com.acj.acjsignature.mobile.androidws.util.MessageConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DniRequest {

    @NotBlank(message = MessageConstants.DNI_REQUIRED)
    @Pattern(regexp = Constants.DNI_PATTERN, message = MessageConstants.DNI_PATTERN)
    private String numero;
}
