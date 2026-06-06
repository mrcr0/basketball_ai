package com.example.basketball.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.basketball.client.TencentSportsApiClient;
import com.example.basketball.entity.SportsNews;
import com.example.basketball.mapper.SportsNewsMapper;

@Service
public class SportsNewsService {

    private static final Logger log = LoggerFactory.getLogger(SportsNewsService.class);

    private final SportsNewsMapper newsMapper;
    private final TencentSportsApiClient tencentApiClient;

    public SportsNewsService(SportsNewsMapper newsMapper, TencentSportsApiClient tencentApiClient) {
        this.newsMapper = newsMapper;
        this.tencentApiClient = tencentApiClient;
    }

    public List<SportsNews> getAllNews() {
        return newsMapper.selectList(
            new QueryWrapper<SportsNews>()
                .eq("is_published", 1)
                .orderByDesc("publish_time")
        );
    }

    public List<SportsNews> getLatestNews(int limit) {
        return newsMapper.selectLatest(limit);
    }

    public List<SportsNews> getNewsByType(String newsType, int limit) {
        return newsMapper.selectByType(newsType, limit);
    }

    public List<SportsNews> getNewsByLeague(String league, int limit) {
        return newsMapper.selectByLeague(league, limit);
    }

    public SportsNews getNewsById(Long id) {
        SportsNews news = newsMapper.selectById(id);
        if (news != null) {
            news.setViewCount(news.getViewCount() == null ? 1 : news.getViewCount() + 1);
            newsMapper.updateById(news);
        }
        return news;
    }

    public SportsNews createNews(SportsNews news) {
        if (news.getPublishTime() == null) {
            news.setPublishTime(LocalDateTime.now());
        }
        news.setIsPublished(1);
        news.setIsVerified(1);
        newsMapper.insert(news);
        return news;
    }

    public SportsNews updateNews(Long id, SportsNews news) {
        news.setId(id);
        newsMapper.updateById(news);
        return newsMapper.selectById(id);
    }

    public void deleteNews(Long id) {
        newsMapper.deleteById(id);
    }

    /**
     * 获取腾讯体育来源的NBA热门赛事资讯
     */
    public List<SportsNews> getTencentNews() {
        return newsMapper.selectBySource("腾讯体育", 10);
    }

    /**
     * 定时刷新赛事资讯（每30分钟执行一次）
     * 优先从腾讯体育API获取实时数据，API不可用时降级使用模拟数据
     */
    @Scheduled(fixedDelay = 1800000)
    public void scheduledNewsRefresh() {
        log.info("Starting scheduled sports news refresh... [API status: {}]", tencentApiClient.getStats());
        try {
            List<SportsNews> newsList;

            // 第一步：尝试从腾讯体育API获取数据
            List<SportsNews> apiNews = tencentApiClient.fetchAll();
            if (!apiNews.isEmpty()) {
                log.info("Tencent API returned {} news items, using live data", apiNews.size());
                newsList = apiNews;
            } else {
                // 第二步：API未返回数据时，使用模拟数据兜底
                log.info("Tencent API returned no data (enabled={}), falling back to mock data generator",
                        tencentApiClient.isHealthy());
                newsList = generateMockNews();
            }

            // 第三步：逐条入库（去重）
            int addedCount = 0;
            for (SportsNews news : newsList) {
                QueryWrapper<SportsNews> wrapper = new QueryWrapper<>();
                wrapper.eq("title", news.getTitle());
                if (newsMapper.selectCount(wrapper) == 0) {
                    newsMapper.insert(news);
                    addedCount++;
                }
            }
            log.info("Scheduled refresh complete. Source: {}, Added: {} new articles",
                    apiNews.isEmpty() ? "mock" : "tencent_api", addedCount);
        } catch (Exception e) {
            log.error("Scheduled news refresh failed: {}", e.getMessage(), e);
        }
    }

