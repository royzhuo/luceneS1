package lucene;

import java.util.Date;

public class News {
    public Integer id;
    public String title;
    public Date writeTime;
    public Date publicationTime;
    public String content;

    public News(Integer id, String title, Date writeTime, Date publicationTime, String content) {
        this.id = id;
        this.title = title;
        this.writeTime = writeTime;
        this.publicationTime = publicationTime;
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(Date writeTime) {
        this.writeTime = writeTime;
    }

    public Date getPublicationTime() {
        return publicationTime;
    }

    public void setPublicationTime(Date publicationTime) {
        this.publicationTime = publicationTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", writeTime=" + writeTime +
                ", publicationTime=" + publicationTime +
                ", content='" + content + '\'' +
                '}';
    }
}
