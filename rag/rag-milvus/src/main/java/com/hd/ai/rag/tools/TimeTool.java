package com.hd.ai.rag.tools;


import com.hd.ai.rag.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 */
@Slf4j
public class TimeTool {

    @Tool(description = "获取当前时间，格式为：年-月-日 时:分:秒")
    public String getTimeNow() {

        Date currentTime=Calendar.getInstance().getTime();
        SimpleDateFormat  dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        String formatDateStr = dateFormat.format(currentTime);
        return formatDateStr;
    }

    @Tool(description = "获取某个城市的当前时间，格式为：年-月-日 时:分:秒")
    public String  getCityTimeMethod(@ToolParam(description = "Time zone id, such as Asia/Shanghai") String timeZoneId) {
        log.info("The current time zone is {}", timeZoneId);
        return String.format("The current time zone is %s and the current time is " + "%s", timeZoneId,
                TimeUtils.getTimeByZoneId(timeZoneId));
    }
}
