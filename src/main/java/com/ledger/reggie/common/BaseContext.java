package com.ledger.reggie.common;

/**
 * @author ledger
 * @version 1.0
 **/

public class BaseContext {
    private static final ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrent(){
        return threadLocal.get();
    }












}
