package com.example.todo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

import java.io.File;
import java.util.List;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder>{
    private List<String> attachments;
    private Context context;

    public AttachmentAdapter(List<String> attachments, Context context) {
        this.attachments = attachments;
        this.context = context;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View attachmentItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.attachment_item, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        attachmentItemView.setLayoutParams(lp);

        return new AttachmentViewHolder(attachmentItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        String attachment = attachments.get(position);
        String fileName = new File(attachment).getName();
        Log.i("AttachmentAdapter_onBindViewHolder", "Attachment item on position: " + position + " filename is: " + fileName);
        holder.attachmentFileNameTextView.setText(fileName);

        holder.itemView.setOnClickListener(v -> openFile(new File(attachment)));

        holder.attachmentDeleteBtn.setOnClickListener(v -> {
            deleteFile(attachment);
            attachments.remove(position);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        });

    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    private void openFile(File file) {
        try {
            Log.i("AttachmentAdapterOpenFile", "Trying to open file " + file.getName() + " isFile?: " + file.isFile());

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            String type = getMimeType(file.getPath());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
            intent.setDataAndType(uri, type);

            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open file.", Toast.LENGTH_SHORT).show();
            Log.e("AttachmentAdapterOpenFile", "Error opening file", e);
        }
    }

    private String getMimeType(String filePath) {
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            String mimeType = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            return mimeType;
        } else {
            return "*/*";
        }
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (deleted) {
                Log.d("AttachmentAdapter", "File deleted: " + filePath);
            } else {
                Log.d("AttachmentAdapter", "Failed to delete file: " + filePath);
            }
        }
    }

    public void refresh() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }


}
