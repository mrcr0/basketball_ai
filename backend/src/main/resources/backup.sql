-- 数据备份记录表
CREATE TABLE IF NOT EXISTS data_backup (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    backup_name VARCHAR(200) NOT NULL,
    backup_type VARCHAR(50),
    backup_path VARCHAR(500),
    file_size BIGINT,
    status VARCHAR(20) DEFAULT 'completed',
    backup_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);