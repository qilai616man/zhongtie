package com.brainsoon.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;

import org.geometerplus.android.fbreader.FBReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.net.Proxy.Type.HTTP;

public class BookUtils {

	public static Context mFBContext;
	public static boolean isSelected=false;

	private Context mContext;
	private static String productInfo = null;
	private static int FILETYPE = 0; // 0:EPUB 1:PDF
	private static String pdfPath = null;
	private static ArrayList<String> bmpList;

	public BookUtils(Context context) {
		this.mContext = context;
		mFBContext = context;
	}

	// 将Raw文件夹下的文件复制到SD卡中
	public void copyFileFromRawToSDCard(int srcId, String targetPath) {

		FileOutputStream apkOutStream = null;
		try {
			File file = new File(targetPath, "wps.apk");
			if (!file.exists()) {
				file.createNewFile();
			}
			apkOutStream = new FileOutputStream(file);
			InputStream open = mContext.getResources().openRawResource(srcId);
			int temp = -1;
			byte[] data = new byte[1024 * 4];
			while ((temp = open.read(data)) != -1) {
				apkOutStream.write(data, 0, temp);
			}
			apkOutStream.flush();
			open.close();
			apkOutStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//打开文件
	public static void openFile(String filePath,Context context){
		if (filePath.endsWith("mp3")||filePath.endsWith("wma")||filePath.endsWith("wav")){
			openAudio(filePath,context);
		}else if (filePath.endsWith("rm")||filePath.endsWith("rmvb")||filePath.endsWith("mov")||filePath.endsWith("wmv")||filePath.endsWith("3gp")
				||filePath.endsWith("avi")||filePath.endsWith("mtv")||filePath.endsWith("mp4")||filePath.endsWith("mpeg4")){
			openVideo(filePath, context);
		}else if (filePath.endsWith(".doc") || filePath.endsWith(".docx")){
			openWord(filePath, context);
		}else if (filePath.endsWith(".xls") || filePath.endsWith(".xlsx")){
			openExcel(filePath, context);
		}else if (filePath.endsWith(".ppt") || filePath.endsWith(".pptx")){
			openPPT(filePath, context);
		} else if (filePath.endsWith(".pdf")){
			openPDF(filePath, context);
		}else if (filePath.endsWith(".epub")){
			openEPUB(filePath, context);
		}else if (filePath.endsWith(".jpg") || filePath.endsWith(".png")){
			openImage(filePath,context);
		}else {
			File file=new File(filePath);
			if (file.isDirectory()){
				openImage(filePath,context);
			}
		}
	}

	//打开音频文件
	private static void openAudio(String filePath,Context context){
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Uri uri = Uri.fromFile(new File(filePath));
		intent.setDataAndType(uri, "audio/*");
		context.startActivity(intent);
	}

	//打开视频文件
	private static void openVideo(String filePath,Context context){

		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		Uri uri = Uri.fromFile(new File(filePath));

		intent.setDataAndType(uri, "video/*");

		context.startActivity(intent);
	}
	//打开word文件
	private static void openWord( String filePath,Context context )
	{

		Intent intent = new Intent("android.intent.action.VIEW");

//		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(filePath ));

		intent.setDataAndType(uri, "application/msword");

		context.startActivity(intent);
	}

	//打开excel文件
	private static void openExcel(  String filePath,Context context )

	{

		Intent intent = new Intent("android.intent.action.VIEW");

//		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(filePath));

		intent.setDataAndType(uri, "application/vnd.ms-excel");

		context.startActivity(intent);
	}
	//打开PPT文件
	private static void openPPT( String filePath,Context context )

	{

		Intent intent = new Intent("android.intent.action.VIEW");

//		intent.addCategory("android.intent.category.DEFAULT");

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Uri uri = Uri.fromFile(new File(filePath ));

		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

		context.startActivity(intent);

	}

	private static void openImage(String rootPath,Context context){
		bmpList = new ArrayList<String>();
		File rootFile=new File(rootPath);
		getFiles(rootFile);
		if (bmpList.size()>0) {
			Intent bmpIntent = new Intent(context,
					BitmapScannerActivity.class);
			bmpIntent.putStringArrayListExtra("bmpList", bmpList);
			context.startActivity(bmpIntent);
		}else {
			Toast.makeText(context,"该路径没有图片!",Toast.LENGTH_SHORT).show();
		}
	}

	private static void getFiles(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File childFile = files[i];
				getFiles(childFile);
			}
		} else {
			String fileName = null;
			fileName = file.getName();
			if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
				String absolutePath = file.getAbsolutePath();
				bmpList.add(absolutePath);
			}
		}
	}


	/**
	// 通过调用WPS打开文件
	public void openFile(String filePath) {
		Intent intent = new Intent();
		// 存放打开文件相关属性
		Bundle bundle = new Bundle();
		bundle.putString("OpenMode", "Normal"); // ReadOnly:只读模式 Normal:正常模式
		// ReadMode:阅读器模式
		// SaveOnly:保存模式

		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setClassName("cn.wps.moffice_eng",
				"cn.wps.moffice.documentmanager.PreStartActivity2");

		File file = new File(filePath);
		if (file == null || !file.exists()) {

		}
		Uri uri = Uri.fromFile(file);
		intent.setData(uri);
		intent.putExtras(bundle);

		try {
			mContext.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(mContext, "未安装WPS", Toast.LENGTH_SHORT).show();

			boolean exsits = CheckInstalled("cn.wps.moffice_eng");
			if (!exsits) {
				File apkFile = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ File.separator + "wps.apk");
				if (!apkFile.exists()) {
					String apkRootPath = Environment
							.getExternalStorageDirectory().getAbsolutePath();
					copyFileFromRawToSDCard(R.raw.wps, apkRootPath);
				}
			}
			String apkPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + File.separator + "wps.apk";
			Intent installIntent = new Intent(Intent.ACTION_VIEW);
			installIntent.setDataAndType(Uri.fromFile(new File(apkPath)),
					"application/vnd.android.package-archive");
			mContext.startActivity(installIntent);
			e.printStackTrace();
		}
	}


	// 检测是否安装了该应用
	public boolean CheckInstalled(String packageName) {
		PackageManager pm = mContext.getPackageManager();
		List<PackageInfo> infos = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infos) {
			if (packageName.equals(info.packageName)) {
				return true;
			}
		}
		return false;
	}

	// 根据路径安裝应用
	public void installPackage(String apkPath) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(apkPath)),
				"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}

	// 获取PDF阅读进度
	public static int getPDFRecord(String filePath, Context context) {
		int lastSlashPos = filePath.lastIndexOf('/');
		String fileName = new String(lastSlashPos == -1 ? filePath
				: filePath.substring(lastSlashPos + 1));
		File file = new File(filePath);
		if (file.exists()) {
			SharedPreferences prefs = context.getSharedPreferences(
					"pageRecord", Context.MODE_PRIVATE);
			return prefs.getInt("page" + fileName, 0);
		} else {
			return -1;
		}
	}

	// 获取EPUB阅读进度
	public static int getEPUBRecord(String filePath, Context context) {
		int lastSlashPos = filePath.lastIndexOf('/');
		String fileName = new String(lastSlashPos == -1 ? filePath
				: filePath.substring(lastSlashPos + 1));
		File file = new File(filePath);
		if (file.exists()) {
			SharedPreferences prefs = context.getSharedPreferences(
					"pageRecord", Context.MODE_PRIVATE);
			return prefs.getInt("page" + fileName, 0);
		} else {
			return -1;
		}
	}
	 */

	/**
	 * 根据文件路径获取PDF所有批注
	 * 
	 * @param filePath
	 *            文件路径
	 * @param context
	 *            上下文
	 * @return PDF所有批注(JSON格式)

	public static String getPDFAnnotations(String filePath, Context context) {
		PDFMarkDbAdapter adapter = new PDFMarkDbAdapter(context);
		adapter.open(filePath);
		List<BookmarkItem> list = adapter.getall();
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		for (int i = 0; i < list.size(); i++) {
			BookmarkItem item = list.get(i);
			buffer.append("{\"page\":" + item.mIndex + "," + "\"info\":\""
					+ item.markDescribe + "\"}");
			if (i < list.size() - 1) {
				buffer.append(",");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}

	// 根据文件路径和页码删除PDF批注
	public boolean delPDFAnnotation(String filePath, int page) {
		PDFMarkDbAdapter adapter = new PDFMarkDbAdapter(mContext);
		adapter.open(filePath);
		return adapter.delete(page + "");
	}
	 */

	/**
	 * 根据文件路径获取EPUB所有批注
	 * 
	 * @param filePath
	 *            文件路径
	 * @param context
	 *            上下文
	 * @return EPUB所有批注(JSON格式)
	 */

