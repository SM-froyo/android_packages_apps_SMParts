package com.cyanogenmod.cmparts.activities;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import android.content.DialogInterface;
import android.app.AlertDialog;
import android.widget.Toast;
import android.os.Environment;
import java.io.File;
import java.io.FileWriter;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import java.util.ArrayList;
import java.io.IOException;
import android.graphics.Color;
import java.io.FileReader;
import java.io.FileNotFoundException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;
import android.content.Intent;
import java.io.FileInputStream;
import java.io.DataInputStream;

import com.cyanogenmod.cmparts.R;

public class TweaksExtras extends PreferenceActivity {

    /* Reset to Defaults */
    private static final String UI_RESET_TO_DEFAULTS = "reset_ui_tweaks_to_defaults";
    private Preference mResetToDefaults;
    /* XML */
    private static final String XML_FILENAME = "cmparts_ui.xml";
    private static final String UI_EXPORT_TO_XML = "export_to_xml";
    private Preference mExportToXML;
    private static final String UI_IMPORT_FROM_XML = "import_from_xml";
    private Preference mImportFromXML;
    private static final String NAMESPACE = "com.cyanogenmod.cmparts";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.te_title);
        addPreferencesFromResource(R.xml.tweaks_extras);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Reset to Defaults */
        mResetToDefaults = prefSet.findPreference(UI_RESET_TO_DEFAULTS);
        /* XML */
        mExportToXML = prefSet.findPreference(UI_EXPORT_TO_XML);
        mImportFromXML = prefSet.findPreference(UI_IMPORT_FROM_XML);

        Intent mvSdUi = new Intent("com.cyanogenmod.cmparts.RESTORE_CMPARTS_UI");
        mvSdUi.putExtra("temp", "temp");
        sendBroadcast(mvSdUi);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        /* Reset to Defaults */
        if (preference == mResetToDefaults) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getResources().getString(R.string.title_dialog_ui_interface));
            alertDialog.setMessage(getResources().getString(R.string.message_dialog_reset));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    resetUITweaks();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
        else if (preference == mExportToXML) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getResources().getString(R.string.title_dialog_ui_interface));
            alertDialog.setMessage(getResources().getString(R.string.message_dialog_export));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    writeUIValuesToXML();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
        else if (preference == mImportFromXML) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getResources().getString(R.string.title_dialog_ui_interface));
            alertDialog.setMessage(getResources().getString(R.string.message_dialog_import));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    readUIValuesFromXML();
                }
            });
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
        return true;
    }
    private void resetUITweaks() {
        Settings.System.putInt(getContentResolver(), Settings.System.CLOCK_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.DBM_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.DATE_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.SPN_LABEL_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.PLMN_LABEL_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.BATTERY_PERCENTAGE_STATUS_COLOR, -1);
        Settings.System.putInt(getContentResolver(), Settings.System.NEW_NOTIF_TICKER_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.NOTIF_COUNT_COLOR, -1);
        Settings.System.putInt(getContentResolver(), Settings.System.NO_NOTIF_COLOR, -1);
        Settings.System.putInt(getContentResolver(), Settings.System.CLEAR_BUTTON_LABEL_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.ONGOING_NOTIF_COLOR, -1);
        Settings.System.putInt(getContentResolver(), Settings.System.LATEST_NOTIF_COLOR, -1);
        Settings.System.putInt(getContentResolver(), Settings.System.NOTIF_ITEM_TITLE_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.NOTIF_ITEM_TEXT_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.NOTIF_ITEM_TIME_COLOR, -16777216);
        Settings.System.putInt(getContentResolver(), Settings.System.BATTERY_PERCENTAGE_STATUS_ICON, 0);
        Settings.System.putInt(getContentResolver(), Settings.System.SHOW_STATUS_CLOCK, 1);
        Settings.System.putInt(getContentResolver(), Settings.System.SHOW_STATUS_DBM, 0);
        Settings.System.putInt(getContentResolver(), Settings.System.SHOW_PLMN_LS, 1);
        Settings.System.putInt(getContentResolver(), Settings.System.SHOW_SPN_LS, 1);
        Settings.System.putInt(getContentResolver(), Settings.System.SHOW_PLMN_SB, 1);
        Settings.System.putInt(getContentResolver(), Settings.System.SHOW_SPN_SB, 1);

        Toast.makeText(getApplicationContext(), R.string.reset_ui_success, Toast.LENGTH_SHORT).show();
    }
    private void writeUIValuesToXML() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(getApplicationContext(), R.string.xml_sdcard_unmounted, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent mvUiSd = new Intent("com.cyanogenmod.cmparts.SAVE_CMPARTS_UI");

        FileWriter writer = null;
        File outFile = new File(Environment.getDataDirectory() + "/data/" + NAMESPACE + "/tempfile.xml");
        boolean success = false;

        try {
            outFile.createNewFile();
            writer = new FileWriter(outFile);
            XmlSerializer serializer = Xml.newSerializer();

            ArrayList<String> elements = new ArrayList<String>();
            elements.add(Settings.System.BATTERY_PERCENTAGE_STATUS_COLOR);
            elements.add(Settings.System.CLOCK_COLOR);
            elements.add(Settings.System.DBM_COLOR);
            elements.add(Settings.System.DATE_COLOR);
            elements.add(Settings.System.PLMN_LABEL_COLOR);
            elements.add(Settings.System.SPN_LABEL_COLOR);
            elements.add(Settings.System.NEW_NOTIF_TICKER_COLOR);
            elements.add(Settings.System.NOTIF_COUNT_COLOR);
            elements.add(Settings.System.NO_NOTIF_COLOR);
            elements.add(Settings.System.CLEAR_BUTTON_LABEL_COLOR);
            elements.add(Settings.System.ONGOING_NOTIF_COLOR);
            elements.add(Settings.System.LATEST_NOTIF_COLOR);
            elements.add(Settings.System.NOTIF_ITEM_TITLE_COLOR);
            elements.add(Settings.System.NOTIF_ITEM_TEXT_COLOR);
            elements.add(Settings.System.NOTIF_ITEM_TIME_COLOR);

            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "cmparts");

            int color = -1;

            for (String s : elements) {
                try {
                    color = Settings.System.getInt(getContentResolver(), s);
                }
                catch (SettingNotFoundException e) {
                    if (s.equals(Settings.System.BATTERY_PERCENTAGE_STATUS_COLOR) ||
                            s.equals(Settings.System.NOTIF_COUNT_COLOR) ||
                            s.equals(Settings.System.NO_NOTIF_COLOR) ||
                            s.equals(Settings.System.ONGOING_NOTIF_COLOR) ||
                            s.equals(Settings.System.LATEST_NOTIF_COLOR)) {
                        color = -1;
                    }
                    else {
                        color = -16777216;
                    }
                }

                serializer.startTag("", s);
                serializer.text(convertToARGB(color));
                serializer.endTag("", s);
            }

            serializer.endTag("", "cmparts");
            serializer.endDocument();
            serializer.flush();
            success = true;
        }
        catch (Exception e) {
            android.util.Log.d("XMLOUTPUT", e.toString());
            Toast.makeText(getApplicationContext(), R.string.xml_write_error, Toast.LENGTH_SHORT).show();
        }
        finally {
            if (writer != null) {
        		try {
	        	    writer.close();
	        	} catch (IOException e) {
	        	}
	        }
        }

        if (success) {
            try {
              FileInputStream infile = new FileInputStream(outFile);
              DataInputStream in = new DataInputStream(infile);
              byte[] b = new byte[in.available()];
              in.readFully(b);
              in.close();
              String result = new String(b, 0, b.length);
              mvUiSd.putExtra("xmldata", result);
            } catch (Exception e) {
              e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), R.string.xml_run_helper, Toast.LENGTH_SHORT).show();
            sendBroadcast(mvUiSd);
        }
        if (outFile.exists())
            outFile.delete();
    }

    private void readUIValuesFromXML() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Toast.makeText(getApplicationContext(), R.string.xml_sdcard_unmounted, Toast.LENGTH_SHORT).show();
            return;
        }

        File xmlFile = new File(Environment.getDataDirectory() + "/data/" + NAMESPACE + "/tempfile.xml");
        FileReader reader = null;
        boolean success = false;
        boolean exists = false;

        try {
            reader = new FileReader(xmlFile);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(reader);
            int eventType = parser.getEventType();
            String uiType = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
						uiType = parser.getName().trim();
						if (!uiType.equalsIgnoreCase("cmparts")) {
						    Settings.System.putInt(getContentResolver(), uiType, Color.parseColor(parser.nextText()));
						}
						break;
                }
                eventType = parser.next();
            }
            success = true;
        }
        catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), R.string.xml_file_not_found, Toast.LENGTH_SHORT).show();
        }
        catch (IOException e) {
            Toast.makeText(getApplicationContext(), R.string.xml_io_exception, Toast.LENGTH_SHORT).show();
        }
        catch (XmlPullParserException e) {
            Toast.makeText(getApplicationContext(), R.string.xml_parse_error, Toast.LENGTH_SHORT).show();
        }
        catch (IllegalArgumentException e) {
            Toast.makeText(getApplicationContext(), R.string.xml_invalid_color, Toast.LENGTH_SHORT).show();
        }
        finally {
            if (reader != null) {
        		try {
	        	    reader.close();
	        	} catch (IOException e) {
	        	}
	        }
        }

        if (success) {
            Toast.makeText(getApplicationContext(), R.string.xml_import_success, Toast.LENGTH_SHORT).show();
        }
        if (xmlFile.exists())
            xmlFile.delete();
    }

    private String convertToARGB(int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + alpha + red + green + blue;
    }
}
