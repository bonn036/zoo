package com.mmnn.zoo.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mmnn.zoo.R;

import java.io.File;

public class ChooseImageWindow extends AlertDialog implements View.OnClickListener {

    public static final int IMAGE_PICK = 10244;
    public static final int IMAGE_CAMERA = 10245;
    public static final int IMAGE_CROP = 10246;

    private View mContentView;
    private Activity mActivity;
    private Uri mCameraImageUri;
    private int mImageWidth, mImageHeight;
    private OnChooseImageListener mOnChooseImageListener;

    public ChooseImageWindow(Activity activity, int imageWidth, int imageHeight) {
        super(activity);
        setCanceledOnTouchOutside(true);
        setCancelable(true);
        mActivity = activity;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mContentView);
        initValues();
    }

    private void initValues() {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        lp.width = dm.widthPixels;
        lp.gravity = Gravity.BOTTOM;
        window.setAttributes(lp);
    }

    private void initView() {
        mContentView = View.inflate(mActivity, R.layout.layout_camera_gallary, null);
        mContentView.findViewById(R.id.btn_camera).setOnClickListener(this);
        mContentView.findViewById(R.id.btn_gallary).setOnClickListener(this);
        mContentView.findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    public void setOnChooseImageListener(OnChooseImageListener listener) {
        mOnChooseImageListener = listener;
    }

    private void selectFromGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, Media.EXTERNAL_CONTENT_URI);
        mActivity.startActivityForResult(i, IMAGE_PICK);
    }

    private void selectFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//			mCameraImageUri = mActivity.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());
        String path_Img = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sockertemp";
        mCameraImageUri = Uri.fromFile(new File(path_Img));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
        mActivity.startActivityForResult(intent, IMAGE_CAMERA);
    }

    public void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", mImageWidth);
        intent.putExtra("aspectY", mImageHeight);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", mImageWidth);
        intent.putExtra("outputY", mImageHeight);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        mActivity.startActivityForResult(intent, IMAGE_CROP);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ChooseImageWindow.IMAGE_CAMERA:
                    crop(mCameraImageUri);
                    break;
                case ChooseImageWindow.IMAGE_PICK:
                    crop(data.getData());
                    break;
                case ChooseImageWindow.IMAGE_CROP:
                    if (data != null) {
                        if (mOnChooseImageListener != null) {
                            mOnChooseImageListener.onImageResult((Bitmap) data.getParcelableExtra("data"));
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:// 拍照
                selectFromCamera();
                dismiss();
                break;
            case R.id.btn_gallary:// 相册
                selectFromGallery();
                dismiss();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface OnChooseImageListener {
        void onImageResult(Bitmap bitmap);
    }
}
