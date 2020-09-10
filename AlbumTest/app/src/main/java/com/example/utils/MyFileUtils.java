package com.example.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyFileUtils {

    //按时间对文件名进行排序
    public static List<File> listFileSortByModifyTime(String path) {
        List<File> list = getFiles(path);
        if (list != null && list.size() > 0) {
            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() > newFile.lastModified()) {
                        return -1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return 1;
                    }
                }
            });
        }
        return list;
    }
    //获取文件夹下的文件
    public static List<File> getFiles (String realPath) {
        List<File> files = new ArrayList<File>();
        File realFile = new File(realPath);
        if (realFile.isDirectory()) {
            File[] subFiles = realFile.listFiles();
            for (File file : subFiles) {
                if (file.isDirectory()) {

                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}
