package com.modestcyber.service;

import com.modestcyber.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件服务
 */
@Slf4j
@Service
public class FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.access.url}")
    private String accessUrl;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/jpg"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * 上传文件
     */
    public String uploadFile(MultipartFile file, String fileType) {
        // 校验文件
        validateFile(file, fileType);

        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;

            // 构建保存路径：uploads/日期/文件名
            String dateDir = LocalDate.now().toString();
            String relativePath = fileType + File.separator + dateDir;
            Path dirPath = Paths.get(uploadPath, relativePath);

            log.info("开始上传文件: {}, 目标路径: {}", originalFilename, dirPath.toAbsolutePath());

            // 创建目录
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                log.info("创建目录: {}", dirPath.toAbsolutePath());
            }

            // 保存文件
            Path filePath = dirPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 返回访问URL
            String fileUrl = accessUrl + relativePath.replace(File.separator, "/") + "/" + filename;
            log.info("文件上传成功: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("文件上传失败: ", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件大小不能超过5MB");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if ("image".equals(fileType)) {
            if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
                throw new BusinessException("只支持JPG、PNG、GIF、WEBP格式的图片");
            }
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
