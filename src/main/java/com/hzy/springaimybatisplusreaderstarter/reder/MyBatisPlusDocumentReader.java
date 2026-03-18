package com.hzy.springaimybatisplusreaderstarter.reder;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class MyBatisPlusDocumentReader<T> implements DocumentReader {

    private final IService<T> service;
    private final Wrapper<T> queryWrapper; // 允许用户传 MP 的条件构造器 (比如 eq("status", 1))
    private final Function<T, Document> documentMapper; // 核心：用户自己定义怎么把 T 转成 Document
    private final int pageSize; // 分页大小，防 OOM

    // 强制使用 Builder 构造
    private MyBatisPlusDocumentReader(Builder<T> builder) {
        this.service = builder.service;
        this.queryWrapper = builder.queryWrapper;
        this.documentMapper = builder.documentMapper;
        this.pageSize = builder.pageSize;
    }

    @Override
    public List<Document> get() {
        List<Document> allDocuments = new ArrayList<>();
        long current = 1;
        long totalPages = 1; // 初始占位，第一次查询后会被真实总页数覆盖

        log.info("开始通过 MyBatis-Plus 分页读取数据库作为知识库源...");

        // 核心：利用 MyBatis-Plus 的分页机制，分批次把数据拉到内存里
        while (current <= totalPages) {
            Page<T> page = new Page<>(current, pageSize);
            Page<T> result = service.page(page, queryWrapper);
            
            // 更新总页数
            totalPages = result.getPages();

            // 将当前页的实体类列表，转换为 Spring AI 的 Document 列表
            for (T entity : result.getRecords()) {
                try {
                    Document doc = documentMapper.apply(entity);
                    if (doc != null) {
                        allDocuments.add(doc);
                    }
                } catch (Exception e) {
                    log.warn("实体类转换为 Document 失败，已跳过。数据: {}", entity, e);
                }
            }
            
            log.debug("成功读取第 {}/{} 页数据", current, totalPages);
            current++;
        }

        log.info("MyBatis-Plus 读取完毕，共加载了 {} 条 Document", allDocuments.size());
        return allDocuments;
    }

    // ================== 泛型 Builder 建造者 ==================

    public static <T> Builder<T> builder(IService<T> service) {
        return new Builder<>(service);
    }

    public static class Builder<T> {
        private final IService<T> service;
        private Wrapper<T> queryWrapper = null; // 默认查全表
        private Function<T, Document> documentMapper;
        private int pageSize = 1000; // 默认每次查 1000 条

        public Builder(IService<T> service) {
            this.service = service;
        }

        /**
         * 选填：传入 MyBatis-Plus 的条件构造器
         */
        public Builder<T> wrapper(Wrapper<T> wrapper) {
            this.queryWrapper = wrapper;
            return this;
        }

        /**
         * 选填：防 OOM 分页大小
         */
        public Builder<T> pageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        /**
         * 必填：告诉 Reader，如何把你的实体类变成 Document
         */
        public Builder<T> mapper(Function<T, Document> documentMapper) {
            this.documentMapper = documentMapper;
            return this;
        }

        public MyBatisPlusDocumentReader<T> build() {
            if (this.documentMapper == null) {
                throw new IllegalArgumentException("必须提供 documentMapper 来定义实体类到 Document 的转换规则！");
            }
            return new MyBatisPlusDocumentReader<>(this);
        }
    }
}