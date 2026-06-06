package com.example.basketball.ai;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component
public class DeepSeekClient {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    @Value("${ai.deepseek.api-key}")
    private String apiKey;

    @Value("${ai.deepseek.api-url}")
    private String apiUrl;

    @Value("${ai.deepseek.model}")
    private String model;

    @Value("${ai.deepseek.pro-api-url}")
    private String proApiUrl;

    @Value("${ai.deepseek.pro-model}")
    private String proModel;

    private static final String SYSTEM_PROMPT = "你是一名专业的篮球教练，精通篮球技术训练、战术分析和体能训练。请用专业但易懂的语言回答用户的问题。";

    public String generateTrainingPlan(String position, String skillLevel, String weakPoints,
                                      String trainingGoal, String height, String weight, String experienceYears,
                                      String cycleType, int cycleCount) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为我生成一个").append(cycleType).append("篮球训练计划。\n");
        prompt.append("我的个人信息：\n");
        prompt.append("- 场上位置：").append(position).append("\n");
        prompt.append("- 技术水平：").append(skillLevel).append("\n");
        prompt.append("- 薄弱技术：").append(weakPoints).append("\n");
        prompt.append("- 训练目标：").append(trainingGoal).append("\n");
        prompt.append("- 身高：").append(height).append("cm\n");
        prompt.append("- 体重：").append(weight).append("kg\n");
        prompt.append("- 球龄：").append(experienceYears).append("年\n");
        prompt.append("\n请以Word文档风格输出训练计划，要求内容详尽具体，采用分要点、分层次的结构化呈现方式。\n");
        prompt.append("必须包含以下所有部分：\n\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("📋 训练计划概览\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("- 计划名称\n");
        prompt.append("- 适用对象：位置/水平/球龄\n");
        prompt.append("- 训练周期与频率\n");
        prompt.append("- 专项侧重\n\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("🎯 训练目标\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("- 列出3-5个具体的、可量化的训练目标\n\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("🔥 热身准备（10-15分钟）\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("- 列出具体热身动作及时间\n\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("🏀 主体训练（按天组织）\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("每一天按以下格式输出：\n");
        prompt.append("  【第N天：训练主题】\n");
        prompt.append("  训练项目一：项目名称\n");
        prompt.append("    · 动作要领：xxx\n");
        prompt.append("    · 训练组数：N组\n");
        prompt.append("    · 每组次数：N次\n");
        prompt.append("    · 训练时长：N分钟\n");
        prompt.append("    · 休息时间：N秒/分钟\n");
        prompt.append("    · 技术要点：xxx\n");
        prompt.append("  （每个训练日至少包含3-5个训练项目）\n\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("🧊 拉伸放松（10-15分钟）\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("- 列出具体拉伸动作及时间\n\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("⚠️ 注意事项\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("- 列出5-8条安全与效率注意事项\n\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("📊 进度追踪建议\n");
        prompt.append("━━━━━━━━━━━━━━━━━━━━\n");
        prompt.append("- 列出如何进行自我评估和进度追踪\n\n");
        prompt.append("请直接输出纯文本格式，不要输出JSON，不要使用代码块包裹。\n");

        try {
            return callAPIWithTimeout(prompt.toString());
        } catch (Exception e) {
            log.warn("DeepSeek API调用失败，使用备用训练计划: {}", e.getMessage());
            return generateMockTrainingPlan(position, skillLevel, weakPoints, trainingGoal, height, weight, experienceYears, cycleType, cycleCount);
        }
    }
    
    private String generateMockTrainingPlan(String position, String skillLevel, String weakPoints,
                                          String trainingGoal, String height, String weight, String experienceYears,
                                          String cycleType, int cycleCount) {
        return "━━━━━━━━━━━━━━━━━━━━\n" +
               "📋 训练计划概览\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "计划名称：" + cycleType + "篮球训练计划（" + cycleCount + "周期）\n" +
               "适用对象：" + position + " / " + skillLevel + " / 球龄" + experienceYears + "年\n" +
               "训练周期：" + cycleType + "计划 × " + cycleCount + "个周期\n" +
               "专项侧重：" + weakPoints + "\n\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "🎯 训练目标\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "1. " + trainingGoal + "\n" +
               "2. 全面提升篮球基础技术和专项能力\n" +
               "3. 增强核心力量和下肢爆发力\n" +
               "4. 培养良好的训练习惯和比赛意识\n\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "🔥 热身准备（10-15分钟）\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "· 慢跑热身：绕球场慢跑5分钟，逐步提升心率\n" +
               "· 动态拉伸：高抬腿30秒×2组、开合跳30秒×2组、弓步拉伸每侧30秒\n" +
               "· 关节激活：肩部环绕、髋关节旋转、踝关节活动各1分钟\n" +
               "· 球感激活：原地运球左右手各50次、胸前传球对墙50次\n\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "🏀 主体训练\n" +
               "━━━━━━━━━━━━━━━━━━━━\n\n" +
               "【第1天：" + weakPoints + "专项训练】\n\n" +
               "训练项目一：基础技术打磨\n" +
               "  · 动作要领：从基本姿势开始，保持身体平衡，专注动作规范性\n" +
               "  · 训练组数：5组\n" +
               "  · 每组次数：10-15次\n" +
               "  · 训练时长：25分钟\n" +
               "  · 休息时间：45秒\n" +
               "  · 技术要点：记录每次成功率，逐步提高标准\n\n" +
               "训练项目二：模拟实战应用\n" +
               "  · 动作要领：在移动中完成技术动作，模拟比赛节奏\n" +
               "  · 训练组数：4组\n" +
               "  · 每组次数：8-12次\n" +
               "  · 训练时长：20分钟\n" +
               "  · 休息时间：60秒\n" +
               "  · 技术要点：注意动作连贯性和节奏控制\n\n" +
               "训练项目三：耐力强化\n" +
               "  · 动作要领：连续完成技术动作，保持动作质量不下降\n" +
               "  · 训练组数：3组\n" +
               "  · 每组次数：15-20次\n" +
               "  · 训练时长：15分钟\n" +
               "  · 休息时间：90秒\n" +
               "  · 技术要点：疲劳状态下仍保持标准动作\n\n" +
               "【第2天：综合体能训练】\n\n" +
               "训练项目一：核心力量训练\n" +
               "  · 动作要领：平板支撑、俄罗斯转体、仰卧举腿\n" +
               "  · 训练组数：4组\n" +
               "  · 每组次数：15次/30秒\n" +
               "  · 训练时长：20分钟\n" +
               "  · 休息时间：60秒\n" +
               "  · 技术要点：保持核心收紧，呼吸均匀\n\n" +
               "训练项目二：下肢爆发力训练\n" +
               "  · 动作要领：深蹲跳、弓箭步跳、箱式跳跃\n" +
               "  · 训练组数：4组\n" +
               "  · 每组次数：10-12次\n" +
               "  · 训练时长：20分钟\n" +
               "  · 休息时间：90秒\n" +
               "  · 技术要点：快速起跳，落地缓冲保护膝盖\n\n" +
               "训练项目三：敏捷性训练\n" +
               "  · 动作要领：折返跑、绳梯训练、变向跑\n" +
               "  · 训练组数：3组\n" +
               "  · 每组次数：5个来回\n" +
               "  · 训练时长：15分钟\n" +
               "  · 休息时间：60秒\n" +
               "  · 技术要点：降低重心，快速变向\n\n" +
               "【第3天：技术综合训练】\n\n" +
               "训练项目一：投篮专项\n" +
               "  · 动作要领：从近到远逐步增加距离，专注投篮手型和弧度\n" +
               "  · 训练组数：5组\n" +
               "  · 每组次数：20次（记录命中数）\n" +
               "  · 训练时长：25分钟\n" +
               "  · 休息时间：45秒\n" +
               "  · 技术要点：肘关节内收，手腕柔和，跟随动作完整\n\n" +
               "训练项目二：运球突破\n" +
               "  · 动作要领：变向运球、胯下运球、背后运球组合练习\n" +
               "  · 训练组数：4组\n" +
               "  · 每组次数：左右各10次\n" +
               "  · 训练时长：20分钟\n" +
               "  · 休息时间：45秒\n" +
               "  · 技术要点：抬头观察，降低运球高度，指腹控球\n\n" +
               "训练项目三：实战模拟\n" +
               "  · 动作要领：半场1v1或2v2对抗，应用所学技术\n" +
               "  · 训练组数：3局\n" +
               "  · 每组次数：每局5分钟\n" +
               "  · 训练时长：15分钟\n" +
               "  · 休息时间：2分钟\n" +
               "  · 技术要点：主动使用训练中的技术动作\n\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "🧊 拉伸放松（10-15分钟）\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "· 大腿前侧拉伸：每侧30秒\n" +
               "· 大腿后侧拉伸：每侧30秒\n" +
               "· 小腿拉伸：每侧30秒\n" +
               "· 肩部和手臂拉伸：各30秒\n" +
               "· 脊柱扭转放松：左右各30秒\n" +
               "· 泡沫轴放松：股四头肌、腘绳肌、背部各1分钟\n\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "⚠️ 注意事项\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "1. 每次训练前务必充分热身，训练后认真拉伸\n" +
               "2. 训练强度应根据当天身体状态灵活调整，不要勉强\n" +
               "3. 保证每天充足睡眠（7-8小时）和营养摄入\n" +
               "4. 训练中如感到关节疼痛或不适，立即停止并休息\n" +
               "5. 每周至少安排1天完全休息日，让身体恢复\n" +
               "6. 训练期间保持充足水分摄入（每天3-4升）\n" +
               "7. 技术动作质量优先于数量，宁少勿滥\n" +
               "8. 建议每周记录训练数据，追踪进步情况\n\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "📊 进度追踪建议\n" +
               "━━━━━━━━━━━━━━━━━━━━\n" +
               "· 每日记录：训练完成度、命中率、体能感受\n" +
               "· 每周评估：对比本周与上周数据，检查进步幅度\n" +
               "· 月度总结：回顾4周训练数据，调整下月训练重点\n" +
               "· 视频记录：每周录制训练视频，对比动作规范性\n" +
               "· 体能测试：每月进行一次体能测试（折返跑、垂直弹跳等）\n";
    }

    public String analyzeAction(String actionType, String description) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下篮球动作：\n");
        prompt.append("- 动作类型：").append(actionType).append("\n");
        prompt.append("- 动作描述：").append(description).append("\n");
        prompt.append("\n请从以下方面进行分析并给出改进建议：\n");
        prompt.append("1. 动作规范性评估\n");
        prompt.append("2. 技术要点分析\n");
        prompt.append("3. 常见错误及纠正方法\n");
        prompt.append("4. 训练建议\n");
        prompt.append("\n请用JSON格式输出：\n");
        prompt.append("{\n");
        prompt.append("  \"assessment\": \"动作评估\",\n");
        prompt.append("  \"keyPoints\": \"技术要点\",\n");
        prompt.append("  \"errors\": [\"错误1\", \"错误2\"],\n");
        prompt.append("  \"suggestions\": [\"建议1\", \"建议2\"]\n");
        prompt.append("}\n");

        return callAPI(prompt.toString());
    }

    public String explainTactic(String tacticName) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请详细讲解篮球战术：").append(tacticName).append("\n");
        prompt.append("\n请按照以下结构讲解：\n");
        prompt.append("1. 战术定义和目的\n");
        prompt.append("2. 战术执行要点\n");
        prompt.append("3. 适用场景\n");
        prompt.append("4. 注意事项\n");
        prompt.append("\n请用JSON格式输出：\n");
        prompt.append("{\n");
        prompt.append("  \"definition\": \"定义\",\n");
        prompt.append("  \"keyPoints\": [\"要点1\", \"要点2\"],\n");
        prompt.append("  \"scenarios\": [\"场景1\", \"场景2\"],\n");
        prompt.append("  \"notes\": [\"注意1\", \"注意2\"]\n");
        prompt.append("}\n");

        return callAPI(prompt.toString());
    }

    public String answerQuestion(String question) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请回答关于篮球的问题：").append(question).append("\n");
        prompt.append("\n请提供详细、专业的回答。\n");

        return callAPI(prompt.toString());
    }

    public String chat(String systemPrompt, String userMessage) {
        return callApiInternal(apiUrl, model, systemPrompt, userMessage);
    }

    public String chatPro(String systemPrompt, String userMessage) {
        log.info("Calling DeepSeek Pro API with model: {}", proModel);
        return callApiInternal(proApiUrl, proModel, systemPrompt, userMessage);
    }

    public String testProConnection() {
        log.info("Testing DeepSeek Pro connection...");
        try {
            String response = chatPro("你是一个专业的AI助手。", "请确认连接成功，并介绍一下你自己。");
            log.info("DeepSeek Pro connection test successful!");
            return "DeepSeek Pro API连接成功！\n" + response;
        } catch (Exception e) {
            log.error("DeepSeek Pro connection test failed: {}", e.getMessage());
            throw new RuntimeException("DeepSeek Pro API连接失败: " + e.getMessage(), e);
        }
    }

    private String callApiInternal(String apiUrl, String model, String systemPrompt, String userMessage) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);

        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 4096);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBodyStr = response.body();
            
            if (responseBodyStr == null || responseBodyStr.isEmpty()) {
                log.error("DeepSeek API returned empty response");
                throw new RuntimeException("API返回空响应");
            }
            
            JSONObject responseBody = JSON.parseObject(responseBodyStr);
            
            if (responseBody == null) {
                log.error("Failed to parse DeepSeek API response: {}", responseBodyStr);
                throw new RuntimeException("无法解析API响应");
            }

            if (responseBody.containsKey("choices")) {
                JSONArray choices = responseBody.getJSONArray("choices");
                if (!choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                }
            }

            if (responseBody.containsKey("error")) {
                JSONObject error = responseBody.getJSONObject("error");
                log.error("DeepSeek API error: {}", error.getString("message"));
                throw new RuntimeException("API调用失败: " + error.getString("message"));
            }

            return responseBody.toJSONString();
        } catch (IOException | InterruptedException e) {
            log.error("DeepSeek API call failed", e);
            throw new RuntimeException("API调用失败", e);
        }
    }

    public String analyzeTrainingData(String dataJson) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请分析以下训练数据：\n");
        prompt.append(dataJson).append("\n");
        prompt.append("\n请分析：\n");
        prompt.append("1. 训练表现评估\n");
        prompt.append("2. 技术短板分析\n");
        prompt.append("3. 改进建议\n");
        prompt.append("4. 训练计划优化建议\n");
        prompt.append("\n请用JSON格式输出：\n");
        prompt.append("{\n");
        prompt.append("  \"performance\": \"表现评估\",\n");
        prompt.append("  \"weakPoints\": [\"短板1\", \"短板2\"],\n");
        prompt.append("  \"suggestions\": [\"建议1\", \"建议2\"],\n");
        prompt.append("  \"optimization\": \"优化建议\"\n");
        prompt.append("}\n");

        return callAPI(prompt.toString());
    }

    private String callAPIWithTimeout(String userPrompt) {
        java.util.concurrent.CompletableFuture<String> future = java.util.concurrent.CompletableFuture.supplyAsync(() -> callAPI(userPrompt));
        try {
            return future.get(8, java.util.concurrent.TimeUnit.SECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            log.warn("DeepSeek API call timed out after 8 seconds, falling back");
            future.cancel(true);
            throw new RuntimeException("DeepSeek API timeout after 8s", e);
        } catch (Exception e) {
            throw new RuntimeException("DeepSeek API call failed: " + e.getMessage(), e);
        }
    }

    private String callAPI(String userPrompt) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);

        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", SYSTEM_PROMPT);
        messages.add(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);
        messages.add(userMsg);

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 4096);

        log.info("Calling DeepSeek API with model: {}, prompt length: {}", model, userPrompt.length());

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(5))
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .timeout(java.time.Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String responseBody = response.body();

            log.info("DeepSeek API response status: {}, body length: {}", statusCode, responseBody != null ? responseBody.length() : 0);

            if (statusCode != 200) {
                log.error("DeepSeek API returned non-200 status: {}, body: {}", statusCode, responseBody);
                throw new RuntimeException("DeepSeek API returned status " + statusCode + ": " + 
                    (responseBody != null ? responseBody.substring(0, Math.min(200, responseBody.length())) : "empty body"));
            }

            if (responseBody == null || responseBody.isEmpty()) {
                log.error("DeepSeek API returned empty response");
                throw new RuntimeException("API返回空响应");
            }
            
            JSONObject responseBodyJson = JSON.parseObject(responseBody);
            
            if (responseBodyJson == null) {
                log.error("Failed to parse DeepSeek API response: {}", responseBody);
                throw new RuntimeException("无法解析API响应");
            }

            if (responseBodyJson.containsKey("choices")) {
                JSONArray choices = responseBodyJson.getJSONArray("choices");
                if (!choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                }
            }

            if (responseBodyJson.containsKey("error")) {
                JSONObject error = responseBodyJson.getJSONObject("error");
                log.error("DeepSeek API error: {}", error.getString("message"));
                throw new RuntimeException("API调用失败: " + error.getString("message"));
            }

            log.warn("Unexpected DeepSeek API response format: {}", responseBody);
            return responseBody;
        } catch (IOException | InterruptedException e) {
            log.error("DeepSeek API call failed", e);
            throw new RuntimeException("API调用失败", e);
        } catch (Exception e) {
            log.error("Error processing DeepSeek API response", e);
            throw new RuntimeException("API响应处理失败", e);
        }
    }

}
