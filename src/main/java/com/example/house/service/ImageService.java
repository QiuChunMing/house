package com.example.house.service;

import com.example.house.config.QiniuProperties;
import com.qiniu.util.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    @Autowired
    private Auth auth;

    @Autowired
    private QiniuProperties qiniuProperties;

    public String getUploadToken() {
        return auth.uploadToken(qiniuProperties.getBucket());
    }
}
