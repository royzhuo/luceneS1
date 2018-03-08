package lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class lucene2 {
    public static void main(String[] args) throws ParseException, InvalidTokenOffsetsException, IOException {
//        testLucene1();
//        testLucene2();
//        testLucene3();
//        testLuceneHighLight();//
//        lucenePageTest();
        testLuceneSort();
    }


    private static void testLucene1() throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();
        // Store the index in memory:
        Directory directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);
        Document doc = new Document();
        String text = "This is the text to be indexed.";
        String text2 = "This is the text to be value.";
        doc.add(new Field("fieldname", text, TextField.TYPE_STORED));
        doc.add(new Field("fieldvalue", text2, TextField.TYPE_STORED));
        iwriter.addDocument(doc);
        iwriter.close();
        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("fieldname", analyzer);
        Query query = parser.parse("*dex*");
/*        Term term=new Term("fieldname","Th");
        Query query=new PrefixQuery(term);*/
//        ScoreDoc[] hits = isearcher.search(query, 1000, null).scoreDocs;
        ScoreDoc[] hits = isearcher.search(query, 1000).scoreDocs;
        // assertEquals(1, hits.length);
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            //assertEquals("This is the text to be indexed.", hitDoc.get("fieldvalue"));
            System.out.println(hitDoc.get("fieldvalue") + "  " + hitDoc.getField("fieldvalue").stringValue());
        }
        ireader.close();
        directory.close();
    }

    //lucene 三种分词器分词结果
    public static void testLucene2() throws IOException {
        String content = "我在学习java";
        Analyzer analyzer1 = new StandardAnalyzer();//标准分词器
        Analyzer analyzer2 = new SimpleAnalyzer();//简单分词器
        Analyzer analyzer3 = new CJKAnalyzer();//二元分词器
        TokenStream tokenStream = analyzer1.tokenStream("content", new StringReader(content));//生成一个分词流
        CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);//为token设置属性类
        tokenStream.reset();//重新设置
        System.out.println("运行结果:");
        while (tokenStream.incrementToken()) {
            System.out.println(new String(termAttribute.buffer(), 0, termAttribute.length()) + "");
        }
    }

    //多条件查询
    public static void testLucene3() throws IOException, ParseException {
        //1.初始化要索引的内容
        String content = "问我aaams d dsdsd ee,health life,stronger body.i am a good student,my score is  83";
        //2.初始化lucene写入索引
        Directory directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document document = new Document();
        Document document2 = new Document();
        FieldType fieldType = new FieldType();
        document.add(new TextField("content", content, Field.Store.YES));
//        document.add(new Field("score",String.valueOf(80),fieldType));
        document.add(new IntPoint("score", 66));
        document.add(new IntPoint("score", 60));

        document2.add(new TextField("content", "dsddsds skldsjflife aaaa life ewwewew", Field.Store.YES));
//        document.add(new Field("score",String.valueOf(80),fieldType));
        document2.add(new IntPoint("score", 85));
        document2.add(new IntPoint("score", 77));
        document2.add(new IntPoint("score", 60));
        indexWriter.addDocument(document);
        indexWriter.addDocument(document2);
        indexWriter.close();
        //3.搜索
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        String queryContent = "life";
        //booleanquery为组合查询，可以拼接多个条件
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        //解析用户的查询字符串进行搜索,是一个解析用户输入的工具，可以通过扫描用户输入的字符串，生成Query对象
        QueryParser queryParser1 = new QueryParser("content", analyzer);
        Query query1 = queryParser1.parse(queryContent);
        Query query2 = IntPoint.newRangeQuery("score", 62, 85);
        booleanQuery.add(query1, BooleanClause.Occur.MUST);//MUST和MUST：取得连个查询子句的交集
        booleanQuery.add(query2, BooleanClause.Occur.MUST);//SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集

        Query query = booleanQuery.build();
        //4.查询结果
        TopDocs topDocs = indexSearcher.search(query, 100);//查询前100条结果
        System.out.println("总计录数:" + topDocs.totalHits);
        //返回命中
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //得到返回结果id
            int docId = scoreDoc.doc;
            Document document1 = indexSearcher.doc(docId);
            System.out.println("内容：" + document1.get("content") + "  score: " + document1.get("score"));
            System.out.println("id(序号):" + docId + " scores(相关度得分):" + scoreDoc.score + " index(文档编号):" + scoreDoc.shardIndex);

        }

    }

    //高亮显示
    public static void testLuceneHighLight() throws IOException, ParseException, InvalidTokenOffsetsException {
        //1.初始化要索引的内容
        String content="问我aaams d dsdsd ee,health life,stronger body.i am a good student,my score is  83";
        //2.初始化lucene写入索引
        Directory directory=new RAMDirectory();
        Analyzer analyzer=new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig=new IndexWriterConfig(analyzer);
        IndexWriter indexWriter=new IndexWriter(directory,indexWriterConfig);
        Document document=new Document();
        Document document2=new Document();
        FieldType fieldType=new FieldType();
        document.add(new TextField("content",content, Field.Store.YES));
//        document.add(new Field("score",String.valueOf(80),fieldType));
        document.add(new IntPoint("score",66));
        document.add(new IntPoint("score",60));

        document2.add(new TextField("content","dsddsds skldsjflife aaaa life ewwewew", Field.Store.YES));
//        document.add(new Field("score",String.valueOf(80),fieldType));
        document2.add(new IntPoint("score",85));
        document2.add(new IntPoint("score",77));
        document2.add(new IntPoint("score",60));
        indexWriter.addDocument(document);
        indexWriter.addDocument(document2);
        indexWriter.close();
        //3.搜索
        IndexReader indexReader=DirectoryReader.open(directory);
        IndexSearcher indexSearcher=new IndexSearcher(indexReader);

        String queryContent="life";
        //booleanquery为组合查询，可以拼接多个条件
        BooleanQuery.Builder booleanQuery=new BooleanQuery.Builder();
        //解析用户的查询字符串进行搜索,是一个解析用户输入的工具，可以通过扫描用户输入的字符串，生成Query对象
        QueryParser queryParser1=new QueryParser("content",analyzer);
        Query query1=queryParser1.parse(queryContent);
        Query query2= IntPoint.newRangeQuery("score",62,85);
        booleanQuery.add(query1, BooleanClause.Occur.MUST);//MUST和MUST：取得连个查询子句的交集
        booleanQuery.add(query2, BooleanClause.Occur.MUST);//SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集

        Query query=booleanQuery.build();
        //4.查询结果
        TopDocs topDocs=indexSearcher.search(query,100);//查询前100条结果
        System.out.println("总计录数:"+topDocs.totalHits);
        //返回命中
        ScoreDoc[] scoreDocs=topDocs.scoreDocs;
        //5.设置高亮显示
        Formatter formatter=new SimpleHTMLFormatter("<font color='red'>","</font>");
        Scorer scorer=new QueryScorer(query);//检索评分
        Fragmenter fragmenter=new SimpleFragmenter(100);//设置片段大于100
        Highlighter highlighter=new Highlighter(formatter,scorer);//初始化高亮显示类
        highlighter.setTextFragmenter(fragmenter);//设置格式
        for (ScoreDoc scoreDoc:scoreDocs){
            //得到返回结果id
            int docId=scoreDoc.doc;
            Document document1=indexSearcher.doc(docId);
            String resultContent=document1.get("content");
            TokenStream tokenStream=analyzer.tokenStream("content",new StringReader(resultContent));
            String finalContent=highlighter.getBestFragment(tokenStream,resultContent);
            System.out.println("内容："+finalContent+"  score: "+document1.get("score"));
            System.out.println("id(序号):"+docId+" scores(相关度得分):"+scoreDoc.score+" index(文档编号):"+scoreDoc.shardIndex);

        }
    }

    //lucene分页
    public static void lucenePageTest() throws IOException, ParseException {
        //1.初始化要索引的内容
        String content = "问我aaams d dsdsd ee,health life,stronger body.i am a good student,my score is  83";
        //2.初始化lucene写入索引
        Directory directory = new RAMDirectory();
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        Document document = new Document();
        Document document2 = new Document();
        FieldType fieldType = new FieldType();
        document.add(new TextField("content", content, Field.Store.YES));
//        document.add(new Field("score",String.valueOf(80),fieldType));
        document.add(new IntPoint("score", 66));
        document.add(new IntPoint("score", 60));

        document2.add(new TextField("content", "dsddsds skldsjflife aaaa life ewwewew", Field.Store.YES));
//        document.add(new Field("score",String.valueOf(80),fieldType));
        document2.add(new IntPoint("score", 85));
        document2.add(new IntPoint("score", 77));
        document2.add(new IntPoint("score", 60));
        indexWriter.addDocument(document);
        indexWriter.addDocument(document2);
        indexWriter.close();
        //3.搜索
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        String queryContent = "life";
        //booleanquery为组合查询，可以拼接多个条件
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        //解析用户的查询字符串进行搜索,是一个解析用户输入的工具，可以通过扫描用户输入的字符串，生成Query对象
        QueryParser queryParser1 = new QueryParser("content", analyzer);
        Query query1 = queryParser1.parse(queryContent);
        Query query2 = IntPoint.newRangeQuery("score", 62, 85);
        booleanQuery.add(query1, BooleanClause.Occur.MUST);//MUST和MUST：取得连个查询子句的交集
        booleanQuery.add(query2, BooleanClause.Occur.MUST);//SHOULD与SHOULD：表示“或”关系，最终检索结果为所有检索子句的并集

        Query query = booleanQuery.build();
        //4.查询结果
//        TopDocs topDocs = indexSearcher.search(query, 100);//查询前100条结果
//        System.out.println("总计录数:" + topDocs.totalHits);
        //分页
        TopScoreDocCollector topScoreDocCollector=TopScoreDocCollector.create(100);
        indexSearcher.search(query,topScoreDocCollector);
        TopDocs topDocs=topScoreDocCollector.topDocs(0,2);
        //返回命中
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //得到返回结果id
            int docId = scoreDoc.doc;
            Document document1 = indexSearcher.doc(docId);
            System.out.println("内容：" + document1.get("content") + "  score: " + document1.get("score"));
            System.out.println("id(序号):" + docId + " scores(相关度得分):" + scoreDoc.score + " index(文档编号):" + scoreDoc.shardIndex);

        }
    }

    //排序
    public static void testLuceneSort() throws IOException, ParseException {
        Analyzer analyzer=LuceneUtils.getAnalyzer();
        Directory directory=LuceneUtils.getDirectory();
        IndexWriter indexWriter=LuceneUtils.getIndexWriter(directory,analyzer);
        //创建要写入的对象
        List<News> newsList=new ArrayList<>();
        News news1=new News(10001,"鲁滨孙荒岛生活一个星期",new Date(),new Date(),"鲁滨孙和他的队友在美国太平洋荒岛生活一星期");
        News news2=new News(10002,"滨孙荒岛生活2个星期",new Date(),new Date(),"鲁滨孙和他的队友在中国南海荒岛生活二星期");
        News news3=new News(10003,"鲁滨荒岛生活3个星期",new Date(),new Date(),"鲁滨孙和他的队友在北极北冰洋海荒岛生活三星期");
        newsList.add(news1);
        newsList.add(news2);
        newsList.add(news3);
        //添加索引
        LuceneUtils.writeContent(indexWriter,newsList);

        //创建索引读取对象和搜索对象
        IndexReader indexReader=DirectoryReader.open(directory);
        IndexSearcher indexSearcher=new IndexSearcher(indexReader);

        //搜索关键字
        String queryContent="鲁滨荒岛生活3个星期";
        //创建需要用于查询的字段数组
        String[] fields={"title"};
        //创建用于查询的类分析器
        QueryParser queryParser=new MultiFieldQueryParser(fields,analyzer);
        //查询符合关键字的数据
        Query query=queryParser.parse(queryContent);

        //创建排序字段用于升序
//        SortField sortField=new SortField("id", SortField.Type.INT);
        //创建排序字段用于降序
        SortField sortField=new SortField("id",SortField.Type.INT);

        //创建sort排序字段
        Sort sort=new Sort(sortField);

        TopDocs topDocs=indexSearcher.search(query,100,sort);

        //返回的结果对象
        ScoreDoc[] scoreDocs=topDocs.scoreDocs;
        //命中数
        int hitsCount=topDocs.totalHits;
        System.out.println("命中总数:"+hitsCount);

        for (ScoreDoc scoreDoc:scoreDocs){
            Document document=indexSearcher.doc(scoreDoc.doc);
            String resultId= document.get("id");
            System.out.println("id:"+resultId);
        }



    }


}
