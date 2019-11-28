package com.lease.framework.persistence.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lease.framework.core.LogUtils;
import com.lease.framework.core.StringUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by hxd on 15/6/23.
 */
public class DefaultOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DefaultOpenHelper";

    /**
     * 当前DB的版本号
     */
    private static final int SCHEMA_VERSION = 1;

    /**
     * 数据库文件名
     */
    public static final String DATABASE_NAME = "meiyou.db";

    /**
     * 创建表文件，onCreate时使用，用于创建所有表
     */
    private static final String CREATE_TABLE_FILE = "/CreateTablesSQL.xml";

    /**
     * 创建index/trigger文件，onCreate时使用
     */
    private static final String CREATE_SQL_FILE = "/CreateSQL.xml";

    /**
     * 数据库升级文件，onUpgrade时使用
     */
    private static final String UPGRADE_SQL_FILE = "/UpgradeSQL.xml";
    private static final String UPGRADE_SQL_KEY = "From%1$dTo%2$d";



    public DefaultOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        recreateAllTables(db);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LogUtils.i(TAG, "数据库版本升级，版本从[" + oldVersion + "]至[" + newVersion + "]");

        int countBetweenNewVer = newVersion - oldVersion;

        //判断每一个版本是否都成功升级了。这就要确保每个版本号都是连续的
        boolean hasUpgradeEachVersion = false;
        int procVer = oldVersion;
        for(int i=0 ; i < countBetweenNewVer ; i++){
            String upgradeSqlKey = String.format(UPGRADE_SQL_KEY, procVer,procVer+1);
            LogUtils.w(TAG, upgradeSqlKey);
            if(! upgradeUseXmlFile(upgradeSqlKey,db)){
                hasUpgradeEachVersion = false;
                break;
            }
            hasUpgradeEachVersion = true;
            procVer += 1;
        }

        //如果没有升级成功，则全部删除表，并重建
        if(! hasUpgradeEachVersion){
            recreateAllTables(db);
        }
    }


    /**
     * 重新创建db，包括创建所有table，以及索引、触发器等
     * @param db
     */
    private void recreateAllTables(SQLiteDatabase db) {
        SAXParser parser;
        //  create all tables
        try {
            LogUtils.i(TAG, "初始化数据库表...");
            parser = SAXParserFactory.newInstance().newSAXParser();
            InputStream sqlFileStream = this.getClass().getResourceAsStream(CREATE_TABLE_FILE);
            XMLContentHandler xmlHandler = new XMLContentHandler();
            parser.parse(sqlFileStream, xmlHandler);
            sqlFileStream.close();

            for (String sql : xmlHandler.getSQLList()) {
                db.execSQL(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // do other initialization, such as create index/trigger
        try {
            LogUtils.i(TAG, "初始化数据库其它...");
            if (parser == null) {
                parser = SAXParserFactory.newInstance().newSAXParser();
            }
            InputStream sqlFileStream = this.getClass().getResourceAsStream(CREATE_SQL_FILE);
            XMLContentHandler xmlHandler = new XMLContentHandler();
            parser.parse(sqlFileStream, xmlHandler);
            sqlFileStream.close();

            for (String sql : xmlHandler.getSQLList()) {
                db.execSQL(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 使用xml中的升级sql对数据库执行升级操作
     * @param upgradeSqlKey
     * @param db
     * @return 是否成功执行升级sql，如果没有读取到升级sql、或者出现异常，都返回false
     */
    private boolean upgradeUseXmlFile(String upgradeSqlKey, SQLiteDatabase db){
        try{
            List<String> sqls = new ArrayList<String>();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);

            InputStream sqlFileStream = this.getClass().getResourceAsStream(UPGRADE_SQL_FILE);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(sqlFileStream);
            Element root = dom.getDocumentElement();
            root.normalize();

            LogUtils.d(TAG, "get all sqls.");
            NodeList items = root.getElementsByTagName(upgradeSqlKey);// 查找节点
            if(items == null || items.getLength() < 1) {
                LogUtils.e(TAG, upgradeSqlKey + " not exit or child item is empty.");
                return false;
            }
            //取 upgradeSqlKey 节点下的 sql元素
            NodeList sqlNodes = items.item(0).getChildNodes();
            Node sqlEle ;
            for(int i=0;i<sqlNodes.getLength();i++){
                sqlEle = sqlNodes.item(i);
                if(sqlEle.getNodeType() == Node.ELEMENT_NODE){
                    if(StringUtils.equalsIgnoreCase("sql", sqlEle.getNodeName())){
                        NodeList sqlEleChilds = sqlEle.getChildNodes();
                        for(int j=0; j<sqlEleChilds.getLength() ;j++ ){
                            Node sqlEleC = sqlEleChilds.item(j);
                            //只读CDATA节点的内容
                            if(sqlEleC.getNodeType() == Node.CDATA_SECTION_NODE){
                                String sql = sqlEleC.getNodeValue();
                                sql = StringUtils.trimToNull(StringUtils.replaceEach(sql, new String[]{"\t","\n"}, new String[]{" "," "}));
                                if(sql != null){
                                    sqls.add(sql);
                                }
                            }
                        }
                    }
                }
            }

            //不能没有升级sql
            if(sqls.isEmpty()) {
                LogUtils.e(TAG, upgradeSqlKey + " parse sqls is empty.");
                return false;
            }

            db.beginTransaction();
            try{
                int i=0;
                for(String sql : sqls){
                    LogUtils.d(TAG, "upgrade sql "+i);
                    db.execSQL(sql);
                    ++i;
                }
                db.setTransactionSuccessful();
            }finally{
                db.endTransaction();
            }
            return true;
        }catch(Exception e){
            LogUtils.e(TAG,"升级数据库失败！upgradeSqlKey："+upgradeSqlKey,e);
        }
        return false;
    }




    /**
     * 新建DB时附加SQL的xml文件解析器
     * @author jinzhaoyu
     */
    private class XMLContentHandler extends DefaultHandler {

        private List<String> sqlList;
        private StringBuilder sql = new StringBuilder();

        /*
         * (non-Javadoc)
         *
         * @see org.xml.sax.helpers.DefaultHandler#startDocument()
         */
        @Override
        public void startDocument() throws SAXException {
            sqlList = new ArrayList<String>();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
         * java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {
            if(StringUtils.equalsIgnoreCase("sql", localName)){
                sql = new StringBuilder();
            }else{
                sql = null;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (sql != null) {
                sql.append(ch,start,length);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
         * java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if(sql != null && StringUtils.equalsIgnoreCase("sql", localName)){
                String rs = sql.toString();
                rs = StringUtils.replaceEach(rs, new String[]{"\t","\n"}, new String[]{" "," "});
                if (StringUtils.isNotBlank(rs)) {
                    sqlList.add(rs);
                }
            }
        }

        /**
         * @return
         */
        public List<String> getSQLList() {
            return sqlList;
        }
    }

}
