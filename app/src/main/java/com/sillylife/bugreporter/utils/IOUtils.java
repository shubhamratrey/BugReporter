package com.sillylife.bugreporter.utils;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    private IOUtils() {/* No instances */}

    static void writeStringToFile(String data, File file) throws IOException {
        OutputStream output = new FileOutputStream(file);
        InputStream input = new ByteArrayInputStream(data.getBytes());
        try {
            write(input, output);
        } finally {
            closeQuietly(output);
            closeQuietly(input);
        }
    }

    public static String readStringFromFile(File file) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream((int) file.length());
        FileInputStream input = new FileInputStream(file);
        try {
            write(input, output);
            return output.toString("utf-8");
        } finally {
            closeQuietly(output);
            closeQuietly(input);
        }
    }

    public static void deleteRecursively(File fileOrDirectory) throws IOException {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursively(child);
            }
        }
        fileOrDirectory.delete();
    }

    static void write(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        for (int read = 0; read != -1; read = input.read(buffer)) {
            output.write(buffer, 0, read);
        }
    }

    public static void closeQuietly(@Nullable Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException ignored) {
            // Ignore
        }
    }

    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        // not doing try-with-resources because it requires min API 19
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                write(in, out);
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
