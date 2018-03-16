package com.luck.picture.lib.model;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.entity.LocalMediaFolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * author：luck
 * project：LocalMediaLoader
 * package：com.luck.picture.ui
 * email：893855882@qq.com
 * data：16/12/31
 */

public class LocalMediaLoader {
    private int type = PictureConfig.TYPE_IMAGE;
    private FragmentActivity activity;
    private boolean isGif;

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.SIZE,
            "duration"};

    private static final String SELECTION = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    private static final String SELECTION_NOT_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + " AND " + MediaStore.MediaColumns.MIME_TYPE + "!='image/gif'";

    private static final String[] SELECTION_ARGS = new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};

    public LocalMediaLoader(FragmentActivity activity, int type, boolean isGif) {
        this.activity = activity;
        this.type = type;
        this.isGif = isGif;
    }

    public void loadAllMedia(final LocalMediaLoadListener imageLoadListener) {
        activity.getSupportLoaderManager().initLoader(type, null,
                new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        CursorLoader cursorLoader = null;
                        switch (id) {
                            case PictureConfig.TYPE_IMAGE:
                                cursorLoader = new CursorLoader(
                                        activity, QUERY_URI,
                                        PROJECTION, isGif ? SELECTION : SELECTION_NOT_GIF,
                                        SELECTION_ARGS, ORDER_BY);
                                break;
                        }
                        return cursorLoader;
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                        try {
                            List<LocalMediaFolder> imageFolders = new ArrayList<>();
                            LocalMediaFolder allImageFolder = new LocalMediaFolder();
                            List<LocalMedia> latelyImages = new ArrayList<>();
                            if (data != null) {
                                int count = data.getCount();
                                if (count > 0) {
                                    data.moveToFirst();
                                    do {
                                        String path = data.getString
                                                (data.getColumnIndexOrThrow(PROJECTION[1]));
                                        String pictureType = data.getString
                                                (data.getColumnIndexOrThrow(PROJECTION[2]));
                                        int w = data.getInt
                                                (data.getColumnIndexOrThrow(PROJECTION[3]));
                                        int h = data.getInt
                                                (data.getColumnIndexOrThrow(PROJECTION[4]));
                                        LocalMedia image = new LocalMedia
                                                (path, type, pictureType, w, h);
                                        LocalMediaFolder folder = getImageFolder(path, imageFolders);
                                        List<LocalMedia> images = folder.getImages();
                                        images.add(image);
                                        folder.setImageNum(folder.getImageNum() + 1);
                                        latelyImages.add(image);
                                        int imageNum = allImageFolder.getImageNum();
                                        allImageFolder.setImageNum(imageNum + 1);
                                    } while (data.moveToNext());

                                    if (latelyImages.size() > 0) {
                                        sortFolder(imageFolders);
                                        imageFolders.add(0, allImageFolder);
                                        allImageFolder.setFirstImagePath
                                                (latelyImages.get(0).getPath());
                                        String title = activity.getString(R.string.picture_camera_roll);
                                        allImageFolder.setName(title);
                                        allImageFolder.setImages(latelyImages);
                                    }
                                    imageLoadListener.loadComplete(imageFolders);
                                } else {
                                    // 如果没有相册
                                    imageLoadListener.loadComplete(imageFolders);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {
                    }
                });
    }

    private void sortFolder(List<LocalMediaFolder> imageFolders) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, new Comparator<LocalMediaFolder>() {
            @Override
            public int compare(LocalMediaFolder lhs, LocalMediaFolder rhs) {
                if (lhs.getImages() == null || rhs.getImages() == null) {
                    return 0;
                }
                int lsize = lhs.getImageNum();
                int rsize = rhs.getImageNum();
                return lsize == rsize ? 0 : (lsize < rsize ? 1 : -1);
            }
        });
    }

    private LocalMediaFolder getImageFolder(String path, List<LocalMediaFolder> imageFolders) {
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();
        for (LocalMediaFolder folder : imageFolders) {
            // 同一个文件夹下，返回自己，否则创建新文件夹
            if (folder.getName().equals(folderFile.getName())) {
                return folder;
            }
        }
        LocalMediaFolder newFolder = new LocalMediaFolder();
        newFolder.setName(folderFile.getName());
        newFolder.setPath(folderFile.getAbsolutePath());
        newFolder.setFirstImagePath(path);
        imageFolders.add(newFolder);
        return newFolder;
    }

    public interface LocalMediaLoadListener {
        void loadComplete(List<LocalMediaFolder> folders);
    }
}
