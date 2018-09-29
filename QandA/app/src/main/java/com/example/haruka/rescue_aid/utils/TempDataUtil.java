package com.example.haruka.rescue_aid.utils;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TempDataUtil {

    public static void store(Context context, MedicalCertification medicalCertification){
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    context.openFileOutput(medicalCertification.FILENAME, Context.MODE_PRIVATE));
            out.writeObject(medicalCertification);
            out.close();
            Log.d("TempDataUtil", "save is done");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static MedicalCertification load(Context context, String filename){
        Object retObj = null;
        try {
            ObjectInputStream in = new ObjectInputStream(
                    context.openFileInput(filename)
            );
            retObj = in.readObject();
            in.close();
            Log.d("TempDataUtil", "load is done");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return (MedicalCertification)retObj;
    }
}
