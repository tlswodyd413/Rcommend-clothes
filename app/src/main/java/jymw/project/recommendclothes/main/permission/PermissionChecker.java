package jymw.project.recommendclothes.main.permission;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import jymw.project.recommendclothes.R;

public class PermissionChecker {
    private Context context;
    private Activity activity;

    public PermissionChecker(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    public void execute() {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("권한이 필요합니다.")
                    .setMessage("단말기의 \"위치\" 권한이 필요합니다.\n권한 허용에 동의해주세요.")
                    .setPositiveButton(R.string.dialog_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    })
                    .setNegativeButton(R.string.dialog_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }
}