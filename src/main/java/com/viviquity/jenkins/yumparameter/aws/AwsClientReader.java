package com.viviquity.jenkins.yumparameter.aws;

import java.io.IOException;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.google.common.base.Optional;
import com.viviquity.jenkins.yumparameter.aws.apt.AptMetadataProvider;
import com.viviquity.jenkins.yumparameter.aws.yum.YumMetadataProvider;

/**
 * yum-parameter
 *
 * @author Declan Newman (467689)
 * @since 15/02/15
 */
public class AwsClientReader {
    private final AmazonS3Client awsClient;
    private final String repoPath;
    private final Optional<String> pack;
    private final Optional<String> awsAccessKeyId;
    private final Optional<String> awsSecretAccessKey;
    private final PackageMetadataProvider packageMetadataProvider;

    private AwsClientReader(Builder builder)  {
        this.repoPath = builder.repoPath;
        this.pack = builder.pack;
        this.awsAccessKeyId = builder.awsAccessKeyId;
        this.awsSecretAccessKey = builder.awsSecretAccessKey;
        this.awsClient = buildClient();
        this.packageMetadataProvider = "apt".equals(builder.repoType) ?  new AptMetadataProvider() : new YumMetadataProvider();
    }


    public Map<String, String> getPackageMap(final String bucketName) throws IOException {
        final S3Object s3Object = awsClient.getObject(bucketName, packageMetadataProvider.getMetatdataFilePath(repoPath));
        return packageMetadataProvider.extractPackageMetadata(s3Object.getObjectContent(), pack);
    }

    private AmazonS3Client buildClient() {
        if (awsAccessKeyId.isPresent() && awsSecretAccessKey.isPresent()) {
            return new AmazonS3Client(new BasicAWSCredentials(awsAccessKeyId.get(), awsSecretAccessKey.get()));
        } else {
            return new AmazonS3Client(new InstanceProfileCredentialsProvider());
        }
    }


    public static class Builder {
    	private final String repoType;
        private final String repoPath;
        private final Optional<String> pack;
        private Optional<String> awsAccessKeyId = Optional.absent();
        private Optional<String> awsSecretAccessKey = Optional.absent();

        private Builder(String repoPath, String repoType, Optional<String> pack) {
            this.repoPath = repoPath;
            this.repoType = repoType;
            this.pack = pack;
        }

        public static Builder newInstance(String repoPath, String repoType) {
            Optional<String> pack = Optional.absent();
            return new Builder(repoPath, repoType, pack);
        }

        public static Builder newInstance(String repoPath, String repoType, String pack) {
            Optional<String> absent = Optional.absent();
            Optional<String> packOptional = pack.isEmpty() ? absent : Optional.fromNullable(pack);
            return new Builder(repoPath, repoType, packOptional);
        }

        public Builder withAwsAccessKeys(String awsAccessKeyId, String awsSecretAccessKey) {
            this.awsAccessKeyId = Optional.of(awsAccessKeyId);
            this.awsSecretAccessKey = Optional.of(awsSecretAccessKey);
            return this;
        }

        public AwsClientReader build() throws JAXBException {
            return new AwsClientReader(this);
        }
    }

}
