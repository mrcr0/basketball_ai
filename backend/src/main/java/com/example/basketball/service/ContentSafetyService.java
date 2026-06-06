package com.example.basketball.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.regex.Pattern;

@Service
public class ContentSafetyService {

    private static final Set<String> BLOCKED_WORDS = Set.of(
            "暴力", "色情", "赌博", "毒品", "诈骗", "犯罪", "自杀", "自残",
            "恐怖", "仇恨", "歧视", "邪教", "反动"
    );

    private static final Pattern PATTERN_EMAIL = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PATTERN_PHONE = Pattern.compile("1[3-9]\\d{9}");
    private static final Pattern PATTERN_ID_CARD = Pattern.compile("\\d{15}|\\d{18}");

    public SafetyCheckResult checkContent(String content) {
        SafetyCheckResult result = new SafetyCheckResult();
        result.setSafe(true);

        if (content == null || content.trim().isEmpty()) {
            result.setSafe(false);
            result.setReason("内容不能为空");
            return result;
        }

        String lowerContent = content.toLowerCase();

        for (String word : BLOCKED_WORDS) {
            if (lowerContent.contains(word)) {
                result.setSafe(false);
                result.setReason("内容包含敏感词: " + word);
                result.setMatchedWord(word);
                return result;
            }
        }

        if (content.length() > 5000) {
            result.setSafe(false);
            result.setReason("内容过长，请控制在5000字以内");
            return result;
        }

        return result;
    }

    public String filterPersonalInfo(String content) {
        if (content == null) return null;

        String filtered = content;
        filtered = PATTERN_EMAIL.matcher(filtered).replaceAll("[邮箱已过滤]");
        filtered = PATTERN_PHONE.matcher(filtered).replaceAll("[电话已过滤]");
        filtered = PATTERN_ID_CARD.matcher(filtered).replaceAll("[身份证号已过滤]");

        return filtered;
    }

    public String detectDomain(String content) {
        if (content == null) return "general";

        String lowerContent = content.toLowerCase();

        if (containsAny(lowerContent, "篮球", "投篮", "运球", "传球", "防守", "战术", "比赛", "球员")) {
            return "basketball";
        }
        if (containsAny(lowerContent, "历史", "朝代", "战争", "皇帝", "王朝", "文明")) {
            return "history";
        }
        if (containsAny(lowerContent, "科学", "物理", "化学", "生物", "实验", "公式", "定理")) {
            return "science";
        }
        if (containsAny(lowerContent, "编程", "代码", "算法", "软件", "计算机", "程序", "开发")) {
            return "technology";
        }
        if (containsAny(lowerContent, "健康", "饮食", "运动", "医学", "疾病", "治疗")) {
            return "health";
        }
        if (containsAny(lowerContent, "教育", "学习", "考试", "学校", "课程", "老师")) {
            return "education";
        }
        if (containsAny(lowerContent, "经济", "金融", "投资", "股票", "市场", "贸易")) {
            return "economy";
        }

        return "general";
    }

    private boolean containsAny(String content, String... keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getDomainLabel(String domain) {
        switch (domain) {
            case "basketball": return "🏀 篮球运动";
            case "history": return "📜 历史人文";
            case "science": return "🔬 科学技术";
            case "technology": return "💻 信息技术";
            case "health": return "🏥 健康医疗";
            case "education": return "📚 教育教学";
            case "economy": return "💰 财经金融";
            default: return "🌐 综合问答";
        }
    }

    public static class SafetyCheckResult {
        private boolean safe;
        private String reason;
        private String matchedWord;

        public boolean isSafe() {
            return safe;
        }

        public void setSafe(boolean safe) {
            this.safe = safe;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getMatchedWord() {
            return matchedWord;
        }

        public void setMatchedWord(String matchedWord) {
            this.matchedWord = matchedWord;
        }
    }
}
