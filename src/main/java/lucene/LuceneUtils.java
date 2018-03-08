package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.List;

public class LuceneUtils {

    private static Analyzer analyzer;

    private static IndexWriter indexWriter;


    //分词器
    public static Analyzer getAnalyzer(){
        if (analyzer==null){
            synchronized (Analyzer.class){
                if (analyzer==null){
                    analyzer=new CJKAnalyzer();
                }
            }
        }
        return analyzer;
    }

    //创建写入文件存放对象
    public static Directory getDirectory(){
        Directory directory=new RAMDirectory();
        return directory;
    }

    //索引写入对象
    public static IndexWriter getIndexWriter(Directory directory,Analyzer analyzer) throws IOException {
        //目录
       // Directory directory=new RAMDirectory();
        if (indexWriter==null){
            synchronized (IndexWriter.class){
                if (indexWriter==null){
                    IndexWriterConfig indexWriterConfig=new IndexWriterConfig(analyzer);
                    indexWriter=new IndexWriter(directory,indexWriterConfig);
                }
            }
        }
        return indexWriter;
    }


    //数据写入库
    public static void writeContent(IndexWriter indexWriter, List<News> newss) throws IOException {
        if (newss!=null&&newss.size()>0){
            for(News news:newss){
                Document document=new Document();
                IntPoint id=new IntPoint("id",news.getId());
                FieldType fieldType=new FieldType();
                fieldType.setStored(true);
                //int字段没有store.yes的属性，无法把数据写入索引库，用storedField对象来写入索引
                StoredField idField=new StoredField("id",news.getId());
                NumericDocValuesField idNumericField=new NumericDocValuesField("id",news.getId());
                StringField nameField=new StringField("title",news.getTitle(), Field.Store.YES);
                TextField contentField=new TextField("content",news.getContent(), Field.Store.YES);
                document.add(id);
                document.add(idField);
                document.add(idNumericField);
                document.add(nameField);
                document.add(contentField);
                indexWriter.addDocument(document);
            }
            indexWriter.close();

        }
    }

}
