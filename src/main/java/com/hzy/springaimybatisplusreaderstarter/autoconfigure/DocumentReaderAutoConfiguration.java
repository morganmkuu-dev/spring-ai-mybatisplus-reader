package com.hzy.springaimybatisplusreaderstarter.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

@AutoConfiguration
// 只有当用户的环境里有 Spring AI 的 Document 和 MyBatis-Plus 的 IService 时才生效
@ConditionalOnClass({org.springframework.ai.document.Document.class, com.baomidou.mybatisplus.extension.service.IService.class})
public class DocumentReaderAutoConfiguration {
    // 目前不需要在这里 @Bean 注入 Reader，因为 Reader 是用户在使用时通过 Builder 动态构建的。
    // 但保留这个类，以后你可以加全局的 yml 配置读取。
}