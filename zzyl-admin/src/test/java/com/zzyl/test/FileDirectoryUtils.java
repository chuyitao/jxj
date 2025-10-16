package com.zzyl.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹操作工具类
 * 提供读取文件夹中文件名称的功能
 */
public class FileDirectoryUtils {

    /**
     * 读取指定文件夹下的所有文件名称（不包含子文件夹）
     * @param folderPath 文件夹路径
     * @return 文件名称列表，如果路径无效则返回空列表
     */
    public static List<String> getFileNames(String folderPath) {
        return getFileNames(folderPath, false, null);
    }

    /**
     * 读取指定文件夹下的所有文件名称（可选择是否递归）
     * @param folderPath 文件夹路径
     * @param recursive 是否递归获取子文件夹中的文件
     * @return 文件名称列表，如果路径无效则返回空列表
     */
    public static List<String> getFileNames(String folderPath, boolean recursive) {
        return getFileNames(folderPath, recursive, null);
    }

    /**
     * 读取指定文件夹下的所有文件名称（可选择是否递归和文件过滤）
     * @param folderPath 文件夹路径
     * @param recursive 是否递归获取子文件夹中的文件
     * @param extension 文件扩展名过滤（如".txt"，为null则不过滤）
     * @return 文件名称列表，如果路径无效则返回空列表
     */
    public static List<String> getFileNames(String folderPath, boolean recursive, String extension) {
        List<String> fileNames = new ArrayList<>();
        
        // 检查文件夹路径是否有效
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("错误：路径不存在或不是一个文件夹 - " + folderPath);
            return fileNames;
        }
        
        // 获取文件列表
        File[] files = folder.listFiles();
        if (files == null) {
            return fileNames;
        }
        
        for (File file : files) {
            if (file.isFile()) {
                // 检查文件扩展名是否符合要求
                if (extension == null || file.getName().toLowerCase().endsWith(extension.toLowerCase())) {
                    fileNames.add(file.getName());
                }
            } else if (file.isDirectory() && recursive) {
                // 递归获取子文件夹中的文件
                List<String> subFiles = getFileNames(file.getAbsolutePath(), true, extension);
                for (String subFile : subFiles) {
                    fileNames.add(file.getName() + File.separator + subFile);
                }
            }
        }
        
        return fileNames;
    }

    /**
     * 获取文件夹中所有文件的完整路径
     * @param folderPath 文件夹路径
     * @param recursive 是否递归获取子文件夹中的文件
     * @return 文件的完整路径列表
     */
    public static List<String> getFilePaths(String folderPath, boolean recursive) {
        List<String> filePaths = new ArrayList<>();
        
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("错误：路径不存在或不是一个文件夹 - " + folderPath);
            return filePaths;
        }
        
        File[] files = folder.listFiles();
        if (files == null) {
            return filePaths;
        }
        
        for (File file : files) {
            if (file.isFile()) {
                filePaths.add(file.getAbsolutePath());
            } else if (file.isDirectory() && recursive) {
                filePaths.addAll(getFilePaths(file.getAbsolutePath(), true));
            }
        }
        
        return filePaths;
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        // 测试获取当前目录下的文件
        String currentDir = "D:\\昌平校区\\05-课程\\AI学科课程\\AI大模型就业班_深度学习\\上课视频\\04-循环神经网络";
        System.out.println("当前工作目录: " + currentDir);
        
        System.out.println("\n=== 所有文件（非递归）===");
        List<String> files = getFileNames(currentDir, false);
        for (String file : files) {
            String substring = file.substring(0,file.lastIndexOf("."));
            System.out.println(substring.substring(3));
        }

    }
}