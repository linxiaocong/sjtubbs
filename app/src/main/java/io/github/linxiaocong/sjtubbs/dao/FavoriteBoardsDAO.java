package io.github.linxiaocong.sjtubbs.dao;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

import io.github.linxiaocong.sjtubbs.models.Board;

/**
 * Created by linxiaocong on 2014/10/26.
 */
public class FavoriteBoardsDAO {

    public static final String JSON_FAVORITE_FILENAME = "favorite.json";

    private Context mContext;

    public FavoriteBoardsDAO(Context context) {
        mContext = context;
    }

    public void getFavoriteBoards(ArrayList<Board> results) {
        BufferedReader reader = null;
        try {
            InputStream in = mContext.openFileInput(JSON_FAVORITE_FILENAME);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            JSONArray arr = (JSONArray) new JSONTokener(jsonString.toString())
                    .nextValue();
            for (int i = 0; i < arr.length(); ++i) {
                results.add(new Board(arr.getJSONObject(i)));
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public synchronized void saveFavoriteBoards(ArrayList<Board> boards) {
        JSONArray arr = new JSONArray();
        try {
            for (Board board: boards) {
                arr.put(board.toJSON());
            }
            OutputStream out = mContext.openFileOutput(
                    JSON_FAVORITE_FILENAME, Context.MODE_PRIVATE);
            Writer writer = new OutputStreamWriter(out);
            writer.write(arr.toString());
            writer.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
