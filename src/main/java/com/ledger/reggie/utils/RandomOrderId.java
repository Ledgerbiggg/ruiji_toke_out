package com.ledger.reggie.utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author ledger
 * @version 1.0
 **/
public class RandomOrderId {
    public static Long nextID(Long userId){
        long timeStamp = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return timeStamp<<32|userId;
    }
}
