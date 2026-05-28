# 对话日志交付说明

## 文件 1：conversation-log.jsonl

- 用途：机器可读的完整日志
- 特点：逐条记录时间戳、迭代轮次、ADD 步骤、智能体角色、提示词和响应
- 当前状态：完整运行后已生成，记录总数为 120

## 文件 2：conversation-log.md

- 用途：人工阅读版完整日志
- 特点：适合报告追溯、人工检查与课堂展示
- 当前状态：完整运行后已生成，末尾已覆盖 Iteration 4 / Step 7 / RECORDER

## 覆盖范围

- 四轮迭代全部覆盖
- 每轮包含 Step 2 到 Step 7
- 五个角色全部参与：
  - `COORDINATOR`
  - `ARCHITECT`
  - `QUALITY_ANALYST`
  - `REVIEWER`
  - `RECORDER`

## 使用建议

- `conversation-log.jsonl` 更适合做统计、筛选和自动处理
- `conversation-log.md` 更适合人工阅读与报告取材
- 如果老师要求原始过程证据，优先提交这两个文件，不建议改名或二次改写
