package com.example.basketball.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.basketball.service.AgentService;

@Service
public class AgentServiceImpl implements AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentServiceImpl.class);

    private static final Pattern DATETIME_PATTERN = Pattern.compile(
        ".*(现在时间|当前时间|现在是|当前是|今天是几点|今天时间).*"
    );

    private static final Pattern DATE_PATTERN = Pattern.compile(
        ".*(今天|日期|几号|星期几|今天几号|几号|几号了|今天是几号).*"
    );

    private static final Pattern TIME_PATTERN = Pattern.compile(
        ".*(几点|几点钟|几点了|现在几点|当前几点|时间是多少|现在时间).*"
    );

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter WEEKDAY_FORMATTER = DateTimeFormatter.ofPattern("EEEE");

    @Override
    public String answerQuestion(String question) {
        if (question == null || question.trim().isEmpty()) {
            return "请输入您的问题！";
        }

        String trimmedQuestion = question.trim();

        String datetimeAnswer = tryGetDateTimeAnswer(trimmedQuestion);
        if (datetimeAnswer != null) {
            log.info("回答日期时间相关问题: {}", trimmedQuestion);
            return datetimeAnswer;
        }

        String dateAnswer = tryGetDateAnswer(trimmedQuestion);
        if (dateAnswer != null) {
            log.info("回答日期相关问题: {}", trimmedQuestion);
            return dateAnswer;
        }

        String timeAnswer = tryGetTimeAnswer(trimmedQuestion);
        if (timeAnswer != null) {
            log.info("回答时间相关问题: {}", trimmedQuestion);
            return timeAnswer;
        }

        return answerGeneralQuestion(trimmedQuestion);
    }

    private String tryGetDateAnswer(String question) {
        String[] dateKeywords = {"今天", "日期", "几号", "星期几", "今天几号", "几号了", "今天是几号"};
        for (String keyword : dateKeywords) {
            if (question.contains(keyword)) {
                LocalDate today = LocalDate.now();
                String date = today.format(DATE_FORMATTER);
                String weekday = today.format(WEEKDAY_FORMATTER);
                return String.format("今天是%s，%s。", date, weekday);
            }
        }
        return null;
    }

    private String tryGetTimeAnswer(String question) {
        String[] timeKeywords = {"几点", "几点钟", "几点了", "现在几点", "当前几点", "时间是多少"};
        for (String keyword : timeKeywords) {
            if (question.contains(keyword)) {
                LocalTime now = LocalTime.now();
                String time = now.format(TIME_FORMATTER);
                return String.format("现在的时间是%s。", time);
            }
        }
        return null;
    }

    private String tryGetDateTimeAnswer(String question) {
        String[] datetimeKeywords = {"现在时间", "当前时间", "现在是", "当前是", "今天是几点", "今天时间"};
        for (String keyword : datetimeKeywords) {
            if (question.contains(keyword)) {
                LocalDateTime now = LocalDateTime.now();
                String date = now.format(DATE_FORMATTER);
                String weekday = now.format(WEEKDAY_FORMATTER);
                String time = now.format(TIME_FORMATTER);
                return String.format("现在是%s，%s，时间是%s。", date, weekday, time);
            }
        }
        return null;
    }

    private String answerGeneralQuestion(String question) {
        log.info("回答综合知识问题: {}", question);
        
        return generateGeneralAnswer(question);
    }

    private String generateGeneralAnswer(String question) {
        if (question.contains("篮球") || question.contains("NBA") || question.contains("投篮") || 
            question.contains("运球") || question.contains("比赛")) {
            return generateBasketballAnswer(question);
        } else if (question.contains("历史") || question.contains("朝代") || question.contains("古代")) {
            return generateHistoryAnswer(question);
        } else if (question.contains("科学") || question.contains("物理") || question.contains("化学") ||
                   question.contains("生物") || question.contains("宇宙") || question.contains("原子")) {
            return generateScienceAnswer(question);
        } else if (question.contains("技术") || question.contains("编程") || question.contains("电脑") ||
                   question.contains("软件") || question.contains("互联网")) {
            return generateTechAnswer(question);
        } else {
            return generateGeneralKnowledgeAnswer(question);
        }
    }

    private String generateBasketballAnswer(String question) {
        if (question.contains("投篮")) {
            return "🏀 提高投篮命中率的关键在于：\n\n1. **正确姿势**：双脚与肩同宽，膝盖微屈，手肘呈90度\n2. **瞄准点**：瞄准篮筐前沿而非后沿\n3. **发力连贯**：从腿部发力，传导至腰腹，最后手腕拨球\n4. **日常训练**：每天至少投进100个球，培养肌肉记忆\n5. **心理稳定**：保持自信，相信自己的训练成果";
        } else if (question.contains("运球")) {
            return "🏀 运球技巧要点：\n\n1. **手指控球**：用手指而非手掌触球\n2. **头部抬起**：运球时抬头观察场上情况\n3. **重心降低**：保持低重心，便于变向和加速\n4. **换手练习**：左右手均衡发展，交叉运球是基础\n5. **节奏变化**：结合快慢节奏突破防守";
        } else if (question.contains("战术") || question.contains("挡拆")) {
            return "🏀 挡拆战术是篮球中最基础也是最有效的战术之一：\n\n**挡拆配合**：\n- 掩护球员：站在持球队友的防守者侧面或身后\n- 持球球员：利用掩护突破或投篮\n- 后续配合：掩护后可顺下接球，也可外弹投篮\n\n**关键要点**：\n1. 掩护要扎实，不能移动\n2. 及时沟通，确认配合意图\n3. 阅读防守，随机应变";
        } else if (question.contains("NBA")) {
            return "🏀 NBA（美国职业篮球联赛）是世界上水平最高的篮球联赛，成立于1946年。\n\n**知名球队**：洛杉矶湖人、波士顿凯尔特人、金州勇士、迈阿密热火等\n\n**传奇球星**：迈克尔·乔丹、科比·布莱恩特、勒布朗·詹姆斯、斯蒂芬·库里等\n\n**赛季结构**：常规赛（82场）→ 季后赛（淘汰赛）→ NBA总决赛";
        } else {
            return "🏀 篮球是一项充满激情的团队运动！无论是街头篮球还是职业比赛，都需要：\n\n1. **基本功**：运球、传球、投篮、防守\n2. **团队配合**：无球跑动、挡拆、传切\n3. **体能训练**：耐力、爆发力、敏捷性\n4. **篮球智商**：阅读比赛、战术理解\n\n如果你有具体的篮球问题，欢迎随时提问！";
        }
    }

    private String generateHistoryAnswer(String question) {
        if (question.contains("唐朝") || question.contains("唐代")) {
            return "📜 唐朝（618年-907年）是中国历史上最辉煌的朝代之一：\n\n**繁荣原因**：\n1. **政治稳定**：唐太宗开创贞观之治，政治清明\n2. **经济繁荣**：农业、手工业、商业高度发展\n3. **文化开放**：诗词、艺术、宗教全面繁荣\n4. **对外交流**：丝绸之路达到鼎盛，万国来朝\n5. **制度创新**：完善科举制度，选拔人才\n\n**文化成就**：唐诗达到巅峰，李白、杜甫、白居易等诗圣辈出。";
        } else if (question.contains("秦始皇") || question.contains("统一六国")) {
            return "📜 秦始皇统一六国（公元前221年）：\n\n**统一过程**：\n1. 公元前230年灭韩\n2. 公元前228年灭赵\n3. 公元前225年灭魏\n4. 公元前223年灭楚\n5. 公元前222年灭燕\n6. 公元前221年灭齐\n\n**历史意义**：\n- 结束春秋战国500年分裂局面\n- 统一文字、度量衡、货币\n- 修建万里长城和灵渠\n- 确立郡县制，影响深远";
        } else if (question.contains("三国")) {
            return "📜 三国时期（220年-280年）是中国历史上英雄辈出的时代：\n\n**著名战役**：\n1. **官渡之战**（200年）：曹操击败袁绍，统一北方\n2. **赤壁之战**（208年）：孙刘联军火烧赤壁，奠定三国鼎立\n3. **夷陵之战**（221-222年）：陆逊火烧连营，蜀汉国力大损\n\n**主要势力**：\n- 曹魏：曹操、曹丕，占据北方\n- 蜀汉：刘备、诸葛亮，占据益州\n- 东吴：孙权，占据江南";
        } else {
            return "📜 中国历史悠久，从夏朝到清朝，经历了无数朝代更替和历史变迁。\n\n**主要朝代顺序**：\n夏→商→周→秦→汉→三国→晋→南北朝→隋→唐→宋→元→明→清\n\n**重大事件**：\n- 秦始皇统一中国\n- 汉武帝开疆拓土\n- 唐朝贞观之治\n- 宋代商业繁荣\n- 明朝郑和下西洋\n\n如果你想了解某个具体朝代或事件，欢迎提问！";
        }
    }

    private String generateScienceAnswer(String question) {
        if (question.contains("天空") || question.contains("蓝色")) {
            return "🔬 天空呈现蓝色是由于**瑞利散射**现象：\n\n**科学原理解释**：\n1. 太阳光包含七种颜色的光\n2. 蓝光波长较短，更容易被大气中的分子散射\n3. 散射后的蓝光向各个方向传播\n4. 我们的眼睛接收到这些散射光，就看到天空是蓝色的\n\n**有趣现象**：\n- 日出日落时天空呈红色/橙色（光线穿过更长大气层，蓝光被散射殆尽）\n- 月球上没有大气，天空是黑色的";
        } else if (question.contains("相对论")) {
            return "🔬 相对论是爱因斯坦提出的物理学理论，分为狭义相对论和广义相对论：\n\n**狭义相对论（1905年）**：\n- 光速不变原理：真空中光速恒定\n- 时间膨胀：运动的时钟变慢\n- 长度收缩：运动的物体缩短\n- 质能方程：E=mc²\n\n**广义相对论（1915年）**：\n- 引力是时空弯曲的表现\n- 质量越大，时空弯曲越明显\n- 解释了水星近日点进动等现象\n\n相对论彻底改变了人类对时间、空间和引力的理解。";
        } else if (question.contains("DNA") || question.contains("双螺旋")) {
            return "🔬 DNA双螺旋结构的发现是20世纪最伟大的科学成就之一：\n\n**发现过程**：\n1. 1953年，沃森和克里克在剑桥大学发现DNA双螺旋结构\n2. 他们利用了富兰克林的X射线衍射数据\n3. 这一发现揭开了遗传信息的奥秘\n\n**DNA结构特点**：\n- 两条反向平行的核苷酸链\n- 碱基配对：A-T、C-G\n- 双螺旋直径约2纳米\n\n这一发现奠定了分子生物学的基础。";
        } else {
            return "🔬 科学探索永无止境！从微观粒子到浩瀚宇宙，人类一直在探索未知。\n\n**科学分支**：\n- 物理学：研究物质和能量\n- 化学：研究物质的组成和变化\n- 生物学：研究生命现象\n- 天文学：研究天体和宇宙\n\n**科学方法**：\n观察→提出假设→实验验证→得出结论\n\n如果你有具体的科学问题，欢迎提问！";
        }
    }

    private String generateTechAnswer(String question) {
        if (question.contains("Python") || question.contains("JavaScript")) {
            return "💻 Python vs JavaScript：\n\n**Python**：\n- 语法简洁优雅，易于学习\n- 适合数据科学、人工智能、后端开发\n- 解释型语言，运行速度较慢\n- 代表框架：Django、Flask、TensorFlow\n\n**JavaScript**：\n- 浏览器原生语言，前端必备\n- 也可用于后端（Node.js）\n- 事件驱动，适合实时应用\n- 代表框架：React、Vue、Express\n\n**选择建议**：\n- 数据科学/AI → Python\n- 前端开发 → JavaScript\n- 全栈开发 → 两者都学";
        } else if (question.contains("机器学习")) {
            return "💻 学习机器学习的路径：\n\n**入门阶段**：\n1. 掌握数学基础：线性代数、微积分、概率论\n2. 学习Python编程\n3. 熟悉NumPy、Pandas、Matplotlib等库\n\n**进阶阶段**：\n1. 学习经典算法：线性回归、决策树、SVM等\n2. 掌握深度学习框架：TensorFlow或PyTorch\n3. 实践项目：图像识别、自然语言处理等\n\n**学习资源**：\n- Coursera机器学习课程（Andrew Ng）\n- fast.ai课程\n- Kaggle竞赛平台\n\n坚持实践是掌握机器学习的关键！";
        } else if (question.contains("区块链")) {
            return "💻 区块链是一种去中心化的分布式账本技术：\n\n**核心特点**：\n1. **去中心化**：没有中央服务器\n2. **不可篡改**：数据一旦写入难以修改\n3. **透明公开**：所有节点都有完整副本\n4. **共识机制**：节点间通过算法达成一致\n\n**应用领域**：\n- 加密货币（比特币、以太坊）\n- 供应链管理\n- 数字身份认证\n- 智能合约\n\n区块链技术正在改变众多行业的运作方式。";
        } else {
            return "💻 技术世界日新月异，从互联网到人工智能，技术正在深刻改变我们的生活。\n\n**热门技术方向**：\n- 人工智能与机器学习\n- 云计算与大数据\n- 区块链与Web3\n- 物联网（IoT）\n- 量子计算\n\n**学习建议**：\n1. 选择一个方向深入\n2. 多动手实践项目\n3. 参与开源社区\n4. 保持持续学习\n\n如果你有具体的技术问题，欢迎提问！";
        }
    }

    private String generateGeneralKnowledgeAnswer(String question) {
        if (question.contains("学习") || question.contains("技能")) {
            return "📚 高效学习新技能的方法：\n\n**学习原则**：\n1. **明确目标**：知道自己要学什么、达到什么水平\n2. **拆解知识**：将复杂技能分解成小模块\n3. **刻意练习**：专注于薄弱环节，反复练习\n4. **及时反馈**：获取反馈，不断调整\n5. **输出倒逼**：通过教别人或写笔记巩固知识\n\n**学习技巧**：\n- 费曼学习法：用简单语言解释复杂概念\n- 间隔重复：定期复习，加深记忆\n- 主动回忆：不看书，尝试回忆知识点\n\n坚持和专注是学习的关键！";
        } else if (question.contains("时间管理")) {
            return "⏰ 时间管理的有效方法：\n\n**实用技巧**：\n1. **四象限法则**：区分重要紧急的事情\n2. **番茄工作法**：25分钟专注+5分钟休息\n3. **任务清单**：每天列出待办事项\n4. **优先级排序**：先做最重要的事\n5. **避免干扰**：关闭手机通知，专注工作\n\n**时间管理工具**：\n- Todo清单：Todo清单类APP\n- 日历：Google日历、Outlook\n- 专注APP：Forest、Focus@Will\n\n记住：时间是最宝贵的资源，合理利用每一天！";
        } else if (question.contains("压力") || question.contains("焦虑")) {
            return "🧘 缓解工作压力的方法：\n\n**心理调节**：\n1. **深呼吸**：腹式呼吸，放松身心\n2. **正念冥想**：专注当下，减少杂念\n3. **运动锻炼**：跑步、瑜伽、篮球都很棒\n4. **兴趣爱好**：做喜欢的事，转移注意力\n5. **社交沟通**：与朋友家人倾诉\n\n**工作方法**：\n- 合理分配任务，不要过度承诺\n- 学会拒绝，设定工作边界\n- 定期休息，保持精力充沛\n\n关爱自己，身心健康最重要！";
        } else {
            return "🌐 这是一个很好的问题！我可以回答各种领域的问题，包括：\n\n🏀 **篮球相关**：训练技巧、战术分析、NBA资讯\n📜 **历史知识**：中国历史、世界历史、名人故事\n🔬 **科学探索**：物理、化学、生物、天文\n💻 **技术编程**：Python、JavaScript、人工智能\n🏥 **健康生活**：运动恢复、饮食建议、心理健康\n\n如果你有任何问题，随时可以问我！";
        }
    }
}
