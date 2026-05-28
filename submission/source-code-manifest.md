# 源代码交付清单

## 核心实现

- `src/main/java/cn/edu/softarch/assignment2/`
  - `Assignment2Application.java`：应用入口
  - `agent/AgentPromptFactory.java`：智能体提示词构造
  - `cli/RunAssignmentCommand.java`：命令行入口
  - `config/`：配置类
  - `domain/`：领域模型
  - `knowledge/KnowledgeBase.java`：知识库加载
  - `llm/`：LLM 调用抽象与 Qwen 网关
  - `logging/ConversationLogWriter.java`：日志写出
  - `workflow/AddWorkflow.java`：ADD 工作流主逻辑

## 资源文件

- `src/main/resources/application.yml`
- `src/main/resources/knowledge/add-3.0.md`
- `src/main/resources/knowledge/hotel-pricing-system.md`
- `src/main/resources/prompts/system-policy.md`

## 测试代码

- `src/test/java/cn/edu/softarch/assignment2/`

## 构建与说明文件

- `pom.xml`
- `README.md`

## 不纳入正式源码交付的运行产物

以下目录或文件属于运行结果或本地工具产物，不作为源码主体：

- `target/`
- `out/`
- `.mybatis/`
- `logs/`
