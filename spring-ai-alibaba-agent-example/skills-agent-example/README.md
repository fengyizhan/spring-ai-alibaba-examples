# Skills Agent Example

## 项目简介

本项目演示了如何使用 Spring AI Alibaba 的 Agent Framework 构建一个支持动态加载skill的ReActAgent。通过 Skills 机制，Agent 可以根据用户需求自动识别并调用相应的技能来完成复杂任务。


## 快速开始

### 配置

1. 设置环境变量或在 `application.yml` 中配置 DashScope API Key：

```bash
export AI_DASHSCOPE_API_KEY=your_api_key_here
```

2. 或直接修改 `src/main/resources/application.yml`：

```yaml
spring:
  ai:
    dashscope:
      api-key: your_api_key_here
```

### 运行

```bash
# 编译项目
mvn clean package

# 运行应用
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 测试

使用 POST 请求与 Agent 对话：

```bash
curl -X POST "http://localhost:8080/chat?message=搜索关于深度学习的最新论文"
```

## 核心组件说明

### SkillsAgent

`SkillsAgent` 是 Agent 的核心构建器，负责：

1. **技能加载**：通过 `SkillsInterceptor` 自动扫描 `skills` 目录
2. **工具注册**：注册文件操作和 Shell 执行工具
3. **Agent 构建**：创建配置完整的 ReactAgent 实例


## 内置 Skills

### 1. arxiv-search

搜索 arXiv 预印本论文库。

**功能**：
- 搜索物理、数学、计算机科学等领域的学术论文
- 支持自定义返回结果数量
- 按相关性排序

**使用示例**：
```
"帮我搜索关于蛋白质折叠预测的最新论文"
"查找深度学习在药物发现中的应用研究"
```

### 2. web-search

结构化的 Web 研究流程。

**功能**：
- 复杂主题的多源信息收集
- 系统化的研究计划和执行
- 综合分析和报告生成

**使用示例**：
```
"研究一下当前主流的云服务提供商对比"
"调查最新的前端框架发展趋势"
```

### 3. skill-creator

创建新技能的指南。

**功能**：
- 技能设计最佳实践
- 技能结构和组织规范
- 渐进式信息披露模式

### 创建自定义 Skill

1. 在 `skills` 目录下创建新文件夹
2. 创建 `SKILL.md` 文件，包含 YAML frontmatter 和使用说明
3. （可选）添加脚本、参考文档或资产文件
4. 重启应用，技能将自动加载

**SKILL.md 模板**：

```markdown
---
name: my-skill
description: 技能描述，包括功能和使用场景
---

# My Skill

## 功能说明

描述技能的主要功能...

## 使用方法

提供使用示例和说明...

## 注意事项

列出重要的注意事项...
```
也可以利用这个 agent 中内置的 skill-creator 去用自然语言创建 skill