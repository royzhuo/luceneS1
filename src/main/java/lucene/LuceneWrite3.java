package lucene;

import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;

//写入
public class LuceneWrite3 {

    private static IndexWriter indexWriter;

    public static void main(String[] args) {
//        wirteFile("D:\\roy\\kaifa\\doc\\lucene\\dd.txt");
        createLuceneFile("D:\\roy\\kaifa\\doc\\lucene\\data",readFile("D:\\roy\\kaifa\\doc\\lucene\\dd.txt"));
    }

    /**
     * @author zhiyi.zhuo
     @desc   读取文件
     * @param path
     * @return
     */
    public static String readFile(String path){
        File file=new File(path);
        System.out.println("file Path:"+file.getAbsolutePath()+"  path:"+file.getPath());
        FileInputStream fileInputStream=null;
        InputStreamReader inputStreamReader=null;
        BufferedReader bufferedReader=null;
        StringBuffer contents=new StringBuffer();
        try {
           fileInputStream=new FileInputStream(file);
           inputStreamReader=new InputStreamReader(fileInputStream);
            bufferedReader=new BufferedReader(inputStreamReader);
            try {
                while (bufferedReader.read()!=-1){
                    String contentLine=bufferedReader.readLine();
                    contents.append(contentLine+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return contents.toString();
    }

    /**
     * @author zhiyi.zhuo
     @desc   通过字节流读取文件
     * @param path
     * @return
     */
    public static String readFileByIn(String path){
        File file=new File(path);
        FileInputStream fileInputStream=null;
        BufferedInputStream bufferedInputStream=null;
        try {
            fileInputStream=new FileInputStream(path);
            bufferedInputStream=new BufferedInputStream(fileInputStream);
            byte[] bytes=new byte[1024];
            int length=0;
            try {
                while ((length=bufferedInputStream.read())!=-1){
                    String s=new String(bytes,0,length);
                    System.out.println(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @author zhiyi.zhuo
     @desc   写入文件
     * @param filePath
     */
    public static void wirteFile(String filePath){
        File file=new File(filePath);
        FileWriter fileWriter=null;
        BufferedWriter bufferedWriter=null;
        String content="我很不高兴得相当不错，让人一目了然，很快就能找到自己需要的东西。更高兴的是，这 \n" +
                "本书没有写成一本改头换面的 API 字典，也没有把我们这些程序员看作傻瓜。（Grant Sayer，Java \n" +
                "Components Group Leader，Ceedata Systems Pty 有限公司，澳大利亚） \n" +
                "啧啧，一本可读性强、论据充分的 Java 书。外面有太多用词贫乏的 Java 书（也有几本好的），只有 \n" +
                "你的书是最好的。那些垃圾在你的书前面不值一提。（John Root，Web 开发员，伦敦社会安全部） \n" +
                "我刚刚开始看《Thinking in Java》。我希望它能有更大的突破，因";
        try {
            fileWriter=new FileWriter(file);
            bufferedWriter=new BufferedWriter(fileWriter);
            for (int i=0;i<200;i++){
                bufferedWriter.write(content);
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @author zhiyi.zhuo
       @desc   创建lucene文件
     * @param path
     */
    public static void createLuceneFile(String path,String content){
        indexWriter=getIndexWirter(path);
        String[] titles={"厦门理工学院","北京大学","南京大学"};
        for (int i=0;i<titles.length;i++){
            //创建document对象存储索引
            Document document=new Document();
            Field idField=new IntPoint("id",(i+1));
            document.add(idField);
            document.add(new StringField("title",titles[i],TextField.Store.YES));
            document.add(new TextField("content",content+titles[i], TextField.Store.YES));
            //将document对象保存到索引库中
            try {
                indexWriter.addDocument(document);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author zhiyi.zhuo
       @desc   获取lucene索引
     * @return
     */
    public static IndexWriter getIndexWirter(String pathOfDir){
        try {
            if (indexWriter==null){
                //创建中文分词解析器
                SmartChineseAnalyzer chineseAnalyzer=new SmartChineseAnalyzer();
                //创建索引写入配置
                IndexWriterConfig indexWriterConfig=new IndexWriterConfig(chineseAnalyzer);
                //创建索引存放的位置
                Directory directory= FSDirectory.open(Paths.get(pathOfDir));
                //创建索引写入对象
                indexWriter=new IndexWriter(directory,indexWriterConfig);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return indexWriter;
    }
}
