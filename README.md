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
2. 引入 Starter 依赖：

```
<dependency>
    <groupId>com.gitee.GDUTHZY</groupId>
    <artifactId>spring-ai-mybatisplus-reader</artifactId>
    <version>latest-version</version> </dependency>
```
💻 使用指南 (Usage)

假设你有一张商品表，对应实体类 Product 和服务类 ProductService。现在你想把“已上架”的商品同步到向量数据库中供 AI 检索。

```
import com.hzy.ai.reader.MyBatisPlusDocumentReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Map;

@Service
public class RagSyncService {

    @Resource
    private VectorStore vectorStore;
    
    @Resource
    private ProductService productService; 

    /**
     * 将数据库数据同步至向量知识库
     */
    public void syncProductsToVectorDb() {
        
        // 1. 构造 MyBatis-Plus 查询条件 (只查已上架的商品)
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);

        // 2. 使用 MyBatisPlusDocumentReader 构建读取器
        DocumentReader reader = MyBatisPlusDocumentReader.builder(productService)
                .wrapper(wrapper)
                .pageSize(2000) // 每次安全拉取 2000 条，防止 OOM
                .mapper(product -> {
                    // A. 组装给大模型阅读的自然语言文本 (Content)
                    String content = String.format("商品名称：%s。详细介绍：%s。", 
                                        product.getName(), product.getDesc());
                    
                    // B. 组装用于向量数据库精准过滤的元数据 (Metadata)
                    Map<String, Object> meta = Map.of(
                            "productId", product.getId(),
                            "category", product.getCategory(),
                            "price", product.getPrice()
                    );
                    
                    return new Document(content, meta);
                })
                .build();

        // 3. 一键获取并存入向量数据库！
        vectorStore.add(reader.get());
    }
}
```
⚙️ API 说明
| Builder 方法                    | 说明                              | 默认值        | 是否必填 |
|-------------------------------|---------------------------------|------------|------|
| builder(IService<T> service)  | 传入你业务的 MyBatis-Plus Service     | 无          | 必填   |
| mapper(Function<T, Document>) | 定义实体类到 Spring AI Document 的转换规则 | 无          | 必填   |
| wrapper(Wrapper<T> wrapper)   | MyBatis-Plus 条件构造器，用于过滤数据       | null (查全表) | 选填   |
| pageSize(int size)            | 每次分页查询拉取的数据量，防止内存溢出             | 1000       | 选填   |

⚠️ 注意事项
必须开启分页插件： 本组件底层依赖 MyBatis-Plus 的分页功能进行防 OOM 处理。请确保你的 Spring Boot 项目中已经注册了 MybatisPlusInterceptor 并添加了 PaginationInnerInterceptor。

依赖范围： 为了不污染使用者的环境，本 Starter 将 spring-ai-core 和 mybatis-plus-extension 设为了 provided，请确保调用方的项目中已包含这些基础依赖。





