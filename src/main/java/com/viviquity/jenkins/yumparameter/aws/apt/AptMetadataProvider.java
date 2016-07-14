package com.viviquity.jenkins.yumparameter.aws.apt;

import java.io.InputStream;
import java.util.Map;

import com.google.common.base.Optional;
import com.viviquity.jenkins.yumparameter.aws.PackageMetadataProvider;


public class AptMetadataProvider implements PackageMetadataProvider {

	private AptPackagesParser parser = new AptPackagesParser();
	private PackagesFormatter formatter = new PackagesFormatter();
	
	@Override
	public Map<String, String> extractPackageMetadata(InputStream file, Optional<String> pack) {
		return formatter.format(parser.parse(file), pack);
	}

	@Override
	public String getMetatdataFilePath(String repoPath) {
		return repoPath + "/dists/trusty/main/binary-amd64/Packages";
	}

}
