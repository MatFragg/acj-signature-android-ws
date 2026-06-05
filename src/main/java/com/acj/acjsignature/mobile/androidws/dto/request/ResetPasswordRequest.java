package com.acj.acjsignature.mobile.androidws.dto.request;

import com.acj.acjsignature.mobile.androidws.util.Constants;
import com.acj.acjsignature.mobile.androidws.util.MessageConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank(message = MessageConstants.EMAIL_REQUIRED)
    @Email(message = MessageConstants.EMAIL_INVALID)
    private String email;

    @NotBlank(message = MessageConstants.OTP_REQUIRED)
    @Pattern(regexp = Constants.OTP_PATTERN, message = MessageConstants.OTP_PATTERN)
    private String otp;

    @NotBlank(message = MessageConstants.NEW_PASSWORD_REQUIRED)
    @Size(min = 6, max = Constants.PASSWORD_MAX_LENGTH, message = MessageConstants.NEW_PASSWORD_MIN_LENGTH)
    @Pattern(regexp = Constants.PASSWORD_PATTERN, message = MessageConstants.PASSWORD_PATTERN_MESSAGE)
    private String newPassword;
}
