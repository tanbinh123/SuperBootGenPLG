package com.ssx.utils;

import org.codehaus.plexus.util.StringUtils;

/**
 * @Author ssx
 * @Date 2021/7/29 13:34
 * @Version 1.0
 */
public class SuperUtils {

    public static String fromArtifactIdGetName(String artifactId){
        String[] split = artifactId.split("[-_]");
        String applicationClassName = "";
        for (String s : split) {
            applicationClassName += StringUtils.capitalise(s);
        }
        return applicationClassName + "Application";
    }
}
