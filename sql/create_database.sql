CREATE DATABASE IF NOT EXISTS basketball_training DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE basketball_training;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) UNIQUE COMMENT '手机号',
    `height` DECIMAL(5,2) COMMENT '身高(cm)',
    `weight` DECIMAL(5,2) COMMENT '体重(kg)',
    `position` VARCHAR(20) COMMENT '场上位置：后卫/前锋/中锋',
    `experience_years` INT DEFAULT 0 COMMENT '球龄(年)',
    `weak_points` TEXT COMMENT '薄弱项',
    `training_goal` VARCHAR(200) COMMENT '训练目标',
    `skill_level` VARCHAR(20) DEFAULT '新手' COMMENT '基础水平：新手/进阶/专业',
    `avatar_url` VARCHAR(500) COMMENT '头像URL',
    `role` VARCHAR(20) DEFAULT 'user' COMMENT '角色：user/admin',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0禁用/1启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `basketball_plan` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '计划ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_name` VARCHAR(100) NOT NULL COMMENT '计划名称',
    `training_cycle` VARCHAR(20) NOT NULL COMMENT '训练周期：周/月',
    `cycle_count` INT DEFAULT 1 COMMENT '周期数量',
    `ai_content` LONGTEXT COMMENT 'AI生成训练内容(JSON)',
    `special_focus` VARCHAR(200) COMMENT '专项侧重',
    `is_customized` TINYINT DEFAULT 0 COMMENT '是否自定义修改',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0无效/1有效',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='训练计划表';

CREATE TABLE IF NOT EXISTS `train_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `plan_id` BIGINT COMMENT '关联计划ID',
    `training_item` VARCHAR(100) NOT NULL COMMENT '训练项目',
    `hit_rate` DECIMAL(5,2) COMMENT '命中率(%)',
    `duration` INT COMMENT '时长(分钟)',
    `intensity` VARCHAR(20) COMMENT '训练强度：低/中/高',
    `calories` DECIMAL(8,2) COMMENT '体能消耗(卡路里)',
    `completion_rate` DECIMAL(5,2) COMMENT '完成度(%)',
    `notes` TEXT COMMENT '训练备注',
    `check_in_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`plan_id`) REFERENCES `basketball_plan`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='训练记录表';

CREATE TABLE IF NOT EXISTS `basketball_action` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '动作ID',
    `name` VARCHAR(100) NOT NULL COMMENT '动作名称',
    `category` VARCHAR(50) NOT NULL COMMENT '技术分类：运球/投篮/突破/防守/传球/脚步',
    `difficulty` VARCHAR(20) DEFAULT '新手' COMMENT '难度：新手入门/进阶提升/专业强化',
    `video_url` VARCHAR(500) COMMENT '教学视频URL',
    `image_url` VARCHAR(500) COMMENT '动作图片URL',
    `training_points` TEXT COMMENT '标准训练要点',
    `tips` TEXT COMMENT '注意事项',
    `is_published` TINYINT DEFAULT 1 COMMENT '是否发布',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='篮球动作表';

CREATE TABLE IF NOT EXISTS `tactic_info` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资讯ID',
    `title` VARCHAR(200) NOT NULL COMMENT '标题',
    `content` LONGTEXT COMMENT '内容',
    `type` VARCHAR(20) DEFAULT 'tactic' COMMENT '类型：tactic-战术/knowledge-科普/news-资讯',
    `cover_image` VARCHAR(500) COMMENT '封面图片',
    `is_published` TINYINT DEFAULT 1 COMMENT '是否上架',
    `view_count` INT DEFAULT 0 COMMENT '浏览量',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='战术资讯表';

CREATE TABLE IF NOT EXISTS `dynamic` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` TEXT NOT NULL COMMENT '动态内容',
    `image_urls` TEXT COMMENT '图片URL(JSON数组)',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `comment_count` INT DEFAULT 0 COMMENT '评论数',
    `is_approved` TINYINT DEFAULT 1 COMMENT '是否审核通过',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区动态表';

CREATE TABLE IF NOT EXISTS `comment` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    `dynamic_id` BIGINT NOT NULL COMMENT '动态ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父评论ID',
    `is_approved` TINYINT DEFAULT 1 COMMENT '是否审核通过',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    FOREIGN KEY (`dynamic_id`) REFERENCES `dynamic`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

CREATE TABLE IF NOT EXISTS `training_template` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '模板ID',
    `name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `position` VARCHAR(20) COMMENT '适用位置',
    `skill_level` VARCHAR(20) COMMENT '适用水平',
    `content` LONGTEXT COMMENT '模板内容(JSON)',
    `is_active` TINYINT DEFAULT 1 COMMENT '是否启用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='训练模板表';

INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', '管理员', 'admin', 1);

INSERT INTO `basketball_action` (`name`, `category`, `difficulty`, `training_points`, `tips`) VALUES
('原地单手肩上投篮', '投篮', '新手入门', '1. 双脚与肩同宽，身体正对篮筐\n2. 持球于肩上，手肘弯曲成90度\n3. 瞄准篮筐前沿，手腕发力拨球\n4. 跟随动作保持平衡', '注意手腕放松，不要用手臂力量推球'),
('行进间运球', '运球', '新手入门', '1. 身体降低重心，运球高度不超过腰部\n2. 手指自然张开，用手指和手掌边缘控球\n3. 抬头观察场上情况，不要看球\n4. 运球节奏均匀', '初学者可以先练习原地运球，再练习行进间'),
('胸前传球', '传球', '新手入门', '1. 双手持球于胸前，拇指相对成\"八\"字形\n2. 传球时手臂快速伸展，手腕外翻\n3. 传球后手指指向传球方向\n4. 保持传球力度适中', '注意传球时脚步稳定，不要跳传'),
('交叉步突破', '突破', '进阶提升', '1. 降低重心，用外侧脚做交叉步\n2. 运球手保护球，避免被断\n3. 突破时眼睛看前方\n4. 突破后准备上篮或分球', '注意突破时的速度和节奏变化'),
('防守滑步', '防守', '新手入门', '1. 双脚平行开立，重心降低\n2. 用侧步移动，保持身体平衡\n3. 手臂张开，干扰对方传球\n4. 始终面对进攻球员', '防守时不要轻易起跳，保持身体稳定'),
('三步上篮', '投篮', '进阶提升', '1. 运球中跨出第一步，同时抱球\n2. 第二步跨步起跳，身体向上伸展\n3. 第三步在空中将球投出\n4. 落地时保持平衡', '注意三步的节奏，避免走步'),
('背后运球', '运球', '进阶提升', '1. 身体降低重心，运球手从身后绕过\n2. 另一只手准备接球\n3. 控制好运球力度和方向\n4. 眼睛看前方', '先练习原地背后运球，再练习行进间'),
('急停跳投', '投篮', '专业强化', '1. 运球中突然急停，双脚落地\n2. 快速调整身体，正对篮筐\n3. 起跳投篮，动作连贯\n4. 落地时保持平衡', '需要良好的下肢力量和身体协调性'),
('欧洲步', '突破', '专业强化', '1. 运球接近防守球员\n2. 先用一只脚向一侧跨步\n3. 快速变向用另一只脚突破\n4. 起跳上篮', '需要良好的控球能力和变向速度'),
('卡位抢篮板', '防守', '进阶提升', '1. 提前预判篮球落点\n2. 用身体挡住对手\n3. 起跳时双手高举\n4. 抢到篮板后保护好球', '卡位时注意不要犯规');

INSERT INTO `tactic_info` (`title`, `content`, `type`, `is_published`) VALUES
('基础挡拆战术详解', '挡拆是篮球比赛中最基础也是最常用的战术之一。挡拆战术由两人配合完成：\n\n1. 掩护队员：站在持球队友的侧前方，用身体挡住防守球员\n2. 持球队员：利用掩护突破或投篮\n3. 配合要点：掩护要扎实，传球要及时\n\n挡拆战术的变化：\n- 顺下：掩护后切入篮下\n- 外弹：掩护后拉到外线投篮\n- 换防应对：利用换防后的错位进攻', 'tactic', 1),
('2-3联防战术解析', '2-3联防是一种常见的区域防守战术：\n\n站位要求：\n- 两名后卫站在罚球线延长线附近\n- 三名前锋/中锋站在底线附近\n\n防守要点：\n- 保持区域内的沟通\n- 及时补位协防\n- 干扰外线投篮\n\n破解方法：\n- 外线多点传球\n- 利用内线空切\n- 突破分球', 'tactic', 1),
('篮球热身运动指南', '热身是篮球训练的重要环节，能够有效预防受伤：\n\n1. 动态拉伸（5-10分钟）\n   - 慢跑、高抬腿、开合跳\n   - 弓步走、侧弓步\n   - 手臂环绕、肩部拉伸\n\n2. 专项热身（5分钟）\n   - 运球练习\n   - 传球练习\n   - 投篮练习\n\n3. 注意事项\n   - 热身强度逐渐增加\n   - 不要过度拉伸\n   - 保持身体微微出汗', 'knowledge', 1),
('篮球运动常见伤病预防', '篮球运动强度较大，需要注意预防伤病：\n\n常见伤病：\n- 脚踝扭伤：做好脚踝保护，加强脚踝力量训练\n- 膝盖损伤：避免过度变向和跳跃，做好膝盖热身\n- 肌肉拉伤：充分热身，避免突然发力\n- 手指挫伤：接球时注意手型\n\n预防措施：\n- 做好热身运动\n- 佩戴护具\n- 保持正确姿势\n- 合理安排训练强度', 'knowledge', 1),
('篮球比赛规则科普', '篮球比赛基本规则：\n\n1. 比赛时间：NBA48分钟，FIBA40分钟\n2. 犯规次数：个人5次或6次罚下\n3. 违例类型：走步、两次运球、回场等\n4. 得分规则：三分线外3分，内线2分，罚球1分\n\n常见判罚：\n- 阻挡犯规：防守方非法阻挡进攻路线\n- 进攻犯规：进攻方撞人\n- 技术犯规：非体育行为', 'knowledge', 1);

