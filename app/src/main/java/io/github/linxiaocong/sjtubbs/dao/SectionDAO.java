package io.github.linxiaocong.sjtubbs.dao;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.models.Section;
import io.github.linxiaocong.sjtubbs.utilities.BBSUtils;

/**
 * Created by linxiaocong on 2014/10/12.
 */
public class SectionDAO {

    private static final String JSON_SECTIONS_FILENAME = "sections.json";
    private Context mContext;

    public SectionDAO(Context context) {
        mContext = context;
    }

    public ArrayList<Section> getSections() {
        ArrayList<Section> sections = new ArrayList<Section>();
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(JSON_SECTIONS_FILENAME);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray arr = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();
            for (int i = 0; i < arr.length(); ++i) {
                sections.add(new Section(arr.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            sections = BBSUtils.getInstance().getSectionList();
            if (sections != null && sections.size() > 0 && mContext != null) {
                JSONArray arr = new JSONArray();
                try {
                    for (Section s : sections) {
                        arr.put(s.toJSON());
                    }
                    OutputStream out = mContext.openFileOutput(
                            JSON_SECTIONS_FILENAME, Context.MODE_PRIVATE);
                    Writer writer = new OutputStreamWriter(out);
                    writer.write(arr.toString());
                    writer.close();
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sections;
    }
}
