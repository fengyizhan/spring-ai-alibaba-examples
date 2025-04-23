package com.alibaba.cloud.ai.example.rag.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import lombok.RequiredArgsConstructor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * - @des 文件内容提取工具
 */
@Component
@RequiredArgsConstructor
public class TikaUtil {
    // 目标段落长度（汉字字符数）
    private static final int TARGET_LENGTH = 500;
    // 允许的段落长度浮动范围（±20字）
    private static final int LENGTH_TOLERANCE = 50;

    public String extractTextString(MultipartFile file) {
        try {
    // 创建解析器--在不确定文档类型时候可以选择使用AutoDetectParser可以自动检测一个最合适的解析器
            Parser parser = new AutoDetectParser();
    // 用于捕获文档提取的文本内容。-1 参数表示使用无限缓冲区,解析到的内容通过此hander获取
            BodyContentHandler bodyContentHandler = new BodyContentHandler(-1);
    // 元数据对象，它在解析器中传递元数据属性---可以获取文档属性
            Metadata metadata = new Metadata();
    // 带有上下文相关信息的ParseContext实例，用于自定义解析过程。
            ParseContext parseContext = new ParseContext();
            parser.parse(file.getInputStream(), bodyContentHandler, metadata, parseContext);
    // 获取文本
            return bodyContentHandler.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 使用HanLP进行句子分割
     *
     * @param text 输入的大文本
     * @return 段落列表，每个段落至少包含minLength个字符
     */
    public static List<String> splitParagraphsHanLP(String text) {
        List<String> paragraphs = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return paragraphs;
        }
        // 1. 使用 HanLP 分词并分句
        List<String> sentences = splitSentences(text);

        // 2. 动态合并句子到段落
        paragraphs = mergeSentencesIntoParagraphs(sentences);

        return paragraphs;
    }

    // 使用 HanLP 分词实现分句
    private static List<String> splitSentences(String text) {
        List<String> sentences = new ArrayList<>();
        StringBuilder currentSentence = new StringBuilder();
        List<Term> terms = HanLP.segment(text);

        for (Term term : terms) {
            currentSentence.append(term.word);
            // 使用正则表达式匹配句子结束标点（支持中英文标点）
            if (term.word.matches("[。！？；.!?;]+")) {
                sentences.add(currentSentence.toString());
                currentSentence.setLength(0);
            }
        }

        // 添加最后一个句子（如果没有标点结尾）
        if (!currentSentence.isEmpty()) {
            sentences.add(currentSentence.toString());
        }

        return sentences;
    }

    // 动态合并句子到段落
    private static List<String> mergeSentencesIntoParagraphs(List<String> sentences) {
        List<String> paragraphs = new ArrayList<>();
        StringBuilder currentParagraph = new StringBuilder();
        int currentLength = 0;

        for (String sentence : sentences) {
            int sentenceLength = countChineseChars(sentence);

            // 处理超长句子（强制分割）
            if (sentenceLength > TARGET_LENGTH + LENGTH_TOLERANCE) {
                if (currentLength > 0) {
                    paragraphs.add(currentParagraph.toString());
                    currentParagraph.setLength(0);
                    currentLength = 0;
                }
                // 按标点二次分割超长句
                List<String> subSentences = splitLongSentence(sentence);
                paragraphs.addAll(subSentences);
                continue;
            }

            // 合并到当前段落的条件
            if (currentLength + sentenceLength <= TARGET_LENGTH + LENGTH_TOLERANCE) {
                currentParagraph.append(sentence);
                currentLength += sentenceLength;
            } else {
                // 当前段落达到长度，保存并重置
                paragraphs.add(currentParagraph.toString());
                currentParagraph.setLength(0);
                currentParagraph.append(sentence);
                currentLength = sentenceLength;
            }
        }

        // 添加最后一个段落
        if (currentLength > 0) {
            paragraphs.add(currentParagraph.toString());
        }

        return paragraphs;
    }

    // 处理超长句子：按逗号、分号等二次分割
    private static List<String> splitLongSentence(String sentence) {
        List<String> validParts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int currentLength = 0;

        // 按标点分割句子
        String[] parts = sentence.split("[,;；，]");
        for (String part : parts) {
            int partLength = countChineseChars(part);
            if (currentLength + partLength > TARGET_LENGTH + LENGTH_TOLERANCE) {
                // 当前部分过长，保存并重置
                validParts.add(current.toString());
                current.setLength(0);
                currentLength = 0;
            }
            // 补回分割符号
            current.append(part).append("，");
            currentLength += partLength;
        }

        // 添加最后一个部分
        if (!current.isEmpty()) {
            validParts.add(current.toString());
        }

        return validParts;
    }

    // 统计中文字符数（忽略标点、英文）
    private static int countChineseChars(String text) {
        return (int) text.chars()
                .filter(c -> Character.UnicodeScript.of(c) == Character.UnicodeScript.HAN)
                .count();
    }

}
