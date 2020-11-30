package com.ringdata.ringinterview.survey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by xch on 2017/10/28.
 */
public class SurveyAccess {
    public static int ASSETS_SURVEY_JS_VERSION = -1;
    private File _www;
    private SharedPreferences _pref;
    public static SurveyAccess instance;

    public static void init(Context cxt, File workspace, SharedPreferences pref) {
        instance = new SurveyAccess(cxt, workspace, pref);
    }

    public SurveyAccess(Context cxt, File workspace, SharedPreferences pref) {
        _www = new File(workspace, "www");
        _pref = pref;
        if (ASSETS_SURVEY_JS_VERSION < 0 ||
                getLocalSurveyJSVersion() < ASSETS_SURVEY_JS_VERSION) {
            _initLocalSurveyJS(cxt.getAssets(), pref, workspace);
        }
    }

    public static void unzip(File zipFile, String location) throws IOException {
        try {
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    File unzipFile = new File(location, ze.getName());

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        /*FileOutputStream fout = new FileOutputStream(unzipFile, false);
                        try {
                            for (int c = zin.read(); c != -1; c = zin.read()) {
                                fout.write(c);
                            }
                            zin.closeEntry();
                        } finally {
                            fout.close();
                        }*/
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(unzipFile, false);
                            byte buffer[] = new byte[1024 * 1024];
                            int realLength = zin.read(buffer);
                            while (realLength != -1) {
                                out.write(buffer, 0, realLength);
                                realLength = zin.read(buffer);
                            }
                        }  finally {
                            if(out != null) {
                                out.close();
                            }
                        }

                    }
                }
            } finally {
                zin.close();
            }
        } catch (Exception e) {
            Log.e("DEV", "Unzip exception", e);
            throw e;
        }
    }

    static public void copyAssetsFolder(AssetManager am, String folder, File parent) throws IOException {
        Log.i("DEV", "copy asserts folder " + folder + " to " + parent.getAbsolutePath());
        if (!parent.exists()) {
            throw new IOException("Parent dir " + parent.getAbsolutePath() + " does not exists");
        }
        int i = folder.lastIndexOf(File.separator);
        File dir = new File(parent, i > 0 ? folder.substring(i) : folder);
        if (!dir.exists()) {
            dir.mkdir();
        }
        copyAssetsFiles(am, folder, dir);
    }

    static public void copyAssetsFiles(AssetManager am, String folder, File targetDir) throws IOException {
        copyAssetsFiles(am, folder, targetDir, false);
    }

    static public void copyAssetsFiles(AssetManager am, String folder, File targetDir, boolean replace) throws IOException {
        if (!targetDir.exists()) {
            throw new IOException("Target dir " + targetDir.getAbsolutePath() + " does not exists");
        }
        String[] files = am.list(folder);
        for (String f : files) {
            String src = folder + File.separator + f;
            String[] subfiles = am.list(src);
            if (subfiles != null && subfiles.length > 0) {
                copyAssetsFolder(am, src, targetDir);
            } else {
                File dest = new File(targetDir, f);
                if (replace || !dest.exists()) {
                    copyAssetsFile(am, src, dest);
                }
            }
        }
    }

    static public void copyAssetsFile(AssetManager am, String src, File dest) throws IOException {
        Log.i("DEV", "copy asserts file " + src + " to " + dest.getAbsolutePath());
        InputStream input = am.open(src);
        FileUtils.copyInputStreamToFile(input, dest);
    }

    public int getLocalSurveyJSVersion() {
        return _pref.getInt("pref_survey_js_version", -1);
    }

    public void showSurvey(Context context, Intent intent, File file) {
        try {
            if (file.exists()) {
                FileUtils.forceDeleteOnExit(new File(_www, "survey.js"));
                FileUtils.copyFile(file, new File(_www, "survey.js"));
                intent.putExtra("SURVEY_PANEL_PAGE", "file://" + new File(_www, "survey-panel.html").getAbsolutePath());
                context.startActivity(intent);
            }
            //intent.putExtra("SURVEY_FILE", file.getAbsolutePath());
        } catch (IOException e) {
            Log.d("DEV", "Failed to init show survey " + file);
        }
    }

    public void showSurvey(Activity activity, Intent intent, String url) {
        try {
            FileUtils.copyFile(new File(_www, "empty_survey.js"), new File(_www, "survey.js"));
            intent.putExtra("SURVEY_PANEL_PAGE", "file://" + new File(_www, "survey-panel.html").getAbsolutePath());
            intent.putExtra("REMOTE_SURVEY", url);
            activity.startActivity(intent);
        } catch (IOException e) {
            Log.d("DEV", "Failed to init show survey " + url);
        }
    }

    public void showRemoteSurveyPanel(Activity activity, Intent intent, String url) {
        try {
            intent.putExtra("SURVEY_PANEL_PAGE", url);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.d("DEV", "Failed to init show survey " + url);
        }
    }

    private void _initLocalSurveyJS(AssetManager manager,
                                    SharedPreferences pref,
                                    File workspace) {
        try {
            if (_www.exists()) {
                FileUtils.deleteDirectory(_www);
            }
            _www.mkdir();
            copyAssetsFolder(manager, "www", workspace);
            pref.edit().putInt("pref_survey_js_version", ASSETS_SURVEY_JS_VERSION).commit();
        } catch (IOException e) {
            Log.e("DEV", "Failed to init local survey JS", e);
        }
    }

    public void removeLocalSurveyJS() {
        try {
            _pref.edit().remove("pref_survey_js_version").commit();
            FileUtils.deleteDirectory(_www);
        } catch (IOException e) {
            Log.e("DEV", "Failed to delete www", e);
        }
    }

    public void updateSurveyJS(File zipfile, int version) {
        if (zipfile != null && zipfile.exists()) {
            Log.d("DEV", "unzip " + zipfile.getAbsolutePath());
            File backup = new File(_www.getParent(), "www_back");
            try {
                if (_www.exists()) {
                    _www.renameTo(backup);
                    _www.mkdir();
                }
                unzip(zipfile, _www.getParent());
                _pref.edit().putInt("pref_survey_js_version", version).commit();
                try {
                    if (backup.exists()) {
                        FileUtils.deleteDirectory(backup);
                    }
                    zipfile.delete();
                } catch (IOException e) {
                }
            } catch (Exception e) {
                Log.e("DEV", "Failed to init local survey JS", e);
                try {
                    if (backup.exists()) {
                        if (_www.exists()) {
                            FileUtils.deleteDirectory(_www);
                        }
                        backup.renameTo(_www);
                    }
                } catch (IOException e1) {
                }
            }
        }
    }
}
