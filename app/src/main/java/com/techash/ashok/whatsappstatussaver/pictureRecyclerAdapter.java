package com.techash.ashok.whatsappstatussaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Ashok on 9/23/2017.
 */
public class pictureRecyclerAdapter extends RecyclerView.Adapter<pictureRecyclerAdapter.MyViewHolder> {
    private static String DIRECTORY_TO_SAVE_MEDIA_NOW = "/storage/emulated/0/WA Status Saver/Status Pictures/";

    private ArrayList<File> filesList;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;TextView download;
        public MyViewHolder(View itemView) {
            super(itemView);
            thumbnail=(ImageView) itemView.findViewById(R.id.pictures_thumbnail);
            download=(TextView) itemView.findViewById(R.id.save_picture);
        }
    }

    public pictureRecyclerAdapter(ArrayList<File> filesList, Activity activity) {
        this.filesList = filesList;
        this.activity = activity;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pictures_item_view, parent, false);
        return new MyViewHolder(inflatedView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final File currentFile = filesList.get(position);
        Glide.with(activity).load(currentFile.getAbsolutePath())
                .skipMemoryCache( false )
                .crossFade().into(holder.thumbnail);
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    copyFile(currentFile, new File( DIRECTORY_TO_SAVE_MEDIA_NOW+currentFile.getName()),activity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Snackbar snackbar;
                snackbar = Snackbar.make(view, "Picture Saved Successfully", Snackbar.LENGTH_LONG);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimaryDark));
                TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(activity.getResources().getColor(R.color.white));
                textView.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.ic_done_black_24dp, 0);
                snackbar.show();

            }
        });
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("image/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(currentFile));
                activity.startActivity(Intent.createChooser(sharingIntent, "Share Image to"));
            }
        });
    }
    @Override
    public int getItemCount() {
        return filesList.size();
    }

    public static void copyFile(File sourceFile, File destFile,Activity activity) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(destFile);
            mediaScanIntent.setData(contentUri);
            activity.sendBroadcast(mediaScanIntent);

        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }


}
