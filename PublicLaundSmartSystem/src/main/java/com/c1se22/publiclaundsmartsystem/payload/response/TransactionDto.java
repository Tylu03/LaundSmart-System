package com.c1se22.publiclaundsmartsystem.payload.response;

import com.c1se22.publiclaundsmartsystem.enums.TransactionStatus;
import com.c1se22.publiclaundsmartsystem.enums.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
  private Integer id;
  @Min(value = 1, message = "Amount must be greater than 0")
  private BigDecimal amount;
  private TransactionStatus status;
  private TransactionType type;
  private LocalDateTime timestamp;
  @NotNull(message = "User id is required")
  private Integer userId;
  private String userName;
  @NotNull(message = "Machine id is required")
  private Integer machineId;
  private String machineName;
}
