package com.example.demo.service.cloudinary;


import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.configuration.CloudinaryConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryService {


    @Autowired
    private Cloudinary cloudinary;


    public String uploadImage(String image) throws IOException {
        Map params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );
        Map upload = cloudinary.uploader().upload(image.strip(), params1);
        return upload.get("public_id").toString();
    }

    public String loadImage(String id) throws Exception {
        Map result = cloudinary.api().resource(id, ObjectUtils.emptyMap());
        return result.get("secure_url").toString();
    }
}
