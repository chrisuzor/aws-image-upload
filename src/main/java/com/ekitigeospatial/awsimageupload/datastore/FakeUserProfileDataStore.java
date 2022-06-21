package com.ekitigeospatial.awsimageupload.datastore;

import com.ekitigeospatial.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("b1d5e47f-b063-423a-88c4-13643d983bc5"), "janetjones", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("6f8733e6-ba00-4adc-a40a-c0a0bf5b3ee3"), "peterobi", null));
    }

    public static List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
