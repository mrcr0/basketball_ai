-- 初始化篮球训练数据库表

-- 用户表
CREATE TABLE IF NOT EXISTS app_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    height DECIMAL(5,2),
    weight DECIMAL(5,2),
    position VARCHAR(20),
    experience_years INT,
    weak_points TEXT,
    training_goal TEXT,
    skill_level VARCHAR(20),
    avatar_url VARCHAR(255),
    role VARCHAR(20) DEFAULT 'user',
    status INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 训练计划表
CREATE TABLE IF NOT EXISTS basketball_plan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    plan_name VARCHAR(100) NOT NULL,
    training_cycle VARCHAR(20),
    cycle_count INT,
    ai_content TEXT,
    special_focus VARCHAR(100),
    is_customized INT DEFAULT 0,
    status INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 训练动作表
CREATE TABLE IF NOT EXISTS basketball_action (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    difficulty VARCHAR(20),
    video_url VARCHAR(255),
    image_url VARCHAR(255),
    training_points TEXT,
    tips TEXT,
    is_published INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 训练记录表
CREATE TABLE IF NOT EXISTS train_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    plan_id BIGINT,
    training_item VARCHAR(100),
    hit_rate DECIMAL(5,2),
    duration INT,
    intensity VARCHAR(20),
    calories DECIMAL(8,2),
    completion_rate DECIMAL(5,2),
    notes TEXT,
    check_in_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id),
    FOREIGN KEY (plan_id) REFERENCES basketball_plan(id)
);

-- 战术信息表
CREATE TABLE IF NOT EXISTS tactic_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    type VARCHAR(50),
    cover_image VARCHAR(255),
    is_published INT DEFAULT 1,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 动态表
CREATE TABLE IF NOT EXISTS dynamic (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    content TEXT,
    image_urls TEXT,
    video_urls TEXT,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    type VARCHAR(50) DEFAULT 'text',
    is_approved INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 动态表补丁：为已有数据库添加缺失的列
ALTER TABLE dynamic ADD COLUMN IF NOT EXISTS video_urls TEXT AFTER image_urls;
ALTER TABLE dynamic ADD COLUMN IF NOT EXISTS type VARCHAR(50) DEFAULT 'text' AFTER comment_count;

-- 评论表
CREATE TABLE IF NOT EXISTS comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dynamic_id BIGINT,
    user_id BIGINT,
    content TEXT,
    parent_id BIGINT,
    is_approved INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dynamic_id) REFERENCES dynamic(id)
);

-- 训练模板表
CREATE TABLE IF NOT EXISTS training_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(20),
    skill_level VARCHAR(20),
    content TEXT,
    is_active INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 比赛表
CREATE TABLE IF NOT EXISTS game_match (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    match_name VARCHAR(200) NOT NULL,
    home_team VARCHAR(100) NOT NULL,
    away_team VARCHAR(100) NOT NULL,
    home_score INT DEFAULT 0,
    away_score INT DEFAULT 0,
    status VARCHAR(20) DEFAULT '未开始',
    current_quarter INT DEFAULT 1,
    remaining_time INT DEFAULT 720,
    home_possession BOOLEAN DEFAULT TRUE,
    start_time TIMESTAMP NULL,
    end_time TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 登录日志表
CREATE TABLE IF NOT EXISTS login_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    login_ip VARCHAR(50),
    user_agent VARCHAR(500),
    login_status VARCHAR(20),
    error_message VARCHAR(500),
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    operation_type VARCHAR(50),
    operation_module VARCHAR(50),
    operation_desc VARCHAR(500),
    request_url VARCHAR(500),
    request_method VARCHAR(10),
    request_params TEXT,
    response_code INT,
    response_message VARCHAR(500),
    operation_ip VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- AI对话会话表
CREATE TABLE IF NOT EXISTS ai_conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(100) NOT NULL,
    topic VARCHAR(255),
    domain VARCHAR(50),
    message_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_message_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0
);

-- AI消息表
CREATE TABLE IF NOT EXISTS ai_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT,
    session_id VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    domain VARCHAR(50),
    token_count INT,
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    FOREIGN KEY (conversation_id) REFERENCES ai_conversation(id)
);

-- 密码重置Token表
CREATE TABLE IF NOT EXISTS password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME NOT NULL,
    used INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES app_user(id)
);

-- 篮球知识库表
CREATE TABLE IF NOT EXISTS basketball_knowledge (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL COMMENT '篮球规则/战术理论/技术要领/训练方法',
    sub_category VARCHAR(50) COMMENT '子分类',
    tags VARCHAR(200) COMMENT '标签，逗号分隔',
    difficulty VARCHAR(20) COMMENT '难度：新手入门/进阶提升/专业强化',
    cover_image VARCHAR(255),
    is_published INT DEFAULT 1,
    view_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_published (is_published)
);

-- 赛事资讯表
CREATE TABLE IF NOT EXISTS sports_news (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(300) NOT NULL,
    summary VARCHAR(500) COMMENT '摘要',
    content TEXT NOT NULL,
    news_type VARCHAR(50) NOT NULL COMMENT '比赛数据/球队动态/球员表现/战术分析/转会新闻',
    league VARCHAR(50) COMMENT 'NBA/CBA/FIBA/奥运会/世界杯/欧洲杯',
    source VARCHAR(100) COMMENT '来源',
    source_url VARCHAR(500) COMMENT '来源链接',
    tags VARCHAR(200) COMMENT '标签',
    match_id BIGINT COMMENT '关联比赛ID',
    team_names VARCHAR(200) COMMENT '涉及球队',
    player_names VARCHAR(200) COMMENT '涉及球员',
    cover_image VARCHAR(255),
    is_published INT DEFAULT 1,
    is_verified INT DEFAULT 0 COMMENT '是否通过审核',
    view_count INT DEFAULT 0,
    publish_time TIMESTAMP NULL COMMENT '发布时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_news_type (news_type),
    INDEX idx_league (league),
    INDEX idx_publish_time (publish_time),
    INDEX idx_published (is_published)
);
