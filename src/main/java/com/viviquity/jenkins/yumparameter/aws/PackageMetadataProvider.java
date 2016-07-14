package com.viviquity.jenkins.yumparameter.aws;

import com.google.common.base.Optional;

import java.io.InputStream;
import java.util.Map;

public interface PackageMetadataProvider {

	public Map<String,String> extractPackageMetadata(InputStream file, Optional<String> pack);

	public String getMetatdataFilePath(String repoPath);
}
