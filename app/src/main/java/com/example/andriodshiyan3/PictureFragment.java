package com.example.andriodshiyan3;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {
    public interface PictureFragmentListener{

        void setTitle(String title);
    }
    PictureFragmentListener listener = null;

    //启动拍照Activity所用的请求码
    static final int REQUEST_IMAGE_CAPTURE = 22;
    //启动图像编辑Activity所用的请求码
    static final int REQUEST_IMAGE_CROP = 24;
    //读公共文件夹申请写权限的请求码
    static final int ASK_PERMISSIONS = 20;

    //拍照后存储到的文件的ContentProvider形式位置
    private Uri photoURI;

    //为了能解码拍照文件，需要保存下它的文件路径
    private String photoFilePath;
    //保存图像文件的字段
    private ImageView imageHead;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener= (PictureFragmentListener) context;
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictureFragment newInstance(String param1, String param2) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageHead=view.findViewById(R.id.imageHead);
        listener.setTitle("拍照上传");
        //实现图像的点击
        Button button=view.findViewById(R.id.button);
        button.setOnClickListener(
                v-> {
//                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    //查找满足拍照功能Intent的Activity
//                    if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
//                        File photoFile = null;
//                        try {
//                            photoFile = createImageFile();
//                            if (photoFile != null) {
//                                //把他的路径保存下来
//                                photoFilePath = photoFile.getAbsolutePath();
//                                //将文件路径转成ContentProvider Uri
//                                photoURI = FileProvider.getUriForFile(getContext(),
//                                        "com.qst.tongxuelu.fileprovider",
//                                        photoFile);
//                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
////                                System.out.println("调用了paizhao()");
//                            }
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        }


                    if(ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)//是否有向公共文件夹写的权限
                    != PackageManager.PERMISSION_GRANTED){
                        String[] perssions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(perssions,ASK_PERMISSIONS);//如果没有则取申请权限
                        //该方法申请多个权限 ASK_PERMISSIONS为请求码,需要那种权限通过请求码区分
//                        System.out.println("if被点击");
                    }else {
                        paizhao();

                    }
//                    }
                }

        );
    }

    //响应权限申请的回调方法
    //当权限申请返回时被调用
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED ){
            //判断用户是否同意
            // 权限申请成功，开始拍照
            //grantResults[0]在此数组中权限就一个
            paizhao();
        }else{

        }


    }
    //在要存放文件的路径下新建了一个文件==创建存放的路径

    //产生拍照后图像要保存的文件，放在私有目录下
    //合成要保存到的文件的绝对路径
    private File createImageFile() throws IOException{
        String timeStamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName ="JPEG_"+timeStamp+"_";
        File storageDir =getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //获得图片存放的目录  getExternalFilesDir外部存储  DIRECTORY_PICTURES:对应"pictures",存放图片的路径
        //getExternalFilesDir获取的路径不是外部路径,是私有路径
        File image =File.createTempFile(imageFileName,".jpg",storageDir);
        /*
         fileName: 临时文件的名字, 生成后的文件名字将会是【fileName + 随机数】
         suffix： 文件后缀，例如.txt, .tmp
         parentFile: 临时文件目录，如果不指定，则默认把临时文件存储于系统临时文件目录上
         */
        return  image;
    }
    private File createImageFileForCrop(){
        //图片名称 时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String photoFileName = format.format(date)+".jpg";

        //存储至外部存储上的公共目录，然后加上DCIM，形成全路径返回
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File outputImage = new File(path, photoFileName );
        try {
            //如果文件已存在，删除之
            if (outputImage.exists()) {
                outputImage.delete();
            }
            //创建保存编辑后图像的文件（因为要写公共文件夹，所以需要提前申请权限）
            outputImage.createNewFile();
            return outputImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            //响应拍照Activity的返回 REQUEST_IMAGE_CAPTURE
            //说明拍照成功
            //对图像进行编辑
            Intent intent = new Intent( "com.android.camera.action.CROP"); //剪裁
            intent.putExtra("scale", "true");//支持缩放操作
            intent.putExtra("crop", "true");//支持剪裁
            //设置宽高比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            //设置裁剪图片宽高
            intent.putExtra("outputX", 340);
            intent.putExtra("outputY", 340);

            //指明要编辑的文件的类mime类型
            intent.setDataAndType(photoURI, "image/*");
            //产生写出文件并获取Uri，注意！新版API不允许读和写是同一个文件
            File finalImage = createImageFileForCrop();
            //将绝对路径保存到字符串中，后面使用
            photoFilePath = finalImage.getAbsolutePath();

            //获取Uri，告诉Crop Activity，图像保存到哪里
            // （写出的Uri不能是Content provider的形式，Activity不支持！！！！）
            Uri imageUri = Uri.fromFile(finalImage);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//设置输出文件名

            //告诉剪裁Activity，要申请对Uri的读权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_IMAGE_CROP);
        }else if(requestCode == REQUEST_IMAGE_CROP && resultCode == Activity.RESULT_OK){
            //响应编辑Activity的返回 REQUEST_IMAGE_CROP
            //图像编辑成功，显示出来
            Bitmap bitmap = BitmapFactory.decodeFile(photoFilePath);
            imageHead.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void paizhao(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        System.out.println("未经判断的paizhao()");
//        resolveActivity这个函数的主要作用就是根据intent去收集需要启动的activity的信息
//PackageManager主要是管理应用程序包，通过它就可以获取应用程序信息
        if(takePictureIntent.resolveActivity(getContext().getPackageManager())!=null){
            File photoFile=null;
            try{
                photoFile=createImageFile();
                if(photoFile!=null){
                    photoFilePath=photoFile.getAbsolutePath();
                    photoURI= FileProvider.getUriForFile(getContext(),
                            "com.qst.tongxuelu.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                    startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
                    System.out.println("调用了paizhao()");
                }
            }catch (IOException ex){
                ex.printStackTrace();
            }

        }
    }
}