//	public static String getEPUBAnnotations(String filePath, Context context) {
//		BookMarkDBHelper helper = BookMarkDBHelper.getInstance(context);
//		List<BookMark> bookMarks = helper.getBookByPath(filePath);
//
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("[");
//		for (int i = 0; i < bookMarks.size(); i++) {
//			BookMark item = bookMarks.get(i);
//			buffer.append("{\"page\":" + item.getElementIndex() + ","
//					+ "\"info\":\"" + item.getContent() + "\"}");
//			if (i < bookMarks.size() - 1) {
//				buffer.append(",");
//			}
//		}
//		buffer.append("]");
//		return buffer.toString();

//	}

	/**
	 * 解压缩
	 * 
	 * @param fileSrc
	 *            源文件
	 * @param fileDes
	 *            解压目的路径
	 */
	public static void unZipFile(String fileSrc, String fileDes,int book_id,Handler handler) {
		UnzipTask unzipTask=new UnzipTask(fileSrc,fileDes,book_id,handler);
		unzipTask.execute();
	}
//    public static String bkjgg(String username,String password){
//        final StringBuffer msg = new StringBuffer();
//        try {
//            username = URLEncoder.encode(username,"UTF-8");//这里要注意编码，如果参数含有汉字或是空格（尤其是日期中的空格），不编码会发生错误
//            password = URLEncoder.encode(password,"UTF-8");
//        } catch (UnsupportedEncodingException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//        //要访问的HttpServlet
//        String urlStr="http://member.m.crphdm.com/?app=memeber&controller=code&action=setBinding";
//        //要传递的数
//        String params ="&AdminName=="+"kmljjg"+
//                "&password_md5="+"e10adc3949ba59abbe56e057f20f883e"+
//                "&apikey="+"6e04222f412811e79c9a4987cb6493b4"+
//                "&username="+"ccee"+
//                "&userID="+"22"+
//                "&orgID="+"258";
//        urlStr = urlStr+params;
//        try{
//            final URL url =new URL(urlStr);
//            //获得连接
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    // 写子线程中的操作
//                    try {
//                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                        conn.setConnectTimeout(6000);
//                        InputStream in = null;
//                        in = conn.getInputStream();
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
//                        String line = null;
//                        while ((line = reader.readLine()) != null) {
//                                msg.append(line);
//                        }
//                        Log.i("msg:",msg.toString());
//                        reader.close();
//                        in.close();//关闭数据流
//                        conn.disconnect();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
//        }catch(Exception e){
//            e.printStackTrace();
//            return null;
//        }
//        return msg.toString();
//    }
    /**
	 * 打开tdp图书
	 * 
	 * @param srcFile
	 *            解压前文件路径
	 * @param unzipPath
	 *            解压后目录
	 * @param uuids
	 *           用户UUID
	 * @param context
	 *            上下文
	 */
	public static String openTdpBook(String srcFile, String unzipPath,
			String[] uuids, Context context) {

		String keys[]=new String[uuids.length];
		for (int i=0;i<uuids.length;i++){
			AESAlgorithmUtil util=new AESAlgorithmUtil();
			String myKey=util.decrypt(uuids[i]);
			if (!"".equals(myKey)&&myKey!=null){
				keys[i]="v_1.0_123"+myKey;
			}else{
				keys[i]="";
			}
//			System.out.println("key=="+keys[i]);
		}

		File file = new File(unzipPath);
		getRo(file);
//		System.out.println("productInfo=="+productInfo);
		if (!"".equals(productInfo) && productInfo != null) {
			String pro=null;
			for (int i=0;i<keys.length;i++){
				String mKey=keys[i];
				if (!"".equals(mKey)&&mKey!=null){
					AESAlgorithmUtil aes = new AESAlgorithmUtil();
					aes.setPASSWORD_KEY(mKey);
					pro = aes.decrypt(productInfo);
					if (!"".equals(pro)&&pro!=null){
						break;
					}
				}
			}
//			AESAlgorithmUtil aes = new AESAlgorithmUtil();
//			aes.setPASSWORD_KEY(fileKey);
//			String pro = aes.decrypt(productInfo);
			if (!"".equals(pro) && pro != null) {
				int start = pro.indexOf("<cek>");
				int end = pro.indexOf("</cek>");
				if (start > -1 && end > -1) {

					String contentKey = pro.substring(start + 5, end);

//					System.out.println("contentKey=="+contentKey);
//					Log.i("contentKey",contentKey);
					if (!"".equals(contentKey) && contentKey != null) {
						if (FILETYPE == 0) {
							Uri uri = Uri.fromFile(new File(srcFile));
							//gy 0918 暂时注释 测试Fbreader2
							Intent epubIntent = new Intent(context, FBReader.class);
							epubIntent.setData(uri);
							epubIntent.putExtra("aesKey", contentKey);
							context.startActivity(epubIntent);
						} else {
							Intent intent = new Intent();
							intent.setData(Uri.fromFile(new File(srcFile)));
							intent.putExtra("unzipPDFFullName", pdfPath);
							intent.setAction(Intent.ACTION_VIEW);
							intent.putExtra("aesKey", contentKey);
							intent.setClass(context, MuPDFActivity.class);
							context.startActivity(intent);
						}
					}else {
						return  "未能获取文件密钥";
					}
				}
				return  "成功打开文件";
			}else {
				return  "解密Ro节点失败";
			}
		}else{
			return  "未获得Ro节点信息";
		}
	}
    public static String openTdpBookCode(String srcFile, String unzipPath,
                                     String[] uuids, String code , Context context) {

        String keys[]=new String[uuids.length];
        productInfo = code;
        for (int i=0;i<uuids.length;i++){
            AESAlgorithmUtil util=new AESAlgorithmUtil();
            String myKey=util.decrypt(uuids[i]);
            if (!"".equals(myKey)&&myKey!=null){
                keys[i]="v_1.0_123"+myKey;
            }else{
                keys[i]="";
            }
//			System.out.println("key=="+keys[i]);
        }

        File file = new File(unzipPath);
        getRo(file);
//		System.out.println("productInfo=="+productInfo);
        if (!"".equals(productInfo) && productInfo != null) {
            String pro=null;
            for (int i=0;i<keys.length;i++){
                String mKey=keys[i];
                if (!"".equals(mKey)&&mKey!=null){
                    AESAlgorithmUtil aes = new AESAlgorithmUtil();
                    aes.setPASSWORD_KEY(mKey);
                    pro = aes.decrypt(productInfo);
                    if (!"".equals(pro)&&pro!=null){
                        break;
                    }
                }
            }
//			AESAlgorithmUtil aes = new AESAlgorithmUtil();
//			aes.setPASSWORD_KEY(fileKey);
//			String pro = aes.decrypt(productInfo);
            if (!"".equals(pro) && pro != null) {
                int start = pro.indexOf("<cek>");
                int end = pro.indexOf("</cek>");
                if (start > -1 && end > -1) {

                    String contentKey = pro.substring(start + 5, end);

//					System.out.println("contentKey=="+contentKey);
//					Log.i("contentKey",contentKey);
                    if (!"".equals(contentKey) && contentKey != null) {
                        if (FILETYPE == 0) {
                            Uri uri = Uri.fromFile(new File(srcFile));
                            //gy 0918 暂时注释 测试Fbreader2
                            Intent epubIntent = new Intent(context, FBReader.class);
                            epubIntent.setData(uri);
                            epubIntent.putExtra("aesKey", contentKey);
                            context.startActivity(epubIntent);
                        } else {
                            Intent intent = new Intent();
                            intent.setData(Uri.fromFile(new File(srcFile)));
                            intent.putExtra("unzipPDFFullName", pdfPath);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.putExtra("aesKey", contentKey);
                            intent.setClass(context, MuPDFActivity.class);
                            context.startActivity(intent);
                        }
                    }else {
                        return  "未能获取文件密钥";
                    }
                }
                return  "成功打开文件";
            }else {
                return  "解密Ro节点失败";
            }
        }else{
            return  "未获得Ro节点信息";
        }
    }

	public static String getContentKey(String srcFile, String unzipPath, String[] uuids, Context context){

		String keys[]=new String[uuids.length];
		for (int i=0;i<uuids.length;i++){
			AESAlgorithmUtil util=new AESAlgorithmUtil();
			String myKey=util.decrypt(uuids[i]);
			if (!"".equals(myKey)&&myKey!=null){
				keys[i]="v_1.0_123"+myKey;
			}else{
				keys[i]="";
			}
			System.out.println("key=="+keys[i]);
		}
		File file = new File(unzipPath);
		getRo(file);
		System.out.println("productInfo=="+productInfo);
		if (!"".equals(productInfo) && productInfo != null) {
			String pro = null;
			for (int i = 0; i < keys.length; i++) {
				String mKey = keys[i];
				if (!"".equals(mKey) && mKey != null) {
					AESAlgorithmUtil aes = new AESAlgorithmUtil();
					aes.setPASSWORD_KEY(mKey);
					pro = aes.decrypt(productInfo);
					if (!"".equals(pro) && pro != null) {
						break;
					}
				}
			}

			if (!"".equals(pro) && pro != null) {
				int start = pro.indexOf("<cek>");
				int end = pro.indexOf("</cek>");
				if (start > -1 && end > -1) {
					String contentKey = pro.substring(start + 5, end);
					System.out.println("contentKey==" + contentKey);
					return contentKey;
				}
			}
		      return "";
		}
		return "";
	}



	/**
	 * 打开无加密EPUB文件
	 * @param filePath
	 * @param context
	 */
	private static void openEPUB(String filePath,Context context){
		Uri uri = Uri.fromFile(new File(filePath));
		Intent epubIntent = new Intent(context, FBReader.class);
		epubIntent.setData(uri);
		epubIntent.putExtra("aesKey", "");
		context.startActivity(epubIntent);
	}

	/**
	 * 打开无加密PDF文件
	 * @param filePath
	 * @param context
	 */
	private static void openPDF(String filePath,Context context){
		Intent intent = new Intent();
		intent.putExtra("unzipPDFFullName", filePath);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra("aesKey", "");
		intent.setClass(context, MuPDFActivity.class);
		context.startActivity(intent);
	}

	// 获取ro
	private static void getRo(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File childFile = files[i];
				getRo(childFile);
			}
		} else {
			String fileName = null;
			fileName = file.getName();
			if (fileName.equalsIgnoreCase("product.xml")) {
				StringBuffer content = new StringBuffer();
				try {
					InputStream instream = new FileInputStream(file);
					if (instream != null) {
						InputStreamReader inputreader = new InputStreamReader(
								instream);
						BufferedReader buffreader = new BufferedReader(
								inputreader);
						String line;
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							content.append(line);
						}
						String total = content.toString();
						int start = total.indexOf("<ro>");
						int end = total.indexOf("</ro>");
						if (start > -1 && end > -1) {
							productInfo = (String) total.subSequence(start + 4,
									end);
						}
						instream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fileName.equalsIgnoreCase("mimetype")) {
				StringBuffer content = new StringBuffer();
				try {
					InputStream instream = new FileInputStream(file);
					if (instream != null) {
						InputStreamReader inputreader = new InputStreamReader(
								instream);
						BufferedReader buffreader = new BufferedReader(
								inputreader);
						String line;
						// 分行读取
						while ((line = buffreader.readLine()) != null) {
							content.append(line);
						}
						String total = content.toString();
						if (total.contains("epub")) {
							FILETYPE = 0;
						} else {
							FILETYPE = 1;
						}
						instream.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fileName.endsWith(".pdf")) {
				pdfPath = file.getAbsolutePath();
			}
		}
	}

}


class UnzipTask extends AsyncTask<Void,Void,Integer> {

	private String fileSrc,fileDes;
	private int book_id;
	private Handler handler;

	public UnzipTask(String fileSrc,String fileDes,int book_id,Handler handler){
		this.fileSrc=fileSrc;
		this.fileDes=fileDes;
		this.book_id=book_id;
		this.handler=handler;
	}
	@Override
	protected Integer doInBackground(Void... params) {
		try {
			File outFile = new File(fileDes);
			if (!outFile.exists()) {
				outFile.mkdirs();
			}
			File srcFile=new File(fileSrc);
			Log.i("file	Src","<<"+srcFile.exists());
			Log.i("file	Des",outFile.exists()+">>");
			ZipInputStream inZip = new ZipInputStream(new FileInputStream(
					fileSrc));
			ZipEntry zipEntry;
			String szName = "";
			while ((zipEntry = inZip.getNextEntry()) != null) {
				szName = zipEntry.getName();
				System.out.println(szName);
				if (zipEntry.isDirectory()) {
					// get the folder name of the widget
					szName = szName.substring(0, szName.length() - 1);
					File folder = new File(fileDes + File.separator + szName);
					folder.mkdirs();
				} else {
					int lastIndex = szName.lastIndexOf(File.separator);
					if (lastIndex > -1) {
						String filePath = szName.substring(0, lastIndex);
						File path = new File(fileDes + File.separator
								+ filePath);
						if (!path.exists()) {
							path.mkdirs();
						}
					}
					File file = new File(fileDes + File.separator + szName);
					file.createNewFile();
					// get the output stream of the file
					FileOutputStream out = new FileOutputStream(file);
					int len;
					byte[] buffer = new byte[1024];
					// read (len) bytes into buffer
					while ((len = inZip.read(buffer)) != -1) {
						// write (len) byte from buffer at the position 0
						out.write(buffer, 0, len);
						out.flush();
					}
					out.close();
				}
			}
			inZip.close();
			return 0;
		} catch (Exception e) {
			return -1;
		}
	}

	@Override
	protected void onPostExecute(Integer b) {
		super.onPostExecute(b);
		Message msg=handler.obtainMessage();
		msg.what=b;
		msg.arg1=book_id;
		msg.sendToTarget();
	}
}
