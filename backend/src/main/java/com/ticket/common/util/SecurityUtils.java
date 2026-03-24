package com.ticket.common.util;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 密码工具类
 *
 * @author Ticket System
 */
@Slf4j
public class PasswordUtils {

    private static final String DEFAULT_PREFIX = "{bcrypt}";

    /**
     * 加密密码
     */
    public static String encode(String rawPassword) {
        return DEFAULT_PREFIX + SecureUtil.md5(rawPassword + "ticket_system_salt");
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || !encodedPassword.startsWith(DEFAULT_PREFIX)) {
            return false;
        }
        String encoded = encode(rawPassword);
        return encoded.equals(encodedPassword);
    }

    /**
     * 生成随机密码
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
