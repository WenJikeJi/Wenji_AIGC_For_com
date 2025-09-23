package com.wenji.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaService {
    
    private static final Logger log = LoggerFactory.getLogger(CaptchaService.class);
    
    // 验证码存储（生产环境建议使用Redis）
    private final Map<String, CaptchaInfo> captchaStore = new ConcurrentHashMap<>();
    
    // 验证码配置
    private static final int CAPTCHA_WIDTH = 120;
    private static final int CAPTCHA_HEIGHT = 40;
    private static final int CAPTCHA_LENGTH = 4;
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟过期
    
    // 验证码字符集（去除容易混淆的字符）
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    
    private final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    public CaptchaService() {
        // 启动定时清理过期验证码的任务
        scheduler.scheduleAtFixedRate(this::cleanExpiredCaptcha, 1, 1, TimeUnit.MINUTES);
    }
    
    /**
     * 生成验证码
     */
    public Map<String, String> generateCaptcha() {
        try {
            // 生成验证码ID
            String captchaId = UUID.randomUUID().toString();
            
            // 生成验证码文本
            String captchaText = generateCaptchaText();
            
            // 生成验证码图片
            String captchaImage = generateCaptchaImage(captchaText);
            
            // 存储验证码信息
            CaptchaInfo captchaInfo = new CaptchaInfo(captchaText, System.currentTimeMillis());
            captchaStore.put(captchaId, captchaInfo);
            
            log.info("生成验证码成功，ID: {}, 文本: {}", captchaId, captchaText);
            
            Map<String, String> result = new HashMap<>();
            result.put("captchaId", captchaId);
            result.put("captchaImage", captchaImage);
            
            return result;
        } catch (Exception e) {
            log.error("生成验证码失败: {}", e.getMessage(), e);
            throw new RuntimeException("验证码生成失败", e);
        }
    }
    
    /**
     * 验证验证码
     */
    public boolean verifyCaptcha(String captchaId, String inputCode) {
        if (captchaId == null || inputCode == null) {
            return false;
        }
        
        CaptchaInfo captchaInfo = captchaStore.get(captchaId);
        if (captchaInfo == null) {
            log.warn("验证码不存在，ID: {}", captchaId);
            return false;
        }
        
        // 检查是否过期
        if (System.currentTimeMillis() - captchaInfo.getCreateTime() > CAPTCHA_EXPIRE_TIME) {
            captchaStore.remove(captchaId);
            log.warn("验证码已过期，ID: {}", captchaId);
            return false;
        }
        
        // 验证码验证（不区分大小写）
        boolean isValid = captchaInfo.getText().equalsIgnoreCase(inputCode.trim());
        
        if (isValid) {
            // 验证成功后删除验证码（一次性使用）
            captchaStore.remove(captchaId);
            log.info("验证码验证成功，ID: {}", captchaId);
        } else {
            log.warn("验证码验证失败，ID: {}, 期望: {}, 实际: {}", captchaId, captchaInfo.getText(), inputCode);
        }
        
        return isValid;
    }
    
    /**
     * 生成验证码文本
     */
    private String generateCaptchaText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }
    
    /**
     * 生成验证码图片
     */
    private String generateCaptchaImage(String text) throws IOException {
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 填充背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
        
        // 绘制干扰线
        drawNoiseLine(g2d);
        
        // 绘制验证码文本
        drawCaptchaText(g2d, text);
        
        // 添加噪点
        drawNoisePoints(g2d);
        
        g2d.dispose();
        
        // 转换为Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        return "data:image/png;base64," + base64Image;
    }
    
    /**
     * 绘制验证码文本
     */
    private void drawCaptchaText(Graphics2D g2d, String text) {
        Font[] fonts = {
            new Font("Arial", Font.BOLD, 24),
            new Font("Times New Roman", Font.BOLD, 24),
            new Font("Courier New", Font.BOLD, 24)
        };
        
        int x = 10;
        for (int i = 0; i < text.length(); i++) {
            // 随机选择字体
            g2d.setFont(fonts[random.nextInt(fonts.length)]);
            
            // 随机颜色
            g2d.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            
            // 随机旋转角度
            double angle = (random.nextDouble() - 0.5) * 0.4;
            g2d.rotate(angle, x + 12, CAPTCHA_HEIGHT / 2);
            
            // 绘制字符
            g2d.drawString(String.valueOf(text.charAt(i)), x, CAPTCHA_HEIGHT / 2 + 8);
            
            // 恢复旋转
            g2d.rotate(-angle, x + 12, CAPTCHA_HEIGHT / 2);
            
            x += 25;
        }
    }
    
    /**
     * 绘制干扰线
     */
    private void drawNoiseLine(Graphics2D g2d) {
        for (int i = 0; i < 5; i++) {
            g2d.setColor(new Color(random.nextInt(150) + 100, random.nextInt(150) + 100, random.nextInt(150) + 100));
            int x1 = random.nextInt(CAPTCHA_WIDTH);
            int y1 = random.nextInt(CAPTCHA_HEIGHT);
            int x2 = random.nextInt(CAPTCHA_WIDTH);
            int y2 = random.nextInt(CAPTCHA_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
    
    /**
     * 绘制噪点
     */
    private void drawNoisePoints(Graphics2D g2d) {
        for (int i = 0; i < 50; i++) {
            g2d.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x = random.nextInt(CAPTCHA_WIDTH);
            int y = random.nextInt(CAPTCHA_HEIGHT);
            g2d.fillOval(x, y, 1, 1);
        }
    }
    
    /**
     * 清理过期验证码
     */
    private void cleanExpiredCaptcha() {
        long currentTime = System.currentTimeMillis();
        captchaStore.entrySet().removeIf(entry -> {
            boolean expired = currentTime - entry.getValue().getCreateTime() > CAPTCHA_EXPIRE_TIME;
            if (expired) {
                log.debug("清理过期验证码，ID: {}", entry.getKey());
            }
            return expired;
        });
    }
    
    /**
     * 验证码信息类
     */
    private static class CaptchaInfo {
        private final String text;
        private final long createTime;
        
        public CaptchaInfo(String text, long createTime) {
            this.text = text;
            this.createTime = createTime;
        }
        
        public String getText() {
            return text;
        }
        
        public long getCreateTime() {
            return createTime;
        }
    }
}