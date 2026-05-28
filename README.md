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

### 2.1 源代码

- `src/main/java/cn/edu/softarch/assignment2/`
  - `Assignment2Application.java`：应用入口
  - `agent/AgentPromptFactory.java`：构造多智能体提示词
  - `cli/RunAssignmentCommand.java`：命令行执行入口
  - `config/`：配置类
  - `domain/`：ADD 工作流领域模型
  - `knowledge/KnowledgeBase.java`：知识库加载
  - `llm/`：Qwen 调用抽象与实现
  - `logging/ConversationLogWriter.java`：日志输出
  - `workflow/AddWorkflow.java`：ADD 工作流主逻辑
- `src/main/resources/`
  - `application.yml`
  - `knowledge/add-3.0.md`
  - `knowledge/hotel-pricing-system.md`
  - `prompts/system-policy.md`
- `src/test/java/cn/edu/softarch/assignment2/`：单元测试

### 2.2 最终提交材料

- `submission/final-report.docx`：最终英文报告
- `submission/final-report.md`：最终英文报告的 Markdown 源稿
- `submission/cost-summary.md`：交互成本统计
- `submission/member-contributions.md`：成员贡献与反思素材
- `submission/source-code-manifest.md`：源码交付清单
- `submission/log-delivery-note.md`：日志交付说明
- `submission/README-提交说明.md`：提交补充说明
- `submission/交付清单.md`：最终交付核对清单

### 2.3 运行输出

完整运行后会生成以下文件：

- `out/assignment2/conversation-log.jsonl`
- `out/assignment2/conversation-log.md`
- `out/assignment2/report-draft.md`

这些文件属于运行产物，默认不纳入 Git 管理。

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

## 6. 当前已完成的作业成果

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

## 7. 当前 Git 管理策略

为了保持仓库干净，以下内容默认不纳入 Git：

- `target/`
- `out/`
- `logs/`
- `.mybatis/`
- `docs/`
- Word 临时锁文件
- 报告生成过程中的测试性文档和已废弃图片产物

这意味着仓库主要保留：

- 可复现的源代码
- 最终需要提交的文档材料
- 必要的说明文件

## 8. 说明

- `submission/final-report.docx` 可能经过人工格式微调，因此如无明确需要，不应自动重写该文件。
- `out/assignment2/report-draft.md` 是模型原始生成草稿，不应直接作为最终报告提交。
- 如果需要重新生成最终报告，建议先确认是否会覆盖已经完成的人工格式调整。
