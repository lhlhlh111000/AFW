package com.lease.framework.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 * Created by WenHui on 2015/7/20.
 */
public class FileUtils {
    static String SDPATH =Environment.getExternalStorageDirectory()==null?"":
                    Environment.getExternalStorageDirectory() + "/";
    private static String TAG = "FileUtil";

    public static String readFileContent(File file) {
        try {
            int len = 1024;
            byte[] buffer = new byte[len];
            try {
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int nrb = fis.read(buffer, 0, len); // read up to len bytes
                while (nrb != -1) {
                    baos.write(buffer, 0, nrb);
                    nrb = fis.read(buffer, 0, len);
                }
                buffer = baos.toByteArray();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = new String(buffer);
            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";

    }

    /**
     * 下载文件的监听接口
     */
    public interface ILoadFileListener {
        /**
         * 开始下载
         */
        public void onLoadStart();

        /**
         * 完成下载
         *
         * @param url  下载的URL
         * @param file 下载完成的File
         */
        public void OnLoadFinish(String url, File file);

        /**
         * 进度 0-100
         *
         * @param process
         */
        public void onProgress(int process);

        /**
         * 是否取消下载
         *
         * @return
         */
        public boolean onCancle();
    }

    public static String getSDPATH() {
        return SDPATH;
    }


    /**
     * 获取文件大小 单位KB
     *
     * @param filePath
     * @return
     */
    public static String getFileSize(String filePath) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        long length = 0;
        try {
            File file = new File(filePath);
            length = file.length();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decimalFormat.format(length / 1024.0) + "KB");
    }

    /**
     * 获取文件字节长度
     *
     * @param filePath
     * @return
     */
    public static long getFileSizeValue(String filePath) {
        try {
            File file = new File(filePath);
            return file.length();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取缓存已经存在的时间
     * @param context
     * @param strSaveFileName
     * @return
     */
    public static int getFileStoreDays(Context context, String strSaveFileName) {
        int days = -1;
        try {
            File file = context.getFileStreamPath(strSaveFileName);
            if (file != null && file.exists()) {
                long end = file.lastModified();
                long start = Calendar.getInstance().getTimeInMillis();
                days = (int) Math.abs(end - start) / 86400000;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return days;
    }

    /**
     * 是否存在SD卡
     *
     * @return
     */
    public static boolean isExistSdCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("NewApi")
    public static String getRootPath(Context context) {

        int _sdk = Integer.valueOf(android.os.Build.VERSION.SDK);

        if (_sdk >= 8) { // android2.2 以上

            File _fileDir = context.getExternalFilesDir(null);

            if (_fileDir == null) {
                return null;
            } else
                return _fileDir.getAbsolutePath();

        } else {

            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                Toast.makeText(context, "储存器不可用", Toast.LENGTH_SHORT).show();
                return null;
            }
            return Environment.getExternalStorageDirectory().getPath()
                    + File.separator + "meiyou";
        }

    }

    /**
     * 创建目录
     *
     * @param dirName 如/sdcard/xiangyu/
     * @return
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 创建文件
     *
     * @param fileName 如/sdcard/xiangyu/temp.apk
     * @return
     */
    public static File creatSDFile(String fileName) {
        try {
            File file = new File(fileName);
            file.createNewFile();
            return file;
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
        return null;

    }

    /**
     * 文件是否存在
     *
     * @param fileName
     * @return
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 删除文件
     *
     * @param path
     * @return
     */
    public static boolean delFile(String path) {
        File f = new File(path);
        return f.delete();
    }

    /**
     * 使用http下载文件
     *
     * @param activity 上下文
     * @param strUrl   下载的URL
     * @param path     路径 如/sdcard/data/
     * @param filename 文件名test.jpg
     * @param listener
     * @return
     */
    public static boolean downLoadFile(Context activity, String strUrl,
                                       String path, String filename, boolean showToast,
                                       final ILoadFileListener listener) {
        InputStream input = null;
        OutputStream output = null;
        LogUtils.d(TAG, "下载文件URL为：" + strUrl);
        LogUtils.d(TAG, "本地路径为：" + path);
        LogUtils.d(TAG, "保存文件名为：" + filename);
        try {
            if (!isExistSdCard()) {
                if (showToast)
                    ToastUtils.showToast(activity, "请先插入SD卡再进行升级");
                return false;
            }
            // 若存在该文件，返回false
            if (isFileExist(path + filename)) {
                // ToastUtils.showToast(activity, "该文件已存在了哦");
                LogUtils.d(TAG, "该文件已存在，进行删除");
                delFile(path + filename);
                // return false;
            }

            // 创建目录和文件
            File fileDir = creatSDDir(path);
            if (fileDir == null) {
                if (showToast)
                    ToastUtils.showToast(activity, "创建目录失败了哦");
                LogUtils.d(TAG, "创建目录失败" + path);
                return false;
            }
            LogUtils.d(TAG, "创建目录成功" + path);
            // File file = new File(path + filename);

            File file = creatSDFile(path + filename);
            if (file == null) {
                if (showToast)
                    ToastUtils.showToast(activity, "创建文件失败了哦");
                LogUtils.d(TAG, "创建文件失败" + path + filename);
                return false;
            }
            LogUtils.d(TAG, "创建文件成功" + path + filename);

            // 获取输入流
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            input = conn.getInputStream();
            // 获取文件大小
            long longFilesize = Long.parseLong(conn
                    .getHeaderField("Content-Length"));
            // 获取输出流
            output = new FileOutputStream(file);

            // 缓存4k
            byte buffer[] = new byte[4096];
            int process = 0;
            int length = 0;
            int nrOfBytes = 0;
            // 开始下载
            if (null != listener)
                listener.onLoadStart();
            while ((nrOfBytes = input.read(buffer)) != -1) {
                output.write(buffer, 0, nrOfBytes);
                output.flush();
                if (longFilesize > 0) {
                    length += nrOfBytes;
                    process = (int) (length * 100 / longFilesize);
                    if (null != listener) {

                        // 下载进度
                        listener.onProgress(process);
                        // 是否取消
                        if (listener.onCancle()) {
                            output.close();
                            input.close();
                        }
                    }
                }
            }
            output.flush();
            // 下载完毕
            if (null != listener) {
                listener.onProgress(100);
                listener.OnLoadFinish(strUrl, file);
            }
            // 关闭流
            if (output != null)
                output.close();
            if (input != null)
                input.close();
            return true;
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
            if (showToast)
                ToastUtils.showToast(activity, "下载文件出异常了哦");
            e.printStackTrace();
            try {
                // 关闭流
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                LogUtils.e(e.getLocalizedMessage());
            }

        }
        return false;
    }


    /**

     * 写字符串到文件
     *
     * @param strPathName 文件
     * @param content  内容
     * @param enc      编码方式
     * @return
     */

    public static boolean writeStringToFile(String strPathName, String content,
                                            String enc) {
        File file = new File(strPathName);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            OutputStreamWriter os = null;
            if (enc == null || enc.length() == 0) {
                os = new OutputStreamWriter(new FileOutputStream(file));
            } else {
                os = new OutputStreamWriter(new FileOutputStream(file), enc);
            }
            os.write(content);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param path     要 放置 文件的 路径
     * @param fileName 文件名
     * @param input    文件的InputStream
     * @return 文件对象
     * @throws IOException           文件操作失败
     * @throws FileNotFoundException 文件未发现
     */
    public static File write2SDFromInput(String path, String fileName,
                                  InputStream input) throws IOException, FileNotFoundException,
            Exception {

        File file = null;
        OutputStream output = null;
        creatSDDir(path);
        file = creatSDFile(path + fileName);
        output = new FileOutputStream(file);
        byte buffer[] = new byte[4096];

        int nrOfBytes = 0;
        while ((nrOfBytes = input.read(buffer)) != -1) {
            output.write(buffer, 0, nrOfBytes);
            output.flush();
        }
        output.flush();
        output.close();
        return file;
    }

    /**
     * 获取文件字节流
     *
     * @param fileName 文件路径
     * @return
     * @throws FileNotFoundException
     */
    public byte[] getFileByte(String fileName) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(fileName);
        return getFileByte(fileInputStream);
    }

    /**
     * 获取文件字节流
     *
     * @param url 路径
     * @return
     * @throws FileNotFoundException
     */
    public byte[] getFileByte(URL url) throws IOException {
        if (url != null) {
            return getFileByte(url.openStream());
        } else {
            return null;
        }
    }

    private byte[] getFileByte(InputStream in) {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream(in.available());
            copy(in, out);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (out != null)
            return out.toByteArray();
        return null;

    }

    /**
     * 复制流
     *
     * @param in 输入文件流
     * @param
     * @throws IOException
     */
    private void copy(InputStream in, OutputStream out) throws IOException {
        try {
            byte[] buffer = new byte[4096];
            int length = -1;
            int nrOfBytes = -1;
            // int process = 0;
            // int fileSize = in.available();
            // 读文件到内存 占 30%
            while ((nrOfBytes = in.read(buffer)) != -1) {
                length += nrOfBytes;
                // process = (int)(length*100*0.05/fileSize);
                out.write(buffer, 0, nrOfBytes);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static long forTransfer(File f1, File f2) throws Exception {
        long time = new Date().getTime();
        int length = 2097152;
        FileInputStream in = new FileInputStream(f1);
        FileOutputStream out = new FileOutputStream(f2);
        FileChannel inC = in.getChannel();
        FileChannel outC = out.getChannel();
        int i = 0;
        while (true) {
            if (inC.position() == inC.size()) {
                inC.close();
                outC.close();
                return new Date().getTime() - time;
            }
            if ((inC.size() - inC.position()) < 20971520)
                length = (int) (inC.size() - inC.position());
            else
                length = 20971520;
            inC.transferTo(inC.position(), length, outC);
            inC.position(inC.position() + length);
            i++;
        }
    }

    public static Bitmap setBitmapAttr(Bitmap bitmap, int tarWidth,
                                       int tarHeigth, int dis) {
        Bitmap scaleBmp = null;
        try {
            bitmap.setDensity(dis);
            int bmpW = bitmap.getWidth();
            int bmpH = bitmap.getHeight();
            // 根据屏幕宽度设置图片大小
            tarHeigth = bmpH * tarWidth / bmpW;
            float scaleH = (float) tarHeigth / bmpH;
            float scaleW = (float) tarWidth / bmpW;
            scaleH = scaleH > 1 ? 1 : scaleH;
            scaleW = scaleW > 1 ? 1 : scaleW;
            int destW = (int) (bmpW * scaleW);
            int destH = (int) (bmpH * scaleH);
            scaleBmp = Bitmap.createScaledBitmap(bitmap, destW, destH, true);
        } catch (Exception e) {
            e.printStackTrace();
            if (scaleBmp != null && !scaleBmp.isRecycled()) {
                scaleBmp.recycle();
            }
        }
        return scaleBmp;
    }

    /**
     * 废弃
     * 使用 @see  com.meiyou.framework.biz.util.FastPersistenceDAO#saveObject(
     *              android.content.Context, java.io.Serializable, java.lang.String)
     * @param
     */
    @Deprecated
    public static void saveObjectToLocal(Context context, Object obj,
                                         String strSaveFileName) {
        try {
            if (obj == null) {
                return;
            }
            context.deleteFile(strSaveFileName);
            FileOutputStream os = context.openFileOutput(strSaveFileName, 0);
            ObjectOutputStream out = new ObjectOutputStream(os);
            out.writeObject(obj);
            os.close();
            out.close();
        } catch (Exception e) {
            LogUtils.e(e.getLocalizedMessage());
        }
    }

    public static void delLocalFile(Context context, String strSaveFileName) {
        context.deleteFile(strSaveFileName);
    }

    /**
     * 废弃
     *使用 @see com.meiyou.framework.biz.util.FastPersistenceDAO#getObject(
     *                  android.content.Context, java.lang.String, java.lang.Class)
     * @return
     */
    @Deprecated
    public static Object getObjectFromLocal(Context context,
                                            String strSaveFileName) {
        FileInputStream is = null;
        ObjectInputStream in = null;
        try {
            Object object = new Object();
            File file = context.getFileStreamPath(strSaveFileName);

            if (file != null && file.exists()) {
                LogUtils.d(TAG, "文件路径为：" + file.getAbsolutePath() + " 文件大小为："
                        + file.length());

                is = context.openFileInput(strSaveFileName);
                in = new ObjectInputStream(is);
                Object obj = in.readObject();
                if (obj == null) {
                    // is.close();
                    // in.close();
                    return object;
                } else {
                    object = obj;
                }

                return object;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    /**
     * 将文件缓存在本地
     */
    public static File saveImageToLocal(Context context, String strDir,
                                        String strFileName, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        LogUtils.d("保存本地缓存文件路径：" + strDir + "/" + strFileName);
        File f = new File(strDir, strFileName);
        if (!f.exists()) {
            File vDirPath = f.getParentFile();
            vDirPath.mkdirs();
        }
        try {
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return f;
    }

    /**
     * 删除文件夹
     *
     * @param folderPath 文件夹路径及名称 如c:/fqf
     * @param
     * @return boolean
     */
    public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File myFilePath = new File(filePath);
            myFilePath.delete(); // 删除空文件夹

        } catch (Exception e) {
            LogUtils.d("删除文件夹操作出错");
            e.printStackTrace();

        }
    }

    /**
     * 删除文件夹里面的所有文件
     *
     * @param path String 文件夹路径 如 c:/fqf
     */
    public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
            }
        }
    }

    public static Object cloneObject(Object obj) throws Exception {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(obj);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(
                byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);

        return in.readObject();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;

    }


    /**
     * 解压Gzip文件
     * @param file
     * @param outFile
     * @return
     */
    public static File decodeFileByGzip(File file,File outFile){
        try {
            if (file == null || !file.exists() || outFile == null || !outFile.exists())
                return null;
            byte[] buffer = new byte[1024];
            GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(file));
            FileOutputStream out = new FileOutputStream(outFile);
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            gzis.close();
            out.close();
            return outFile;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;

    }


    /**
     * Gzip解压数据
     * @param compress
     * @return
     * @throws Exception
     */
    public static byte[] decompressGzip(byte[] compress) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(compress);
        InflaterInputStream iis = new InflaterInputStream(bais);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int c = 0;
        byte[] buf = new byte[1024];
        while (true) {
            c = iis.read(buf);
            if (c == -1)
                break;
            baos.write(buf, 0, c);
        }
        baos.flush();
        return baos.toByteArray();
    }
    public static void copyFile(File src, File dest) throws IOException {
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try {
            if(!dest.exists()) {
                dest.createNewFile();
            }

            inChannel = (new FileInputStream(src)).getChannel();
            outChannel = (new FileOutputStream(dest)).getChannel();
            inChannel.transferTo(0L, inChannel.size(), outChannel);
        } finally {
            if(inChannel != null) {
                inChannel.close();
            }

            if(outChannel != null) {
                outChannel.close();
            }

        }

    }
}
