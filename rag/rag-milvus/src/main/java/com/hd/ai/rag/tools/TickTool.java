package com.hd.ai.rag.tools;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 待办设定工具类
 */
@Slf4j
public class TickTool {


    @Tool(description = "根据用户指定的提醒时间，设置闹钟，到了指定时间提醒用户该待办事项")
    public String  tick(@ToolParam(description = "提醒时间") String tickTime,@ToolParam(description = "提醒内容") String content) {
        String tickContent=String.format("智能助手已经为您设定 于：" + "%s"+ "提醒待办：%s", tickTime,
                content);
        log.info("TickTool tick:", tickContent);
        return tickContent;
    }
}
