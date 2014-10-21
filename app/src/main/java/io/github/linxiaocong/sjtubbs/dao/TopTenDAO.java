package io.github.linxiaocong.sjtubbs.dao;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.models.Topic;
import io.github.linxiaocong.sjtubbs.utilities.BBSFetchr;

public class TopTenDAO {
	private static final String JSON_FILENAME = "topten.json";
	private static final String tag = "TopTenDAO";

	private Context mContext;

	public TopTenDAO(Context c) {
		mContext = c;
	}

	public ArrayList<Topic> getTopTen() throws IOException, JSONException {
		ArrayList<Topic> topTen = new ArrayList<Topic>();
		BufferedReader reader = null;
		try {
			Log.d(tag, "retrieve top ten data from the Internet");

			topTen = BBSFetchr.getTopTen();

			if (topTen != null && topTen.size() > 0) {
				Log.d(tag, "save top ten data to json file");

				JSONArray arr = new JSONArray();
				for (Topic topic : topTen) {
					arr.put(topic.toJSON());
				}
				Writer writer = null;
				try {
					if (mContext != null) {
						OutputStream out = mContext.openFileOutput(
								JSON_FILENAME, Context.MODE_PRIVATE);
						writer = new OutputStreamWriter(out);
						writer.write(arr.toString());
					}
				} finally {
					if (writer != null)
						writer.close();
				}
			} else {
				Log.d(tag, "read top ten data from json file");

				InputStream in = mContext.openFileInput(JSON_FILENAME);
				reader = new BufferedReader(new InputStreamReader(in));
				StringBuilder jsonString = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					jsonString.append(line);
				}
				JSONArray arr = (JSONArray) new JSONTokener(
						jsonString.toString()).nextValue();
				for (int i = 0; i < arr.length(); ++i) {
					topTen.add(new Topic(arr.getJSONObject(i)));
				}
			}
		} catch (FileNotFoundException e) {
			// no need to do anything here
		} finally {
			if (reader != null)
				reader.close();
		}
		return topTen;
	}
}