INSERT INTO `training_template` (`name`, `position`, `skill_level`, `content`) VALUES
('后卫基础训练计划', '后卫', '新手', '{"day1":{"name":"运球基础","items":[{"name":"原地运球","sets":3,"duration":10,"points":"重心降低，手指控球"}],"day2":{"name":"传球训练","items":[{"name":"胸前传球","sets":3,"duration":15},{"name":"击地传球","sets":2,"duration":10}]},"day3":{"name":"投篮训练","items":[{"name":"定点投篮","sets":5,"shotsPerSet":10},{"name":"罚球练习","sets":3,"shotsPerSet":15}]},"day4":{"name":"体能训练","items":[{"name":"折返跑","sets":4,"distance":50},{"name":"跳绳","duration":15}]},"day5":{"name":"综合训练","items":[{"name":"运球突破","sets":3},{"name":"急停跳投","sets":3}]},"day6":{"name":"实战对抗","items":[{"name":"3v3比赛","duration":45}]},"day7":{"name":"休息与恢复","items":[]}}}'),
('前锋综合训练计划', '前锋', '进阶', '{"day1":{"name":"投篮训练","items":[{"name":"中距离投篮","sets":5,"shotsPerSet":12},{"name":"三分球练习","sets":4,"shotsPerSet":10}]},"day2":{"name":"运球突破","items":[{"name":"交叉步突破","sets":3},{"name":"欧洲步","sets":2}]},"day3":{"name":"篮板与防守","items":[{"name":"卡位抢篮板","sets":3},{"name":"防守滑步","sets":4}]},"day4":{"name":"体能训练","items":[{"name":"冲刺跑","sets":6,"distance":40},{"name":"俯卧撑","sets":4,"reps":20}]},"day5":{"name":"战术配合","items":[{"name":"挡拆配合","sets":3},{"name":"空切跑位","sets":3}]},"day6":{"name":"实战对抗","items":[{"name":"5v5比赛","duration":60}]},"day7":{"name":"休息与恢复","items":[]}}}'),
('中锋内线训练计划', '中锋', '进阶', '{"day1":{"name":"内线脚步","items":[{"name":"背身单打脚步","sets":3},{"name":"勾手投篮","sets":4}]},"day2":{"name":"篮板训练","items":[{"name":"卡位训练","sets":3},{"name":"抢篮板练习","sets":4}]},"day3":{"name":"防守训练","items":[{"name":"盖帽练习","sets":3},{"name":"低位防守","sets":4}]},"day4":{"name":"体能训练","items":[{"name":"深蹲","sets":4,"reps":15},{"name":"仰卧起坐","sets":4,"reps":30}]},"day5":{"name":"进攻技巧","items":[{"name":"转身投篮","sets":3},{"name":"抛投","sets":3}]},"day6":{"name":"实战对抗","items":[{"name":"5v5比赛","duration":60}]},"day7":{"name":"休息与恢复","items":[]}}}');

INSERT INTO `dynamic` (`user_id`, `content`, `is_approved`) VALUES
(1, '今天完成了AI生成的训练计划，感觉非常专业！运球训练效果明显提升，推荐给大家！', 1),
(1, '分享一下我的投篮训练心得：每天坚持500次投篮，命中率从40%提升到了55%！', 1);
