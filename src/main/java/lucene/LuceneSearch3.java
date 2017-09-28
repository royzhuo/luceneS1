package lucene;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

//搜索
public class LuceneSearch3 {

    public static IndexSearcher indexSearcher;

    /**
     * @author zhiyi.zhuo
     @desc   主方法
     * @param args
     */
    public static void main(String[] args) {

    }


    /**
     * @author zhiyi.zhuo
     @desc   获取搜索对象
     * @param path
     * @return
     */
    public static IndexSearcher getIndexSearch(String path){
        try {
            if (indexSearcher==null){
                //索引存放的位置
                Directory directory= FSDirectory.open(Paths.get(path));
                //索引读取器
                IndexReader indexReader=DirectoryReader.open(directory);
                //创建索引查找器，来搜索索引库
                indexSearcher=new IndexSearcher(indexReader);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return indexSearcher;
    }

    /**
     * @author zhiyi.zhuo
     @desc   搜索
     * @param path
     * @param searchContent
     */
    public static void search(String path,String searchContent){
        //获取索引查找器
        indexSearcher=getIndexSearch(path);
        //查找解析器
        SmartChineseAnalyzer chineseAnalyzer=new SmartChineseAnalyzer();
        QueryParser queryParser=new QueryParser("content",chineseAnalyzer);
        Query query=null;
        try {
            query=queryParser.parse(searchContent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TopDocs topDocs=null;
        try {
            topDocs=indexSearcher.search(query,100);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }




}
