package com.example.basketball.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 数据库迁移启动器
 * 在应用启动时自动执行必要的DDL变更（列添加等安全操作）
 */
@Configuration
public class DatabaseMigrationRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseMigrationRunner(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void run(ApplicationArguments args) {
        addVideoUrlsColumn();
    }

    /**
     * 为 dynamic 表添加 video_urls 列（若不存在）
     */
    private void addVideoUrlsColumn() {
        try {
            String sql = "ALTER TABLE dynamic ADD COLUMN video_urls TEXT AFTER image_urls";
            jdbcTemplate.execute(sql);
            log.info("Database migration: added video_urls column to dynamic table");
        } catch (Exception e) {
            // 列已存在时忽略（MySQL 1060 Duplicate column）
            log.debug("Database migration skipped (column may already exist): {}", e.getMessage());
        }
    }
}
