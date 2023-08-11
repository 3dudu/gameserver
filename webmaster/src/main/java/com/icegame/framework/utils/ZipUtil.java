package com.icegame.framework.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;


/**
 * 压缩或解压zip：
 * 由于直接使用java.util.zip工具包下的类，会出现中文乱码问题，所以使用ant.jar中的org.apache.tools.zip下的工具类
 * @author Administrator
 */

public class ZipUtil {
    private static byte[] _byte = new byte[1024] ;
    /**
     * 压缩文件或路径
     * @param zip 压缩的目的地址
     * @param srcFiles 压缩的源文件
     */
    public static void zipFile( String zip , List<File> srcFiles ){
        try {
            if( zip.endsWith(".zip") || zip.endsWith(".ZIP") ){
                ZipOutputStream _zipOut = new ZipOutputStream(new FileOutputStream(new File(zip))) ;
                _zipOut.setEncoding("GBK");
                for( File _f : srcFiles ){
                    handlerFile(zip , _zipOut , _f , "");
                }
                _zipOut.close();
            }else{
                System.out.println("target file[" + zip + "] is not .zip type file");
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
    
    /**
     * 
     * @param zip 压缩的目的地址
     * @param zipOut 
     * @param srcFile  被压缩的文件信息
     * @param path  在zip中的相对路径
     * @throws IOException
     */
    private static void handlerFile(String zip , ZipOutputStream zipOut , File srcFile , String path ) throws IOException{
        //System.out.println(" begin to compression file[" + srcFile.getName() + "]");
        if( !"".equals(path) && ! path.endsWith(File.separator)){
            path += File.separator ;
        }
        if( ! srcFile.getPath().equals(zip) ){
            if( srcFile.isDirectory() ){
                File[] _files = srcFile.listFiles() ;
                if( _files.length == 0 ){
                    zipOut.putNextEntry(new ZipEntry( path + srcFile.getName() + File.separator));
                    zipOut.closeEntry();
                }else{
                    for( File _f : _files ){
                        handlerFile( zip ,zipOut , _f , path + srcFile.getName() );
                    }
                }
            }else{
                InputStream _in = new FileInputStream(srcFile) ;
                zipOut.putNextEntry(new ZipEntry(path + srcFile.getName()));
                int len = 0 ; 
                while( (len = _in.read(_byte)) > 0  ){
                    zipOut.write(_byte, 0, len);
                }
                _in.close();
                zipOut.closeEntry();
            }
        }
    }


    public static List<File> getFiles(String path) {
    //目标集合fileList
        ArrayList<File> fileList = new ArrayList<File>();
        File file = new File(path);
        if(file.isDirectory()){
            File []files = file.listFiles();
            for(File fileIndex:files){
                //如果这个文件是目录，则进行递归搜索
                if(fileIndex.isDirectory()){
                    getFiles(fileIndex.getPath());
                }else {
                //如果文件是普通文件，则将文件句柄放入集合中
                    fileList.add(fileIndex);
                }
            }
        }
        return fileList;
    }


    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        //子目录不为空
        if (!(subDir == null || subDir.trim().equals(""))) {
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }

    public static void writeFile(InputStream in, OutputStream out) throws IOException {
        int length;
        byte[] b = new byte[1024];
        while ((length = in.read(b)) != -1) {
            out.write(b, 0, length);
        }
    }

    public static void unZip(File file, String outputDir) throws IOException {
        try (java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(file, Charset.forName("GBK"))) {
            //创建输出目录
            createDirectory(outputDir, null);
            Enumeration<?> enums = zipFile.entries();
            while (enums.hasMoreElements()) {
                java.util.zip.ZipEntry entry = (java.util.zip.ZipEntry) enums.nextElement();
                if (entry.isDirectory()) {
                    //创建空目录
                    createDirectory(outputDir, entry.getName());
                } else {
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        try (OutputStream out = new FileOutputStream(
                                new File(outputDir + File.separator + entry.getName()))){
                            writeFile(in, out);
                        }
                    }
                }
            }
        }
    }
    
}