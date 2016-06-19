package projb.dissystems.aueb.vassilis.nycheckins;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

import projb.dissystems.aueb.vassilis.nycheckins.DownloadImageTask;

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<DownloadImageTask> bitmapWorkerTaskReference;

    public AsyncDrawable(DownloadImageTask bitmapWorkerTask) {
        bitmapWorkerTaskReference =
                new WeakReference<DownloadImageTask>(bitmapWorkerTask);
    }

    public DownloadImageTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}