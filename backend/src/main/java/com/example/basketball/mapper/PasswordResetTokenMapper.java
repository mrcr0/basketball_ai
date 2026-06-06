package com.example.basketball.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.basketball.entity.PasswordResetToken;

@Mapper
public interface PasswordResetTokenMapper {

    @Insert("INSERT INTO password_reset_token (user_id, token, expiry_date, used, created_at) " +
            "VALUES (#{userId}, #{token}, #{expiryDate}, #{used}, #{createdAt})")
    int insert(PasswordResetToken token);

    @Select("SELECT * FROM password_reset_token WHERE token = #{token}")
    PasswordResetToken selectByToken(String token);

    @Update("UPDATE password_reset_token SET used = 1 WHERE id = #{id}")
    int markAsUsed(Long id);

    @Delete("DELETE FROM password_reset_token WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);

    @Delete("DELETE FROM password_reset_token WHERE expiry_date < NOW()")
    int deleteExpiredTokens();
}