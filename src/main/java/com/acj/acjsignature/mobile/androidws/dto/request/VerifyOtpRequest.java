package com.acj.acjsignature.mobile.androidws.dto.request;

import com.acj.acjsignature.mobile.androidws.util.Constants;
import com.acj.acjsignature.mobile.androidws.util.MessageConstants;
import jakarta.validation.constraints.Email;
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
public class VerifyOtpRequest {

    @NotBlank(message = MessageConstants.EMAIL_REQUIRED)
    @Email(message = MessageConstants.EMAIL_INVALID)
    private String email;

    @NotBlank(message = MessageConstants.OTP_REQUIRED)
    @Pattern(regexp = Constants.OTP_PATTERN, message = MessageConstants.OTP_PATTERN)
    private String otp;
}
