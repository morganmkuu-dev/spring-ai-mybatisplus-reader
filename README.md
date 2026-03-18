🚀 Spring AI MyBatis-Plus Reader
Spring AI MyBatis-Plus Reader 是一个专为 Spring AI 和 MyBatis-Plus 生态打造的高性能、防内存溢出（OOM）的关系型数据库文档读取器（DocumentReader）。

它可以帮助你极其优雅地将 MySQL、PostgreSQL 等关系型数据库中的业务数据，一键转换为带有精准元数据（Metadata）的向量知识库（Vector Store），是构建企业级 RAG（检索增强生成）应用的核心利器。

✨ 核心特性 (Features)
🛡️ 绝对防 OOM： 抛弃危险的全表 SELECT *，底层深度融合 MyBatis-Plus 分页插件，支持自定义游标大小，百万级数据也能安全、平滑入库。

🔗 极简生态接入： 完美兼容 MyBatis-Plus 的 IService 和 QueryWrapper，无需手写繁琐的 SQL，用你最熟悉的 Lambda 表达式过滤数据。

🏷️ 高度自定义元数据： 提供极其灵活的转换接口，让你自由决定哪些字段拼接为大模型阅读的内容（Content），哪些字段提取为精准过滤的标签（Metadata）。

📦 开箱即用： 标准的 Spring Boot Starter 架构，零配置污染。

📦 快速引入 (Quick Start)
注意： 本项目目前通过 JitPack 进行分发，请确保你的项目中已引入了 spring-ai-core 和 mybatis-plus-extension。

1. 在你的 pom.xml 中添加 JitPack 仓库：

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

