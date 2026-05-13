package com.yuan.mall.controller;

import com.yuan.mall.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 管理端文件上传
 */
@RestController
@RequestMapping("/mall/admin")
public class AdminUploadController {

    private static final Logger log = LoggerFactory.getLogger(AdminUploadController.class);

    private static final List<String> ALLOWED_EXT = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    @Value("${yuan.upload.path:./data/upload}")
    private String uploadPath;

    /**
     * 上传图片，返回可访问的 URL 路径（如 /upload/xxx.jpg）
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.<String>builder().code(400).message("请选择文件").data(null).timestamp(System.currentTimeMillis()).build();
        }
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            return Result.<String>builder().code(400).message("文件名无效").data(null).timestamp(System.currentTimeMillis()).build();
        }
        String ext = getExtension(originalName).toLowerCase();
        if (!ALLOWED_EXT.contains(ext)) {
            return Result.<String>builder().code(400).message("仅支持图片：jpg、png、gif、webp").data(null).timestamp(System.currentTimeMillis()).build();
        }
        try {
            Path dir = Paths.get(uploadPath).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String newName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            Path target = dir.resolve(newName);
            file.transferTo(target.toFile());
            String url = "/upload/" + newName;
            log.info("upload image success: {}", url);
            return Result.success(url);
        } catch (IOException e) {
            log.error("upload image failed", e);
            return Result.<String>builder().code(500).message("上传失败").data(null).timestamp(System.currentTimeMillis()).build();
        }
    }

    private static String getExtension(String filename) {
        int i = filename.lastIndexOf('.');
        return i > 0 ? filename.substring(i + 1) : "";
    }
}
