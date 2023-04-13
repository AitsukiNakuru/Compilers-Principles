package edu.gxu.util;

import edu.gxu.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtil {
    /**
     * 获取resources目录路径
     *
     * @return resources目录路径
     */
    static public String getResourcePath() {
        String path =  Objects.requireNonNull(Main.class.getResource("/")).getFile();
        return path.substring(1);
    }

    /**
     * 检查文件是否存在
     *
     * @param filePath 文件相对resources的路径
     * @return 文件是否存在
     */
    static public boolean isFileExist(String filePath) {
        return new File(getResourcePath() + filePath).exists();
    }

    /**
     * 读取.txt文件
     * @param filePath 文件路径
     * @return 文本文件内容
     * @throws IOException 读取文件异常
     */
    static public String[] readTxtFile(String filePath) throws IOException {
        Path path = Paths.get(getResourcePath() + filePath);
        return Files.readString(path).split("\n");
    }
}
