package com.hd.ai.rag.common;

import java.util.UUID;

/**
 * ID生成器
 */
public class IdUtil {
    public static String getSnowflakeNextIdStr()
    {
        return UUID.randomUUID().toString().replace("-", "");
    }
    public static void main(String args[])
    {
        System.out.println(getSnowflakeNextIdStr());
    }
}
