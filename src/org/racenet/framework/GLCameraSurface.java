package org.racenet.framework;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.SurfaceHolder;

public class GLCameraSurface extends GLSurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

	Camera camera;
	
	public GLCameraSurface(Context context) {
		
		super(context);
		this.camera = Camera.open();
		Camera.Parameters p = this.camera.getParameters();
        p.setPreviewSize(240, 160);
        this.camera.setParameters(p);

        SurfaceHolder holder = this.getHolder();
        holder.addCallback(this);
		this.camera.setPreviewCallback(this);
		try {
			this.camera.setPreviewDisplay(holder);
		} catch (IOException e) {}
		this.camera.startPreview();

	}

	public void onPreviewFrame(byte[] arg0, Camera arg1) {
	
		Log.d("DEBUG", "camera frame :D");
		
		int width = this.getWidth();
		int height = this.getHeight();
		int screenshotSize = width * height;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());
        GLES10.glReadPixels(0, 0, width, height, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_BYTE, bb);
        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);
        bb = null;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);
        pixelsBuffer = null;

        // color correction
        short sBuffer[] = new short[screenshotSize];
        ShortBuffer sb = ShortBuffer.wrap(sBuffer);
        bitmap.copyPixelsToBuffer(sb);

        for (int i = 0; i < screenshotSize; ++i) {  
        	
            short v = sBuffer[i];
            sBuffer[i] = (short) (((v&0x1f) << 11) | (v&0x7e0) | ((v&0xf800) >> 11));
        }
        
        sb.rewind();
        bitmap.copyPixelsFromBuffer(sb);
	}
}
