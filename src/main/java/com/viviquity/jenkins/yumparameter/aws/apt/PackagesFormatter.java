package com.viviquity.jenkins.yumparameter.aws.apt;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.base.Optional;
import com.viviquity.jenkins.yumparameter.aws.apt.model.Package;

public class PackagesFormatter {
	private VersionComparator versionComparator = new VersionComparator();
	
	public Map<String, String> format(List<Package> packages, Optional<String> packFilter){
		Map<String, String> result = new TreeMap<String, String>(versionComparator);
		if (packFilter.isPresent()) {
			for (Package pack : packages) {
				if (pack.name.equals(packFilter.get()))
					result.put(getFormat(pack), getFormat(pack));
			}
		} else {
			for (Package pack : packages) {
				result.put(getFormat(pack), getFormat(pack));
			}
		}
		return result;
	}

	private String getFormat(Package pack) {
		return String.format("%s=%s", pack.name, pack.version);
	}

	private class VersionComparator implements Comparator<String> {

		@Override
		public int compare(String o1, String o2) {
			return o1.compareTo(o2) * -1;
		}
	}
}
