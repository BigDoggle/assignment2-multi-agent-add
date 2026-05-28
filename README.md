# 作业 2：多智能体 ADD 架构设计系统

本项目用于完成软件体系结构课程作业 2。当前小组方案如下：

- 作业完成方式：多智能体
- 基础 LLM：Qwen3-Max
- 智能体框架：Spring AI Alibaba
- 目标案例：`Hotel Pricing System`
- 方法约束：严格按照 `ADD 3.0` 进行 4 轮迭代式架构设计

项目实现的是一个命令行程序。程序启动后，会组织多个智能体协作执行 ADD 工作流，并输出完整的对话日志与报告草稿。

## 1. 项目功能概览

程序会完成以下工作：

1. 加载作业限定知识：
   - `ADD 3.0` 方法说明
   - `Hotel Pricing System` 案例材料
   - 系统约束与提示词策略
2. 组织 5 个角色协作：
   - `COORDINATOR`
   - `ARCHITECT`
   - `QUALITY_ANALYST`
   - `REVIEWER`
   - `RECORDER`
3. 依次执行 4 个 iteration：
   - Iteration 1：建立整体系统结构
   - Iteration 2：识别支撑主要功能的结构
   - Iteration 3：处理可靠性与可用性质量属性
   - Iteration 4：处理开发与运维问题
4. 在每一轮中执行 ADD Step 2 到 Step 7
5. 生成完整过程日志和英文报告草稿

## 2. 项目结构

```text
Assignment 2/
├── pom.xml                                   # Maven 构建配置
├── README.md                                 # 项目说明
├── 2026SoftArch-assignment2.pdf              # 作业原始要求
├── src/
│   ├── main/
│   │   ├── java/cn/edu/softarch/assignment2/
│   │   │   ├── Assignment2Application.java   # Spring Boot 应用入口
│   │   │   ├── agent/
│   │   │   │   └── AgentPromptFactory.java   # 多智能体提示词构造
│   │   │   ├── cli/
│   │   │   │   └── RunAssignmentCommand.java # 命令行执行入口
│   │   │   ├── config/                       # 配置类
│   │   │   ├── domain/                       # ADD 工作流领域模型
│   │   │   ├── knowledge/
│   │   │   │   └── KnowledgeBase.java        # 作业知识加载
│   │   │   ├── llm/                          # Qwen 调用抽象与实现
│   │   │   ├── logging/
│   │   │   │   └── ConversationLogWriter.java# 对话日志输出
│   │   │   └── workflow/
│   │   │       └── AddWorkflow.java          # ADD 工作流主逻辑
│   │   └── resources/
│   │       ├── application.yml               # 运行配置
│   │       ├── knowledge/
│   │       │   ├── add-3.0.md                # ADD 3.0 知识
│   │       │   └── hotel-pricing-system.md   # 案例知识
│   │       └── prompts/
│   │           └── system-policy.md          # 系统提示词策略
│   └── test/
│       └── java/cn/edu/softarch/assignment2/ # 单元测试
├── submission/
│   ├── final-report.docx                     # 最终英文报告
│   ├── final-report.md                       # 最终英文报告源稿
│   ├── cost-summary.md                       # 成本统计
│   └── member-contributions.md               # 成员贡献说明
└── out/
    └── assignment2/
        ├── conversation-log.jsonl            # 完整机器可读日志
        ├── conversation-log.md               # 完整人工可读日志
        └── report-draft.md                   # 模型生成的报告草稿
```

## 3. 运行要求

- Java：建议使用 Homebrew OpenJDK
- Maven：建议使用 Homebrew Maven
- API Key：需要可访问 `Qwen3-Max` 的 `DASHSCOPE_API_KEY`

注意：当前机器默认环境是 JDK 8，不能直接运行本项目。执行命令时需要显式指定 Homebrew 的 Java 与 Maven。

## 4. 测试命令

使用以下命令运行测试：

```bash
env JAVA_HOME=/opt/homebrew/opt/openjdk PATH=/opt/homebrew/opt/openjdk/bin:/opt/homebrew/bin:/usr/bin:/bin /opt/homebrew/bin/mvn test
```

当前已验证结果：

- `Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`
- `BUILD SUCCESS`

## 5. 运行完整作业流程

先配置 API Key：

```bash
export DASHSCOPE_API_KEY="你的真实 DashScope API Key"
```

再运行完整流程：

```bash
env JAVA_HOME=/opt/homebrew/opt/openjdk PATH=/opt/homebrew/opt/openjdk/bin:/opt/homebrew/bin:/usr/bin:/bin /opt/homebrew/bin/mvn spring-boot:run -Dspring-boot.run.arguments="run-assignment --output-dir out/assignment2"
```

如果只想做一次真实联通测试，可以执行：

```bash
env JAVA_HOME=/opt/homebrew/opt/openjdk PATH=/opt/homebrew/opt/openjdk/bin:/opt/homebrew/bin:/usr/bin:/bin /opt/homebrew/bin/mvn spring-boot:run -Dspring-boot.run.arguments="run-assignment --output-dir out/assignment2-smoke --max-turns 1"
```

## 6. 作业成果

本仓库当前已经整理出以下最终成果：

1. 源代码
2. 完整对话日志
3. 最终英文报告
4. 成本统计与成员贡献说明

关键文件如下：

- `submission/final-report.docx`
- `submission/final-report.md`
- `submission/cost-summary.md`
- `submission/member-contributions.md`
- `out/assignment2/conversation-log.jsonl`
- `out/assignment2/conversation-log.md`

其中：

- `submission/final-report.docx` 是最终提交报告的权威版本
- `out/assignment2/` 下的日志用于作为完整过程证据
