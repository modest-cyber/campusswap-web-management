package com.modestcyber.controller;

import com.modestcyber.common.Result;
import com.modestcyber.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "type", defaultValue = "image") String type) {
        String url = fileService.uploadFile(file, type);
        return Result.success(url);
    }
}
