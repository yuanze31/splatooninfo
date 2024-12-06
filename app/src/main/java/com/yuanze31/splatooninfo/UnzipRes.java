/**
 * 废弃方法，用于解压压缩包
 */
package com.yuanze31.splatooninfo;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipRes {

    public static void unzipFromAssets(Context context, String zipFileName, String destinationPath, boolean overwrite, boolean deleteAfterUnzip) throws IOException {
        File destDir = new File(destinationPath);
        File markerFile = new File(context.getExternalFilesDir(null),
                                   "unzipped");

        // 检查标志文件是否存在，并读取内容
        HashSet<String> unzippedFiles = new HashSet<>();
        if (markerFile.exists()) {
            try (Scanner scanner = new Scanner(new FileReader(markerFile))) {
                while (scanner.hasNextLine()) {
                    unzippedFiles.add(scanner.nextLine()
                                             .trim());
                }
            }
        }

        // 如果标志文件中已记录当前 ZIP 文件，且不覆盖，则跳过解压
        if (unzippedFiles.contains(zipFileName) && !overwrite) {
            System.out.println("资源已解压，无需重复解压：" + zipFileName);
            return;
        }

        // 加载 ZIP 文件
        try (InputStream inputStream = context.getAssets()
                                              .open(zipFileName); ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream))) {

            ZipEntry ze;
            byte[] buffer = new byte[8192];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(destinationPath,
                                     ze.getName());
                if (ze.isDirectory()) {
                    file.mkdirs();
                } else {
                    new File(file.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        while ((count = zis.read(buffer)) != -1) {
                            fos.write(buffer,
                                      0,
                                      count);
                        }
                    }
                }
                zis.closeEntry();
            }
        }

        // 删除 ZIP 文件（可选）
        if (deleteAfterUnzip) {
            System.out.println("ZIP 文件解压完成后不会删除：" + zipFileName);
        }

        // 更新标志文件，记录当前解压的 ZIP 文件路径
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(markerFile,
                                                                       true))) {
            writer.write(zipFileName);
            writer.newLine();
        }

        System.out.println("解压完成，ZIP 文件已记录：" + zipFileName);
    }
}
