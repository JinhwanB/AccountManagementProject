package com.jh.accountmanagement.transaction.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(toBuilder = true)
@ToString
public class TransactionRedisDto {
    private String accountUserId;
    private String accountNum;
    private long accountMoney;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime accountDelDate;

    private long price;
    private String transactionNumber;
    private String transactionType;
    private String transactionResult;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime transactionDate;

    public TransactionCheckDto.Response toCheckResponse() {
        return TransactionCheckDto.Response.builder()
                .accountNum(accountNum)
                .transactionDate(transactionDate)
                .transactionNumber(transactionNumber)
                .transactionResult(transactionResult)
                .transactionPrice(price)
                .transactionType(transactionType)
                .build();
    }
}
