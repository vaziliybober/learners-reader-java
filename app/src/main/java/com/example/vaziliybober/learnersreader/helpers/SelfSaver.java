package com.example.vaziliybober.learnersreader.helpers;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SelfSaver implements Serializable {

    public void save(Context context, String fileName) {

        Functions.deleteContextFile(context, fileName);

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            MyLog.warn("SelfSaver.save()", "It is opening for saving. No wonder it doesn't exist");
            e.printStackTrace();
        }
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            if (fos != null) fos.close();

        } catch (IOException e) {
            MyLog.print("Unable to save to file " + fileName);
            e.printStackTrace();
        }

    }

    public static Object load(Context context, String fileName) {
        Object result = null;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            //MyLog.print("File " + fileName + " not found");
            e.printStackTrace();
            return null;
        }
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(fis);
            result = is.readObject();
            is.close();
            if (fis != null) fis.close();
        } catch (IOException e) {
            //MyLog.print("Can't write to file " + fileName);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            MyLog.warn("SelfSaver.load()", "How can Object not be found?");
            e.printStackTrace();
        }

        return result;
    }


}
