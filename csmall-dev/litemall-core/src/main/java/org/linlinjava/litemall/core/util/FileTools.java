package org.linlinjava.litemall.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class FileTools {

    private static final String[] suffixWhiteList = {"PNG","JPEG","JPG","GIF"};
    private static final String[] mimeTypeWhiteList = {"image/jpeg","image/gif","image/png"};

    /**
     * 文件后缀名校验
     *
     * @param fileName
     *            文件名称
     * @return
     */
    public static boolean suffixCheck(String fileName) {
        if(fileName == null || "".equals(fileName)){
            return false;
        }
        //从最后一个点之后截取字符串
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        //白名单匹配
        boolean anyMatch = Arrays.stream(suffixWhiteList).anyMatch(x -> x.equalsIgnoreCase(suffix));
        return anyMatch;
    }

    public static boolean mp4Check(String fileName) {
        if(fileName == null || "".equals(fileName)){
            return false;
        }
        //从最后一个点之后截取字符串
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        boolean anyMatch = StringUtils.equalsIgnoreCase("mp4",suffix);
        return anyMatch;
    }


    /**
     * 判断文件大小
     *
     * @param len
     *            文件长度
     * @param size
     *            限制大小
     * @param unit
     *            限制单位（B,K,M,G）
     * @return
     */
    public static boolean checkFileSize(Long len, int size, String unit) {
//        long len = file.length();
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }



}
