package es.usc.citius.servando.calendula.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import es.usc.citius.servando.calendula.persistence.Prescription;
import es.usc.citius.servando.calendula.util.medicine.HomogeneousGroupStore;
import es.usc.citius.servando.calendula.util.medicine.PrescriptionStore;

/**
 * Created by joseangel.pineiro on 3/3/15.
 */
public class PopulatePrescriptionDBService {

    public static final String DB_VERSION_KEY = "AEMPS_DB_VERSION";
    public static final String TAG = "PopulateDBService.class";

    public void updateIfNeeded(Context ctx) {

        final int manifestVersion = getAempDbVersionFromManifest(ctx);
        final int currentVersion = getAempDbVersionFromPreferences(ctx);
        boolean needUpdate = (currentVersion < manifestVersion) || Prescription.empty();


        if (needUpdate) {
            Log.d(TAG, "Updating prescriptions database...");
            HomogeneousGroupStore.updateGroupsFromCSV(ctx, true, manifestVersion);
            PrescriptionStore.updatePrescriptionsFromCSV(ctx, true, manifestVersion);
        } else {
            Log.d(TAG, "Do not need to update prescription database");
        }
    }

    public int getAempDbVersionFromPreferences(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return prefs.getInt(DB_VERSION_KEY, -1);
    }

    public int getAempDbVersionFromManifest(Context ctx) {
        int databaseVersion = 0;
        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            databaseVersion = bundle.getInt(DB_VERSION_KEY);

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to load meta-data: ", e);
        }
        return databaseVersion;
    }
}
