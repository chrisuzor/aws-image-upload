package com.ekitigeospatial.awsimageupload.bucket;

public enum BucketName  {
    PROFILE_IMAGE ("ekitigeospatial.com-aws-uploader");

    public String getBucketName() {
        return bucketName;
    }

    private final String bucketName;

    BucketName(String bucketName){
        this.bucketName = bucketName;
    }
}
