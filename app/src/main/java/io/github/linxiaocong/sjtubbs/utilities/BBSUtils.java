package io.github.linxiaocong.sjtubbs.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import io.github.linxiaocong.sjtubbs.models.Board;
import io.github.linxiaocong.sjtubbs.models.Reply;
import io.github.linxiaocong.sjtubbs.models.Section;
import io.github.linxiaocong.sjtubbs.models.Topic;

public class BBSUtils {

    public static final String BBS_INDEX = "https://bbs.sjtu.edu.cn";
    private static final String tag = "BBSUtils";
    private static BBSUtils mInstance;
    private static final String COOKIE_UTMPKEY = "utmpkey";
    private static final String COOKIE_UTMPNUM = "utmpnum";
    private static final String COOKIE_UTMPUSERID = "utmpuserid";
    private HashMap<String, String> mCookies = new HashMap<String, String>();
    private boolean mIsLoggedIn;

    public static synchronized BBSUtils getInstance() {
        if (mInstance == null) {
            mInstance = new BBSUtils();
        }
        return mInstance;
    }

    public boolean login(String username, String password) {
        String loginUrl = BBS_INDEX + "/bbswaplogin";
        try {
            Connection.Response response = Jsoup.connect(loginUrl)
                    .data("id", username, "pw", password)
                    .method(Connection.Method.POST)
                    .execute();
            String userId = response.cookie(COOKIE_UTMPUSERID);
            String utmpKey = response.cookie(COOKIE_UTMPKEY);
            String utmpNum = response.cookie(COOKIE_UTMPNUM);
            if (userId != null && utmpKey != null && utmpNum != null) {
                mCookies.clear();
                mCookies.put(COOKIE_UTMPKEY, utmpKey);
                mCookies.put(COOKIE_UTMPNUM, utmpNum);
                mCookies.put(COOKIE_UTMPUSERID, userId);
                mIsLoggedIn = true;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCookies.clear();
        return false;
    }

    public String getCookies() {
        return COOKIE_UTMPNUM + "=" + mCookies.get(COOKIE_UTMPNUM) + " ;" +
                COOKIE_UTMPKEY + "=" + mCookies.get(COOKIE_UTMPKEY) + " ;" +
                COOKIE_UTMPUSERID + "=" + mCookies.get(COOKIE_UTMPUSERID);
    }

    public ArrayList<Topic> getTopTen() {
        String topTenUrl = BBS_INDEX + "/file/bbs/mobile/top100.html";
        ArrayList<Topic> topTen = new ArrayList<Topic>();
        try {
            Document doc = Jsoup.parse(new URL(topTenUrl).openStream(), "gbk", topTenUrl);
            Elements links = doc.getElementsByTag("a");
            for (int i = 0; i < links.size(); i += 2) {
                Element boardEle = links.get(i);
                Element topicEle = links.get(i + 1);
                String board = boardEle.text();
                String title = topicEle.text();
                String author = topicEle.nextSibling().outerHtml();
                String url = BBS_INDEX + topicEle.attr("href");
                String id = url.substring(url.lastIndexOf('=') + 1);
                Topic topic = new Topic(id, board, title, author, url);
                topTen.add(topic);
            }
        } catch (IOException e) {
            Log.e(tag, "Error while retrieving top ten list");
            e.printStackTrace();
        }
        return topTen;
    }

    public String getTopicList(String boardUrl, ArrayList<Topic> topicList) {
        String nextUrl = null;
        try {
            boardUrl = boardUrl.replace("bbsdoc", "bbstdoc");
            Log.d(tag, "get topic list from board: " + boardUrl);
            Document doc = Jsoup.connect(boardUrl).get();
            Element nextUrlElement = doc.select("hr ~ a").first();
            nextUrl = BBS_INDEX + "/" + nextUrlElement.attr("href");
            Elements trElements = doc.getElementsByTag("tbody").select("tr");
            for (int i = trElements.size() - 1; i >= 1; --i) {
                Elements tdElements = trElements.get(i).select("td");
                String topicAuthor = tdElements.get(2).select("a").first().text();
                Element titleElement = tdElements.get(4).select("a").first();
                String topicTitle = titleElement.text().trim();
                String topicUrl = BBS_INDEX + "/" + titleElement.attr("href").replace("bbstcon", "bbswaptcon");
                String topicId = null;
                if (topicUrl.endsWith("html")) {
                    topicId = topicUrl.substring(topicUrl.lastIndexOf(',') + 1, topicUrl.lastIndexOf('.'));
                } else {
                    topicId = topicUrl.substring(topicUrl.lastIndexOf('=') + 1);
                }
                Topic topic = new Topic(topicId, "", topicTitle, topicAuthor, topicUrl);
                if (!topicList.contains(topic)) {
                    topicList.add(topic);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nextUrl;
    }

    public String getReplyList(String topicUrl, ArrayList<Reply> replyList) {
        String nextUrl = null;
        try {
            Log.d(tag, "get replies for url: " + topicUrl);
            Document doc = Jsoup.parse(new URL(topicUrl).openStream(), "gbk", topicUrl);
            Elements replyElements = doc.getElementsByTag("pre");
            for (Element replyElement : replyElements) {
                String replyHTML = replyElement.html();
                if (topicUrl.startsWith(BBS_INDEX + "/bbstopcon")) {
                    String content = replyHTML.replace("\n", "<br />");
                    replyList.add(new Reply("", "", "", content, ""));
                } else {
                    String replyUrl = replyElement.getElementsByTag("a")
                            .first().attr("href");

                    if (!replyUrl.startsWith("http")) {
                        replyUrl = BBS_INDEX + "/" + replyUrl;
                    }

                    try {
                        replyHTML = replyHTML.substring(replyHTML.indexOf(']') + 2);
                        int index = replyHTML.indexOf('\n');
                        String firstLine = replyHTML.substring(0, index);
                        replyHTML = replyHTML.substring(index + 1);
                        index = firstLine.indexOf(' ');
                        String replyId = firstLine.substring(0, index);
                        String replyTime = firstLine.substring(index + 1);
                        index = replyHTML.indexOf('\n');
                        String replyTitle = replyHTML.substring(0, index);
                        String replyContent = replyHTML.substring(index + 1)
                                .replace("\n", "<br />");
                        replyList.add(new Reply(replyId, replyTime, replyTitle,
                                replyContent, replyUrl));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Elements elements = doc.select("body > a");
            for (Element element : elements) {
                if (element.text().equals("下一页")) {
                    nextUrl = BBS_INDEX + "/" + element.attr("href");
                }
            }
        } catch (IOException e) {
            Log.e(tag, "Error while retrieving replies");
            e.printStackTrace();
        }
        return nextUrl;
    }

    public ArrayList<Section> getSectionList() {
        final String sectionUrl = BBS_INDEX + "/bbssec";
        ArrayList<Section> sections = new ArrayList<Section>();
        try {
            Document doc = Jsoup.connect(sectionUrl).get();
            Elements sectionElements = doc.getElementsByTag("tbody").select(
                    "tr");
            for (int i = 1; i < sectionElements.size(); ++i) {
                Element sectionElement = sectionElements.get(i)
                        .getElementsByTag("td").get(1).select("a").first();
                String name = sectionElement.text();
                String url = BBS_INDEX + "/" + sectionElement.attr("href");
                ArrayList<Board> boards = getBoards(url);
                Section section = new Section(name, url, boards);
                sections.add(section);
            }
        } catch (IOException e) {
            Log.e(tag, "Error while retrieving sections");
            e.printStackTrace();
        }
        return sections;
    }

    public ArrayList<Board> getBoards(String sectionUrl) {
        return parseBoards(sectionUrl);
    }

    public ArrayList<Board> parseBoards(String url) {
        ArrayList<Board> boards = new ArrayList<Board>();
        ArrayList<Board> subBoards = null;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements boardTrElements = doc.getElementsByTag("tbody").select(
                    "tr");
            for (int i = 1; i < boardTrElements.size(); ++i) {
                Elements boardTdElements = boardTrElements.get(i)
                        .getElementsByTag("td");
                String boardUrl = BBS_INDEX
                        + "/"
                        + boardTdElements.get(2).select("a").first()
                        .attr("href");
                String boardName = boardTdElements.get(2).select("a").first()
                        .text();
                String boardDesc = boardTdElements.get(5).select("a").first()
                        .text();
                String hasSubBoards = boardTdElements.get(1).text();
                if (hasSubBoards.equals("＋")) {
                    subBoards = parseBoards(boardUrl);
                    boards.add(new Board(boardName, boardDesc, boardUrl,
                            subBoards));
                } else {
                    boards.add(new Board(boardName, boardDesc, boardUrl));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return boards;
    }

    public String getUploadedPictures(String url, ArrayList<String> uploadedPictures) {
        String nextUrl = null;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements pictureElements = doc.select("a[target=_blank]");
            for (int i = pictureElements.size() - 1; i > 0; --i) {
                uploadedPictures.add(pictureElements.get(i).attr("href"));
            }
            Elements linkElements = doc.getElementsByTag("a");
            for (int i = linkElements.size() - 1; i > 0; --i) {
                Element link = linkElements.get(i);
                if (link.text().equals("上一页")) {
                    nextUrl = BBS_INDEX + "/" + link.attr("href");
                    Log.d(tag, "uploaded pictures nextUrl: " + nextUrl);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return nextUrl;
    }

    public String uploadPicture(Context context, Uri uri, String boardName) {
        if (!mIsLoggedIn) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(BBS_INDEX + "/bbsdoupload");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpPost.addHeader("Cookie", getCookies());
            httpPost.addHeader("Connection", "keep-alive");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addTextBody("board", boardName);
            builder.addTextBody("level", "0");
            builder.addTextBody("live", "180");
            builder.addTextBody("exp", "");
            builder.addTextBody("MAX_FILE_SIZE", "1048577");
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            File tempfile = File.createTempFile("upload_", ".jpg", context.getCacheDir());
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            while (byteArrayOutputStream.size() > 1048577) {
                byteArrayOutputStream.reset();
                quality -= 5;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
            }
            FileOutputStream outputStream = new FileOutputStream(tempfile);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(byteArrayOutputStream.toByteArray());
            bufferedOutputStream.close();
            builder.addBinaryBody("up", tempfile, ContentType.APPLICATION_FORM_URLENCODED,
                    tempfile.getName());
            httpPost.setEntity(builder.build());
            HttpResponse response = httpClient.execute(httpPost);
            String responseHtml = EntityUtils.toString(response.getEntity());
            Document doc = Jsoup.parse(responseHtml);
            String url = doc.select("p > font").text();
            return url;
        } catch (Exception err) {

        }
        return null;
    }

    public boolean post(ArrayList<NameValuePair> nameValuePairs) {
        try {
            HttpPost httpPost = new HttpPost(BBS_INDEX + "/bbssnd");
            DefaultHttpClient client = new DefaultHttpClient();
            httpPost.addHeader("Cookie", getCookies());
            httpPost.addHeader("Connection", "keep-alive");
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "GB2312"));
            HttpResponse httpResponse = client.execute(httpPost);
            String result = EntityUtils.toString(httpResponse.getEntity());
            if (result == null || result.contains("ERROR")) {
                return false;
            }
            return true;
        } catch (Exception err) {
            err.printStackTrace();
        }
        return false;
    }
}
