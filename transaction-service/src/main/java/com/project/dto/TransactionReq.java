package com.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionReq {

    @NotBlank
    private String receiver;
    @NotBlank
    private String sender;
    @Min(1)
    private Long amount;
    private String comment;
}
