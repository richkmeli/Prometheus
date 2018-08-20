package informatica.unipr.it.Prometheus.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import informatica.unipr.it.Prometheus.R;
import informatica.unipr.it.Prometheus.download.DownloadAndSaveManager;

public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            Bundle extras = intent.getExtras();
            Long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID, -1);    // recupera il reference di quel download
            if (id != -1) {
                DownloadManager.Query q = new DownloadManager.Query();
                q.setFilterById(id);
                DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Cursor c = manager.query(q);

                if (c.moveToFirst()) { // Get download status
                    int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

                    if (status == DownloadManager.STATUS_SUCCESSFUL) { // Download success
                        // Show results
                    //    Toast.makeText(context, context.getResources().getString(R.string.receiver_toast_download_success), Toast.LENGTH_SHORT).show();
                        // Get local file path
                        String path = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

                        DownloadAndSaveManager dASM = new DownloadAndSaveManager(context);
                        dASM.moveFromCacheToInternal(path, id.toString());  // sposta il file scaricato dalla cache alla memoria interna rinominandolo

                    } else if (status == DownloadManager.STATUS_FAILED) { // Failed
                     //   Toast.makeText(context, context.getResources().getString(R.string.receiver_toast_download_failed), Toast.LENGTH_LONG).show();
                    }
                }
                c.close();
            }
        }
    }

}
