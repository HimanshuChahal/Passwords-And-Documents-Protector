package com.hcpasswordprotector;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;


public class CreatePDFBytes extends AsyncTask<ParcelFileDescriptor, Void, ArrayList<byte[]>> {

    PdfRenderer.Page pdfRendererPage;

    public Bitmap getBitmapFromPDFRendererPage()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {
            Bitmap bitmap;
            try
            {
                bitmap=Bitmap.createBitmap(pdfRendererPage.getWidth(),pdfRendererPage.getHeight(),Bitmap.Config.ARGB_8888);
                pdfRendererPage.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                return bitmap;
            } catch (Exception e)
            {
                return null;
            }
        }else{
            return null;
        }
    }

    public void applyFirstImageToDocumentImageView(PdfRenderer pdfRenderer)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            try {
                pdfRendererPage = pdfRenderer.openPage(0);
                Bitmap bitmap = getBitmapFromPDFRendererPage();
                AddDocumentActivity.firstImageOfTheDocumentBitmap = bitmap;
                pdfRendererPage.close();
            } catch (Exception e) {

            }
            finally {
                pdfRenderer.close();
            }
        }
    }

    @Override
    protected ArrayList<byte[]> doInBackground(ParcelFileDescriptor... parcelFileDescriptors) {

        try {

            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(parcelFileDescriptors[0].getFileDescriptor()));
            ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
            ByteArrayOutputStream byteArrayOutputStream1=new ByteArrayOutputStream();
            ByteArrayOutputStream byteArrayOutputStream2=new ByteArrayOutputStream();
            ByteArrayOutputStream byteArrayOutputStream3=new ByteArrayOutputStream();
            ByteArrayOutputStream byteArrayOutputStream4=new ByteArrayOutputStream();
            int data=inputStream.read();
            int x=0;
            int max_Value_Of_The_Blob=2096720;
            while (data!=-1)
            {
                if(x<max_Value_Of_The_Blob) {
                    byteArrayOutputStream.write(data);
                } else if(x>=max_Value_Of_The_Blob && x<2*max_Value_Of_The_Blob)
                {
                    byteArrayOutputStream1.write(data);
                } else if(x>=2*max_Value_Of_The_Blob && x<3*max_Value_Of_The_Blob)
                {
                    byteArrayOutputStream2.write(data);
                } else if(x>=3*max_Value_Of_The_Blob && x<4*max_Value_Of_The_Blob)
                {
                    byteArrayOutputStream3.write(data);
                } else if(x>=4*max_Value_Of_The_Blob && x<5*max_Value_Of_The_Blob)
                {
                    byteArrayOutputStream4.write(data);
                }
                data = inputStream.read();
                x++;
            }

            byte[] pdfDocumentByteArray=byteArrayOutputStream.toByteArray();

            byte[] pdfDocumentByteArray1=byteArrayOutputStream1.toByteArray();

            byte[] pdfDocumentByteArray2=byteArrayOutputStream2.toByteArray();

            byte[] pdfDocumentByteArray3=byteArrayOutputStream3.toByteArray();

            byte[] pdfDocumentByteArray4=byteArrayOutputStream4.toByteArray();


            inputStream.close();

            ArrayList<byte[]> bytesArrayList=new ArrayList<>();

            bytesArrayList.add(pdfDocumentByteArray);
            bytesArrayList.add(pdfDocumentByteArray1);
            bytesArrayList.add(pdfDocumentByteArray2);
            bytesArrayList.add(pdfDocumentByteArray3);
            bytesArrayList.add(pdfDocumentByteArray4);

            return bytesArrayList;

        } catch (Exception e)
        {
            return null;
        }
    }
}
