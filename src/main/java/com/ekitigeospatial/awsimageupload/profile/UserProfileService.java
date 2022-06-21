package com.ekitigeospatial.awsimageupload.profile;

import com.ekitigeospatial.awsimageupload.bucket.BucketName;
import com.ekitigeospatial.awsimageupload.filestore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final FileStore fileStore;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository, FileStore fileStore) {
        this.userProfileRepository = userProfileRepository;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles(){
        return userProfileRepository.getUserProfiles();
    }


    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        if(file.isEmpty()){
            throw new IllegalStateException("Cannot upload empty file [ "+ file.getSize() + " ]");
        }

        if (!Arrays.asList(IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType(),
                IMAGE_GIF.getMimeType()).contains(file.getContentType()) ){
            throw new IllegalStateException("File must be image [" + file.getContentType() + "]");
        }

        UserProfile user = userProfileRepository.getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User Profile %s not found", userProfileId)));

        Map<String, String> metadata =new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), user.getUserProfileId());
        String filename = String.format("%s-%s", file.getName(), UUID.randomUUID());
        try {
            fileStore.save(path, filename, Optional.of(metadata), file.getInputStream());
            user.setUserProfileImageLink(filename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile user = userProfileRepository.getUserProfiles()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileId().equals(userProfileId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("User Profile %s not found", userProfileId)));
        String path = String.format("%s/%s",
                BucketName.PROFILE_IMAGE.getBucketName(),
                user.getUserProfileId());
        return user.getUserProfileImageLink()
                .map(key -> fileStore.download(path, key))
                .orElse(new byte[0]);
    }
}
