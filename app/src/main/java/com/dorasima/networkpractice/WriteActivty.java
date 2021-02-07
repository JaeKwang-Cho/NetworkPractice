package com.dorasima.networkpractice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

public class WriteActivty extends AppCompatActivity {

    // 저장될 경로
    String dirPath;
    // 이미지 파일에 접근할 수 있는 Uri
    Uri contentUri;
    // 이미지 파일명을 포함한 경로
    String picPath;

    // Activity를 구분하기 위한 값
    final int CAMERA_ACTIVITY = 1;
    final int GALLERY_ACTIVITY = 2;

    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_activty);

        imageView2 = (ImageView)findViewById(R.id.imageView2);

        // 경로를 구한다.
        String tempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        dirPath = tempPath + "/Android/data/"+getPackageName();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.write_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_camera:
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String fileName = "temp_"+System.currentTimeMillis()+".jpg";
                picPath = dirPath+"/"+fileName;

                File file = new File(picPath);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    contentUri = FileProvider.getUriForFile(
                            this, "com.dorasima.networkclientpractice.file_provider",file
                    );
                }else{
                    contentUri = Uri.fromFile(file);
                }
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,contentUri);
                startActivityForResult(camera_intent,CAMERA_ACTIVITY);
                break;
            case R.id.menu_gallery:
                Intent gallery_intent = new Intent(Intent.ACTION_PICK);
                gallery_intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(gallery_intent,GALLERY_ACTIVITY);
                break;
            case R.id.menu_upload:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_ACTIVITY) {
            if(resultCode==RESULT_OK){
                Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                // 사이즈를 줄인 이미지 개체를 가져온다.
                Bitmap bitmap2 = resizeBitmap(1024,bitmap);
                // 회전 값을 구한다.
                float degree= getDegree(picPath);
                // 회전시킨것을 원본에 덮어 씌우기 한다.
                Bitmap bitmap3 = rotateBitmap(bitmap2,picPath,degree);
                imageView2.setImageBitmap(bitmap3);
            }
        }else if(requestCode==GALLERY_ACTIVITY){
            if(resultCode==RESULT_OK){
                // 이미지에 접근할 수 있는 uri 개체를 추출한다.
                ContentResolver resolver = getContentResolver();
                // contentProvider로 부터 이미지의 경로를 추출한다.
                Cursor cursor = resolver.query(
                        data.getData(),null,null,null,null);
                cursor.moveToNext();

                // 이미지 파일의 이름이 들어있는 컬럼이다.
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                // 해당 사진의 경로를 얻는다.
                String sourcePath = cursor.getString(idx);

                Bitmap bitmap = BitmapFactory.decodeFile(sourcePath);
                Bitmap bitmap2 = resizeBitmap(1024,bitmap);
                float degree = getDegree(sourcePath);

                String fileName = "temp_"+System.currentTimeMillis()+".jpg";
                picPath = dirPath+"/"+fileName;

                Bitmap bitmap3 = rotateBitmap(bitmap2,picPath,degree);

                imageView2.setImageBitmap(bitmap3);
            }
        }
    }

    public Bitmap resizeBitmap(int targetWidth, Bitmap source){
        // 비율을 계산한다.
        double ratio = (double)targetWidth/(double)source.getWidth();
        // 새로운 높이를 계산한다.
        int targetHeight = (int)(source.getHeight()*ratio);
        // 주어진 사이즈로 이미지의 크기를 조정한다.
        Bitmap result = Bitmap.createScaledBitmap(source,targetWidth,targetHeight,false);

        // 원본 이미지 개체를 소멸시킨다.
        if(result!=source){
            source.recycle();
        }
        return  result;
    }
    // 이미지의 회전 각도를 구하는 메서드
    public float getDegree(String path){
        try{
            ExifInterface exifInterface = new ExifInterface(path);
            int degree = 0;

            // 회전 각도 값을 가져온다.
            int ori = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,-1);
            switch (ori){
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case  ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
            }
            return (float)degree;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    // 이미지 회전을 적용하여 새롭게 저장한다.
    public Bitmap rotateBitmap(Bitmap bitmap, String path,float degree){
        try {
            // 원본 이미지의 가로 세로 길이를 구한다.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // 새롭게 만들 이미지의 정보를 담을 개체를 만든다.
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);

            // 새로운 비트맵을 만든다.
            Bitmap rotateBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true);

            // 이미지를 파일에 저장한다.
            FileOutputStream fos = new FileOutputStream(path);
            rotateBitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();

            // 원본 이미지 개체를 소멸한다.
            bitmap.recycle();
            // 새롭게 만든 이미지를 반환한다.
            return rotateBitmap;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}