package io.github.linxiaocong.sjtubbs.utilities;

import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
            if (userId != null) {
                mCookies.clear();
                mCookies.put(COOKIE_UTMPKEY, response.cookie(COOKIE_UTMPKEY));
                mCookies.put(COOKIE_UTMPNUM, response.cookie(COOKIE_UTMPNUM));
                mCookies.put(COOKIE_UTMPUSERID, userId);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCookies.clear();
        return false;
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

    static ArrayList<Board> getBoards(String sectionUrl) {
        return parseBoards(sectionUrl);
    }

    static ArrayList<Board> parseBoards(String url) {
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

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