    /**
     * 生成模拟赛事资讯数据
     * 实际部署时可替换为腾讯体育API调用
     */
    private List<SportsNews> generateMockNews() {
        List<SportsNews> news = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        SportsNews n1 = new SportsNews();
        n1.setTitle("NBA季后赛最新战报：东西部半决赛激战正酣");
        n1.setSummary("季后赛第二轮进入白热化阶段，各队展开激烈角逐，多场比赛出现精彩绝杀。");
        n1.setContent("【比赛综述】\n\nNBA季后赛第二轮正在如火如荼地进行中。东部方面，凯尔特人展现了强大统治力，凭借出色的防守和团队配合取得领先优势。雄鹿在字母哥带领下奋力反击，但受到伤病困扰。\n\n西部战况更为激烈。掘金队约基奇延续MVP级别表现，场均接近三双数据令人惊叹。湖人队詹姆斯和戴维斯双核驱动，老将经验在季后赛中发挥重要作用。\n\n【关键数据】\n· 约基奇季后赛场均：28.5分 13.2篮板 9.8助攻\n· 塔图姆季后赛场均：27.1分 10.5篮板 5.3助攻\n· 本轮已出现3次绝杀/准绝杀\n\n【战术分析】\n各队在季后赛中的防守强度明显提升，换防和延误战术使用频率大幅增加。空间型五号位的价值在季后赛中被进一步放大。");
        n1.setNewsType("比赛数据");
        n1.setLeague("NBA");
        n1.setSource("篮球训练平台");
        n1.setTags("NBA,季后赛,比赛数据,战术分析");
        n1.setPublishTime(now);
        news.add(n1);

        SportsNews n2 = new SportsNews();
        n2.setTitle("FIBA世界杯预选赛中国队最新动态");
        n2.setSummary("中国男篮积极备战世界杯预选赛，多名年轻球员获得征召，展现全新面貌。");
        n2.setContent("【球队动态】\n\n中国男篮主教练公布了最新一期集训名单，阵容呈现出年轻化趋势。多名在CBA联赛中表现出色的年轻球员获得征召机会，展现了教练组着眼未来的建队思路。\n\n【阵容分析】\n· 后卫线：更加注重速度和投射能力\n· 锋线：增加了持球进攻型球员的比例\n· 内线：保留了传统中锋优势，同时培养空间型内线\n\n【战术调整】\n新一期国家队在战术上更加现代化：\n1. 增加挡拆后的三分出手比重\n2. 强调快速转换进攻\n3. 防守端采用更多换防和区域联防\n4. 培养多名持球点，减少对单核的依赖\n\n【对手分析】\n预选赛对手实力分析已完成，教练组针对不同对手制定了详细的战术方案。");
        n2.setNewsType("球队动态");
        n2.setLeague("FIBA");
        n2.setSource("篮球训练平台");
        n2.setTags("中国男篮,FIBA,世界杯预选赛,球队动态");
        n2.setPublishTime(now.minusHours(2));
        news.add(n2);

        SportsNews n3 = new SportsNews();
        n3.setTitle("深度战术解析：掘金约基奇高位策应体系的运作原理");
        n3.setSummary("约基奇的独特打法重新定义了中锋位置，其高位策应体系成为联盟最难防守的战术之一。");
        n3.setContent("【战术体系概述】\n\n尼古拉·约基奇的高位策应体系是当今NBA最具创造力的进攻体系之一。与传统的低位背筐中锋不同，约基奇大部分时间在罚球线以上区域活动，利用其卓越的传球视野和中投能力组织进攻。\n\n【核心运作机制】\n\n1. 高位持球威胁\n· 约基奇在罚球线附近接球后拥有三重威胁：传球、投篮、突破\n· 防守中锋被迫离开篮下舒适区\n· 一旦防守大个子拉到外线，内线空间完全敞开\n\n2. 无球跑动配合\n· 穆雷、戈登等球员持续空切篮下\n· 约基奇的手递手传球是一大杀招\n· 底线反跑空切（Backdoor）使用频率极高\n\n3. 阅读防守决策\n· 如果防守中锋不出来→约基奇中投或三分\n· 如果防守中锋扑出来→传球给空切队友\n· 如果防守换防→利用错位优势单打\n· 如果防守包夹→找到空位三分射手\n\n【战术效果】\n· 掘金队进攻效率连续多个赛季联盟前列\n· 助攻率极高：团队篮球的典范\n· 所有角色球员都能在体系中发挥价值\n\n【防守应对困境】\n约基奇的体系难以防守的原因在于：\n1. 没有固定套路的进攻（阅读防守后选择）\n2. 5个位置都能产生威胁\n3. 约基奇自身的得分能力强迫防守做出选择\n4. 一旦防守犯错就会被立即惩罚");
        n3.setNewsType("战术分析");
        n3.setLeague("NBA");
        n3.setSource("篮球训练平台");
        n3.setTags("战术分析,约基奇,掘金,高位策应");
        n3.setPlayerNames("约基奇,穆雷,戈登");
        n3.setTeamNames("丹佛掘金");
        n3.setPublishTime(now.minusHours(6));
        news.add(n3);

        SportsNews n4 = new SportsNews();
        n4.setTitle("奥运会篮球赛事前瞻：各强队阵容分析与夺冠预测");
        n4.setSummary("巴黎奥运会篮球赛事临近，美国、法国、西班牙、加拿大等强队纷纷公布豪华阵容。");
        n4.setContent("【赛事前瞻】\n\n奥运会篮球赛事即将拉开帷幕，多支强队已经公布了参赛阵容。美国男篮虽然缺少部分超级巨星，但依然星光熠熠。法国队坐拥东道主之利，阵容深度令人畏惧。\n\n【强队分析】\n\n美国队：\n· 优势：球员个人能力顶尖，快攻转换能力联盟最强\n· 弱点：磨合时间短，FIBA规则适应度\n· 核心球员：多名全明星级别后卫和锋线\n· 预测：仍是金牌最大热门\n\n法国队：\n· 优势：内线双塔亚洲级别最顶级，防守体系成熟\n· 核心球员：戈贝尔+文班亚马的内线组合\n· 主场优势不容忽视\n· 预测：冲金热门\n\n加拿大队：\n· 优势：NBA球员数量仅次于美国\n· 核心阵容：多个持球攻击点\n· 首次以完整阵容出战奥运会\n· 预测：奖牌有力竞争者\n\n西班牙队：\n· 传统强队，配合默契\n· FIBA体系下如鱼得水\n· 年轻化阵容展现新气象\n\n【FIBA规则影响】\n· 无防守三秒：有利于区域联防\n· 干扰球规则更宽松\n· 三分线略短（6.75m vs NBA 7.24m）\n· 比赛时长40分钟vs NBA 48分钟");
        n4.setNewsType("比赛数据");
        n4.setLeague("奥运会");
        n4.setSource("篮球训练平台");
        n4.setTags("奥运会,篮球,美国队,法国队,西班牙队,阵容分析");
        n4.setPublishTime(now.minusHours(12));
        news.add(n4);

        SportsNews n5 = new SportsNews();
        n5.setTitle("球员技术分析：新一代控球后卫的技术进化趋势");
        n5.setSummary("现代控球后卫的技术特点发生了根本性变化，得分能力成为衡量顶级控卫的核心指标。");
        n5.setContent("【趋势分析】\n\n篮球运动中控球后卫这一位置在过去20年经历了最剧烈的变革。从传统的'传球第一'组织型后卫，到今天集得分、组织、防守于一体的全能型后卫，技术要求的演变反映了篮球运动的整体发展趋势。\n\n【技术进化轨迹】\n\n第一代：传统型控卫（2000年前）\n· 代表：斯托克顿、基德\n· 核心任务：组织进攻、控制节奏\n· 技术特点：传球精准、比赛阅读能力强\n· 得分能力：场均10-15分即可\n\n第二代：双能卫崛起（2000-2010）\n· 代表：纳什、保罗、帕克\n· 核心任务：组织+得分\n· 技术特点：挡拆大师、中距离稳定\n· 得分能力：场均18-22分\n\n第三代：得分型控卫（2010-2020）\n· 代表：库里、维斯布鲁克、利拉德（Lillard）\n· 核心任务：得分优先，组织为辅\n· 技术特点：超远三分、无限射程、爆发力强\n· 得分能力：场均25-30分\n\n第四代：全能型控卫（2020至今）\n· 代表：东契奇、亚历山大、哈利伯顿\n· 核心任务：全能输出（得分+助攻+篮板）\n· 技术特点：身高臂展优势、全方位无死角\n· 得分能力：场均25+同时场均8+助攻\n\n【训练启示】\n青少年控球后卫的培养应注意：\n1. 三分投射是第一优先级技能\n2. 挡拆阅读能力不可或缺\n3. 防守能力不应被忽视\n4. 身体对抗能力需要早期建立");
        n5.setNewsType("球员表现");
        n5.setLeague("NBA");
        n5.setSource("篮球训练平台");
        n5.setTags("技术分析,控球后卫,库里,东契奇,球员发展");
        n5.setPublishTime(now.minusHours(18));
        news.add(n5);

        SportsNews n6 = new SportsNews();
        n6.setTitle("欧洲篮球锦标赛综述：各国联赛与青训体系对比");
        n6.setSummary("欧洲篮球近年来的进步源于完善的青训体系，各国联赛和青训模式值得深入研究。");
        n6.setContent("【欧洲篮球体系概述】\n\n欧洲篮球的国际竞争力在过去十年大幅提升，越来越多的欧洲球员在NBA取得核心位置。这一现象背后是欧洲完善的青训体系和联赛制度。\n\n【各国体系对比】\n\n西班牙ACB联赛：\n· 欧洲第一联赛，青少年梯队完善\n· U12-U18完整联赛体系\n· 皇马、巴萨青训营全球闻名\n· 培养球员：卢比奥、加索尔兄弟、埃尔南戈麦斯兄弟\n\n法国LNB联赛：\n· 近年青训产出爆炸式增长\n· 文班亚马现象的根源\n· 全国标准化训练的青少年训练营\n· 培养球员：文班亚马、戈贝尔、巴图姆、富尼耶\n\n塞尔维亚：\n· 全民篮球文化传统\n· 基层教练水平极高\n· 培养球员：约基奇、博格达诺维奇\n\n德国BBL联赛：\n· 科学化训练体系\n· 大学+俱乐部双轨制\n· 培养球员：诺维茨基、施罗德、瓦格纳\n\n【青训共同特点】\n1. 从U8开始系统化训练\n2. 重视基本功而非早期专项化\n3. 教练员需要持证上岗\n4. 比赛和训练比例合理\n5. 学业和篮球并重\n\n【对中国篮球的启示】\n· 校园篮球与专业训练体系的衔接\n· 基层教练员培养的重要性\n· 多层次联赛体系的建立\n· 青少年比赛机会的增加");
        n6.setNewsType("球队动态");
        n6.setLeague("欧洲杯");
        n6.setSource("篮球训练平台");
        n6.setTags("欧洲篮球,青训体系,西班牙ACB,法国LNB,塞尔维亚");
        n6.setPublishTime(now.minusDays(1));
        news.add(n6);

        // ===== 从社区NBANewsPanel迁移的NBA热点资讯（腾讯体育来源） =====

        SportsNews n7 = new SportsNews();
        n7.setTitle("凯尔特人4-1击败独行侠夺得2025-26赛季NBA总冠军");
        n7.setSummary("布朗荣膺总决赛MVP，塔图姆场均27分带队登顶，绿军时隔18年再夺奥布莱恩杯");
        n7.setContent("【比赛综述】\n\n波士顿凯尔特人在总决赛中以4-1的总比分击败达拉斯独行侠，夺得2025-26赛季NBA总冠军。这是凯尔特人队史第18座总冠军奖杯，超越了洛杉矶湖人的17座，独占NBA历史第一。\n\n【关键数据】\n· 总决赛MVP：杰伦·布朗，场均24.5分6.2篮板5.8助攻\n· 塔图姆系列赛场均：27.2分10.1篮板7.0助攻\n· 凯尔特人系列赛防守效率：104.3（联盟顶级）\n· 独行侠东契奇系列赛场均：31.5分9.8篮板8.3助攻\n\n【战术解析】\n凯尔特人本次总决赛的防守策略堪称经典：\n1. 对东契奇采取延误（Hedge）+快速回位策略，避免被点名换防\n2. 波神和霍福德的内线护框极大限制了独行侠的篮下进攻\n3. 全队三分投射点遍布全场，五外阵容让独行侠防守顾此失彼\n\n【历史意义】\n· 凯尔特人第18冠超越湖人，成为NBA总冠军最多的球队\n· 布朗成为首位以3D球员出身获得总决赛MVP的后卫\n· 塔图姆完成了从'未来之星'到'冠军领袖'的蜕变");
        n7.setNewsType("比赛数据");
        n7.setLeague("NBA");
        n7.setSource("腾讯体育");
        n7.setTags("NBA,总决赛,凯尔特人,独行侠,总冠军");
        n7.setTeamNames("凯尔特人,独行侠");
        n7.setPlayerNames("布朗,塔图姆,东契奇");
        n7.setPublishTime(now.minusMinutes(45));
        n7.setViewCount(285000);
        news.add(n7);

        SportsNews n8 = new SportsNews();
        n8.setTitle("詹姆斯确认将继续征战第23个NBA赛季");
        n8.setSummary("勒布朗·詹姆斯宣布不会退役，将执行下赛季合同，冲击50000分里程碑");
        n8.setContent("【球员动态】\n\n勒布朗·詹姆斯在赛季总结发布会上正式确认，他将继续征战个人第23个NBA赛季。这位39岁的老将表示身体状况依然良好，希望与儿子布朗尼同场竞技。\n\n【历史记录追踪】\n· 目前总得分：48000+分（常规赛+季后赛）\n· 下赛季有望突破50000分大关\n· 已是NBA历史得分王、季后赛得分王、全明星入选次数最多球员\n\n【训练状态】\n据团队透露，詹姆斯休赛期仍保持每天高强度训练：\n· 凌晨5点起床训练已持续20年\n· 每年在身体保养上花费超过150万美元\n· 本赛季场均仍贡献25+7+7的全面数据\n\n【球队前景】\n湖人管理层表示将继续围绕詹姆斯和戴维斯打造争冠阵容，休赛期将积极寻求阵容升级。");
        n8.setNewsType("球队动态");
        n8.setLeague("NBA");
        n8.setSource("腾讯体育");
        n8.setTags("NBA,詹姆斯,湖人,历史得分王");
        n8.setPlayerNames("詹姆斯,戴维斯");
        n8.setTeamNames("湖人");
        n8.setPublishTime(now.minusMinutes(90));
        n8.setViewCount(198000);
        news.add(n8);

        SportsNews n9 = new SportsNews();
        n9.setTitle("东契奇当选2025-26赛季常规赛MVP");
        n9.setSummary("独行侠核心场均32.5分9.8助攻8.2篮板，率队取得西部第一战绩");
        n9.setContent("【MVP分析】\n\n达拉斯独行侠队的卢卡·东契奇以压倒性优势当选2025-26赛季常规赛MVP，这是这位斯洛文尼亚天才职业生涯首次获此殊荣。\n\n【赛季数据】\n· 场均得分：32.5分（联盟第一）\n· 场均助攻：9.8次（联盟第三）\n· 场均篮板：8.2个\n· 真实命中率：61.8%\n· 三双次数：20次（联盟第一）\n\n【战绩贡献】\n· 独行侠常规赛57胜25负，西部第一\n· 东契奇在场时球队进攻效率联盟第一\n· 比赛关键时刻（Clutch Time）得分能力联盟顶级\n· 使用率（Usage Rate）38.5%创职业生涯新高\n\n【技术特点】\n东契奇的技术特点极其全面：\n1. 顶级的节奏控制能力——不依靠速度而是用节奏和身体保护球\n2. 卓越的挡拆阅读——联盟最擅长利用挡拆创造优势的球员\n3. 后撤步三分——招牌技能，不可阻挡\n4. 精英级别的传球视野——6尺7的身高带来天然的传球优势\n\n【历史地位】\n· 25岁即获得MVP，比肩乔丹、詹姆斯等历史巨星\n· 已是4次一阵、5次全明星\n· 带领斯洛文尼亚国家队获得欧锦赛冠军");
        n9.setNewsType("球员表现");
        n9.setLeague("NBA");
        n9.setSource("腾讯体育");
        n9.setTags("NBA,MVP,东契奇,独行侠,数据分析");
        n9.setPlayerNames("东契奇");
        n9.setTeamNames("独行侠");
        n9.setPublishTime(now.minusMinutes(135));
        n9.setViewCount(156000);
        news.add(n9);

        SportsNews n10 = new SportsNews();
        n10.setTitle("文班亚马荣获年度最佳防守球员");
        n10.setSummary("二年级场均4.2盖帽12.8篮板，连续两年蝉联DPOY");
        n10.setContent("【DPOY分析】\n\n圣安东尼奥马刺队的维克托·文班亚马蝉联NBA年度最佳防守球员（DPOY），以惊人的护框能力统治了防守端。\n\n【防守数据】\n· 场均盖帽：4.2次（联盟第一，领先第二名1.4次）\n· 场均篮板：12.8个\n· 防守篮板率：32.1%\n· 干扰投篮次数：场均18.3次\n· 防守效率值：101.2（联盟第一）\n\n【防守影响力】\n· 文班亚马在场时，马刺防守效率联盟前十\n· 文班亚马不在场时，马刺防守效率联盟倒数\n· 对手篮下命中率下降12.7%（联盟最大影响力）\n· 改变了对手的进攻选择（减少篮下进攻比例）\n\n【技术分析】\n文班亚马的防守不仅仅是身高优势：\n1. 7尺4的身高+8尺的臂展创造了史无前例的防守覆盖面积\n2. 卓越的时机感和盖帽判断力\n3. 移动速度快，可以从内线补防到三分线\n4. 换防能力出色，可以防守1-5号位\n\n【历史意义】\n· 成为历史上最年轻的连续DPOY获得者\n· 有望挑战奥拉朱旺的盖帽纪录\n· 重新定义了现代篮球的防守概念");
        n10.setNewsType("球员表现");
        n10.setLeague("NBA");
        n10.setSource("腾讯体育");
        n10.setTags("NBA,DPOY,文班亚马,马刺,防守");
        n10.setPlayerNames("文班亚马");
        n10.setTeamNames("马刺");
        n10.setPublishTime(now.minusMinutes(180));
        n10.setViewCount(134000);
        news.add(n10);

        SportsNews n11 = new SportsNews();
        n11.setTitle("自由市场开启：多位全明星球员进入转会市场");
        n11.setSummary("哈登、乔治、西亚卡姆等人成为自由球员，多支球队展开追逐");
        n11.setContent("【转会动态】\n\nNBA自由市场正式开启，多位全明星级别球员进入转会市场，预计将引发多笔重磅交易。\n\n【重点自由球员】\n\n詹姆斯·哈登：\n· 上赛季场均20.1分8.5助攻\n· 证明了自己仍是顶级的挡拆发起者\n· 湖人、热火等多支球队有意\n\n保罗·乔治：\n· 上赛季场均22.6分5.2篮板\n· 顶级的3D锋线，适配任何体系\n· 76人、勇士可能成为下家\n\n西亚卡姆：\n· 上赛季场均21.7分7.1篮板\n· 全能型大前锋，防守端多位置切换\n· 步行者有意续约\n\n【球队薪资空间】\n· 76人：拥有最大薪资空间（约6000万）\n· 魔术：可开出顶薪合同\n· 雷霆：年轻核心+选秀权是最大筹码\n\n【交易市场预测】\n1. 多位球星可能通过先签后换方式加盟争冠球队\n2. 选秀权将成为交易中的重要筹码\n3. 第二奢侈税线使部分球队薪资压力巨大");
        n11.setNewsType("转会新闻");
        n11.setLeague("NBA");
        n11.setSource("腾讯体育");
        n11.setTags("NBA,转会,自由市场,哈登,乔治");
        n11.setPlayerNames("哈登,乔治,西亚卡姆");
        n11.setPublishTime(now.minusMinutes(210));
        n11.setViewCount(178000);
        news.add(n11);

        SportsNews n12 = new SportsNews();
        n12.setTitle("库里三分命中数突破4500记，刷新历史纪录");
        n12.setSummary("勇士队后卫史蒂芬·库里在对阵雷霆比赛中命中第4500记三分球");
        n12.setContent("【里程碑时刻】\n\n金州勇士队的史蒂芬·库里在对阵俄克拉荷马雷霆的比赛中，命中个人职业生涯第4500记三分球，继续刷新由他自己保持的NBA历史三分纪录。\n\n【历史三分榜】\n1. 库里：4500+记\n2. 雷·阿伦：2973记\n3. 哈登：2900+记\n4. 雷吉·米勒：2560记\n5. 汤普森：2500+记\n\n【库里的三分革命】\n库里彻底改变了篮球运动的打法：\n1. 证明以三分为核心的进攻体系可以夺冠\n2. 超远三分（Logo Shot）从杂技变成常规武器\n3. 重新定义了控球后卫的职责\n4. 篮球培训体系因他而改变（幼年即重视三分训练）\n\n【传奇数据】\n· 场均三分命中数：3.8个（历史第一）\n· 生涯三分命中率：42.5%\n· 单赛季402记三分（历史纪录）\n· 4次NBA总冠军\n· 2次常规赛MVP（含一次全票MVP）\n\n【影响力】\n库里不仅改写纪录，更改变了篮球文化。从NBA到街头球场，三分球成为各年龄段球员的第一选择。");
        n12.setNewsType("球员表现");
        n12.setLeague("NBA");
        n12.setSource("腾讯体育");
        n12.setTags("NBA,库里,三分球,勇士,历史纪录");
        n12.setPlayerNames("库里");
        n12.setTeamNames("勇士");
        n12.setPublishTime(now.minusMinutes(250));
        n12.setViewCount(112000);
        news.add(n12);

        SportsNews n13 = new SportsNews();
        n13.setTitle("NBA全明星周末确定在洛杉矶举办");
        n13.setSummary("2027年NBA全明星周末将在洛杉矶快船新球馆Intuit Dome举行");
        n13.setContent("【全明星资讯】\n\nNBA官方宣布2027年NBA全明星周末将在洛杉矶举办，具体场馆为洛杉矶快船队的新主场——Intuit Dome球馆。\n\n【Intuit Dome球馆介绍】\n· 耗资20亿美元建造\n· 座位数：18000个\n· 拥有世界上最大的双面计分屏幕\n· 每个座位都配备手机充电插座\n· 无现金消费系统\n\n【全明星周末安排】\n· 周五：名人赛 + 新秀挑战赛\n· 周六：技巧挑战赛、三分大赛、扣篮大赛\n· 周日：全明星正赛\n\n【赛事经济影响】\n· 预计为洛杉矶带来约2亿美元的经济效益\n· 酒店、餐饮、旅游行业将全面受益\n· 全球转播覆盖200+国家和地区\n\n【球迷互动】\n· NBA球迷嘉年华同步举办\n· 球星签名见面会\n· 篮球主题展览\n· 青少年篮球训练营");
        n13.setNewsType("花边新闻");
        n13.setLeague("NBA");
        n13.setSource("腾讯体育");
        n13.setTags("NBA,全明星,洛杉矶,Intuit Dome");
        n13.setPublishTime(now.minusMinutes(300));
        n13.setViewCount(89000);
        news.add(n13);

        SportsNews n14 = new SportsNews();
        n14.setTitle("杜兰特超越奥尼尔升至历史得分榜第八位");
        n14.setSummary("太阳队前锋凯文·杜兰特在对阵魔术比赛中完成里程碑");
        n14.setContent("【里程碑时刻】\n\n菲尼克斯太阳队的凯文·杜兰特在对阵奥兰多魔术的比赛中，生涯总得分超越沙奎尔·奥尼尔，升至NBA历史得分榜第八位。\n\n【历史得分榜前八】\n1. 勒布朗·詹姆斯\n2. 卡里姆·阿卜杜尔-贾巴尔\n3. 卡尔·马龙\n4. 科比·布莱恩特\n5. 迈克尔·乔丹\n6. 德克·诺维茨基\n7. 威尔特·张伯伦\n8. 凯文·杜兰特\n\n【杜兰特的技术特点】\n· 7尺身高+后卫技术的历史级组合\n· 可能是史上最难防守的得分手\n· 无差别单打——任何位置、任何防守人都可以出手\n· 关键时刻的得分能力联盟顶级（绰号'死神'）\n\n【未来展望】\n· 按目前得分效率，杜兰特有望在退役前进入历史前五\n· 保持健康是他的最大挑战\n· 如能再获一枚总冠军戒指，历史地位将进一步提升\n\n【球员评价】\n奥尼尔赛后通过社交媒体祝贺杜兰特：\\\"恭喜KD，你是真正的得分机器，我为你感到骄傲。\\\"");
        n14.setNewsType("球员表现");
        n14.setLeague("NBA");
        n14.setSource("腾讯体育");
        n14.setTags("NBA,杜兰特,得分榜,太阳,历史纪录");
        n14.setPlayerNames("杜兰特,奥尼尔");
        n14.setTeamNames("太阳");
        n14.setPublishTime(now.minusMinutes(360));
        n14.setViewCount(95000);
        news.add(n14);

        SportsNews n15 = new SportsNews();
        n15.setTitle("季后赛对阵出炉：湖人首轮对阵掘金");
        n15.setSummary("西部附加赛结束，湖人以第八身份晋级，将迎战西部第一掘金");
        n15.setContent("【季后赛对阵】\n\nNBA常规赛全部结束后，季后赛对阵正式确定。最受关注的西部首轮对决包括：\n\n【重点对阵分析】\n\n湖人(8) vs 掘金(1)：\n· 詹姆斯+戴维斯 vs 约基奇+穆雷\n· 两队过去两年季后赛均有交手\n· 掘金阵容深度优于湖人\n· 湖人的X因素：角色球员的三分投射\n\n勇士(7) vs 雷霆(2)：\n· 经验vs年轻的经典对决\n· 勇士的季后赛经验是最大优势\n· 雷霆亚历山大首次以主角身份征战季后赛\n· 库里vs亚历山大的控卫对决值得关注\n\n【东部对阵分析】\n\n热火(7) vs 凯尔特人(1)：\n· 老对手再次相遇\n· 热火季后赛模式vs凯尔特人常规赛统治力\n· 巴特勒的季后赛表现是最大变量\n\n【季后赛预测】\n· 西部冠军概率：掘金35%、雷霆25%\n· 东部冠军概率：凯尔特人45%、雄鹿20%\n· 总冠军概率：凯尔特人最高\n\n【赛事看点】\n1. 詹姆斯职业生涯第17次季后赛之旅\n2. 约基奇能否成为继奥拉朱旺后首位蝉联FMVP的球员\n3. 文班亚马首次季后赛亮相");
        n15.setNewsType("比赛数据");
        n15.setLeague("NBA");
        n15.setSource("腾讯体育");
        n15.setTags("NBA,季后赛,对阵,湖人,掘金,勇士");
        n15.setTeamNames("湖人,掘金,勇士,雷霆,凯尔特人,热火");
        n15.setPlayerNames("詹姆斯,戴维斯,约基奇,穆雷,库里,亚历山大");
        n15.setPublishTime(now.minusMinutes(420));
        n15.setViewCount(210000);
        news.add(n15);

        SportsNews n16 = new SportsNews();
        n16.setTitle("安东尼·爱德华兹签名鞋销量创历史新高");
        n16.setSummary("森林狼新星签名球鞋首周销量突破50万双，仅次于乔丹品牌");
        n16.setContent("【商业动态】\n\n明尼苏达森林狼队的安东尼·爱德华兹首款签名篮球鞋发布后，首周销量突破50万双，创造了现役球员签名鞋的历史第二高首周销量纪录（仅次于乔丹品牌）。\n\n【销售数据分析】\n· 首周销量：50万+双\n· 零售价格：120美元起\n· 主要市场：北美（40%）、中国（25%）、欧洲（20%）\n· 线上渠道销量占比：65%\n\n【爱德华兹的商业价值】\n· 年仅23岁，上升空间巨大\n· 2024年奥运会金牌得主\n· 上赛季入选最佳阵容一阵\n· 被媒体称为\"下一个乔丹\"（打法风格相似）\n\n【签名鞋市场格局】\n· 乔丹品牌：遥遥领先\n· 詹姆斯系列：历史最丰富的签名鞋系列\n· 库里系列：安德玛旗舰产品\n· 杜兰特系列：稳定的中端市场\n· 爱德华兹：新生代最具商业潜力的签名鞋\n\n【文化影响】\n爱德华兹的签名鞋不仅是一双篮球鞋，更代表了新一代球员的审美和文化表达。其设计融合了街头文化和篮球传统，受到了年轻消费者的热烈追捧。");
        n16.setNewsType("花边新闻");
        n16.setLeague("NBA");
        n16.setSource("腾讯体育");
        n16.setTags("NBA,爱德华兹,签名鞋,商业,森林狼");
        n16.setPlayerNames("爱德华兹");
        n16.setTeamNames("森林狼");
        n16.setPublishTime(now.minusMinutes(480));
        n16.setViewCount(76000);
        news.add(n16);

        return news;
    }
}
