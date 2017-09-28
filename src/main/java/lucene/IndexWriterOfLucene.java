package lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;

public class IndexWriterOfLucene {

    public static void main(String[] args) throws ParseException {
        try {
            //1.建立索引
            StandardAnalyzer standardAnalyzer=new StandardAnalyzer();
            Directory directory=new RAMDirectory();
            IndexWriterConfig  indexWriterConfig=new IndexWriterConfig(standardAnalyzer);
            IndexWriter indexWriter=new IndexWriter(directory, indexWriterConfig);
            addDoc(indexWriter,"团建","肉");
            addDoc(indexWriter,"tuanjian","meat");
            addDoc(indexWriter,"yuying","kobe");
            //2.搜索请求
            String queryStr=args.length>0?args[0]:"lucene";
            Query query=null;
            query=new QueryParser("title",standardAnalyzer).parse(queryStr);
            //3.搜索
            int hitsPerPage=10;
            IndexReader indexReader= DirectoryReader.open(directory);
            IndexSearcher searcher=new IndexSearcher(indexReader);
            TopScoreDocCollector topScoreDocCollector=TopScoreDocCollector.create(hitsPerPage);
            searcher.search(query,topScoreDocCollector);
            ScoreDoc[] scoreDocs=topScoreDocCollector.topDocs().scoreDocs;
            //4.展示
            System.out.println("found:"+scoreDocs.length+" hits");
            for (int i=0;i<scoreDocs.length;i++){
                int docId=scoreDocs[i].doc;
                Document document=searcher.doc(docId);
                System.out.println((i+1)+" .  "+document.get("isbn")+"\t"+document.get("title"));
            }
indexReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addDoc(IndexWriter writer,String title,String isbn){
        Document document=new Document();
        FieldType fieldType=new FieldType();
        fieldType.setStored(true);
        document.add(new Field("title",title, fieldType));
        document.add(new Field("isbn",isbn, fieldType));
        try {
            writer.addDocument(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
