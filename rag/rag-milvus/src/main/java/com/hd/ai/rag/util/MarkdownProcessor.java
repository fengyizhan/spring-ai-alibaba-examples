package com.hd.ai.rag.util;

import lombok.extern.log4j.Log4j2;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.InputStream;
import java.util.List;

/**
 * Markdown处理器
 */
@Log4j2
public  class MarkdownProcessor {
    static Parser parser;
    static HtmlRenderer renderer;
    static
    {
        List<Extension> extensions = List.of(StrikethroughExtension.create(),TablesExtension.create());
        parser = Parser.builder().extensions(extensions).build();
        renderer = HtmlRenderer.builder().extensions(extensions).build();
    }
    /**
     * 把文件内容转为markdown内容字符串
     * @param fileInputStream 文件流
     * @return html字符串
     */
    public static String process(InputStream fileInputStream) {
        String html="";
        try {
            String markdown = new String(fileInputStream.readAllBytes(),"utf-8");
            Node document = parser.parse(markdown);
            html = renderer.render(document);
        } catch (Exception e) {
            log.error("MarkdownProcessor异常："+e.getMessage());
        }
        finally {
            return html;
        }
    }
}
