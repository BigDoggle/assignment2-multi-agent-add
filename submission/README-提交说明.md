# 提交说明

## 运行环境

- Java：使用 Homebrew OpenJDK
- Maven：使用 Homebrew Maven
- LLM：Qwen3-Max
- 智能体框架：Spring AI Alibaba

## 已验证结果

- 源代码测试通过
- 完整运行已生成 `conversation-log.jsonl`
- 完整运行已生成 `conversation-log.md`
- 完整运行已生成 `report-draft.md`

## 当前验证证据

- `out/assignment2/conversation-log.jsonl` 共 120 条记录
- `out/assignment2/report-draft.md` 包含 Iteration 1 到 Iteration 4
- `out/assignment2/conversation-log.md` 末尾已完成 Iteration 4 / Step 7 / RECORDER
- 使用 Homebrew Java/Maven 重新执行测试后，结果为：
  - `Tests run: 9, Failures: 0, Errors: 0, Skipped: 0`
  - `BUILD SUCCESS`

## 注意事项

- 默认系统 JDK 8 不能直接运行本项目
- 验证和运行时需要显式指定 Homebrew 的 Java 与 Maven
- `report-draft.md` 是原始草稿，不能直接作为最终提交报告
- 最终报告仍需根据作业模板压缩整理，并补齐成本分析、成员贡献和个人反思
- `submission/final-report.docx` 已生成，但当前环境缺少 LibreOffice/soffice，因此未完成渲染后的逐页视觉检查
