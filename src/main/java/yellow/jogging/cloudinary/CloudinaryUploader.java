package yellow.jogging.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import yellow.jogging.beans.Image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CloudinaryUploader {

    private static CloudinaryUploader instance;
    private Cloudinary cloudinary;

    private CloudinaryUploader() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dk9n01gea",
                "api_key", "871778639124492",
                "api_secret", "u6Qk9A-5RZwL9jN1XXpM5jgA_I0"));
    }


    public static CloudinaryUploader getInstance() {
        if (instance == null) {
            instance = new CloudinaryUploader();
        }
        return instance;
    }

    public List<Image> uploadImage(int id, MultipartFile[] files) {
        List<Image> images = new ArrayList<>();
        try {
            Map map;
            for (MultipartFile file : files) {
                map = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                if (map != null) {
                    String url = (String) map.get("url");
                    Image image = new Image();
                    image.setId(id);
                    image.setUrl(url);
                    images.add(image);
                }
            }
        } catch (IOException e) {
            images = null;
        }
        if (images != null && images.size() == 0) {
            images = null;
        }
        return images;
    }


}
