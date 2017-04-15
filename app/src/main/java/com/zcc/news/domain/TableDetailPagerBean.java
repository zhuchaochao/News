package com.zcc.news.domain;

import java.util.List;

/**
 * Created by Administrator on 2017-02-25.
 */
public class TableDetailPagerBean {

    private int retcode;
    private DataBean data;

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {


        private String title;
        private String countcommenturl;
        private String more;
        private List<TopnewsBean> topnews;
        private List<NewsBean> news;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCountcommenturl() {
            return countcommenturl;
        }

        public void setCountcommenturl(String countcommenturl) {
            this.countcommenturl = countcommenturl;
        }

        public String getMore() {
            return more;
        }

        public void setMore(String more) {
            this.more = more;
        }

        public List<TopnewsBean> getTopnews() {
            return topnews;
        }

        public void setTopnews(List<TopnewsBean> topnews) {
            this.topnews = topnews;
        }

        public List<NewsBean> getNews() {
            return news;
        }

        public void setNews(List<NewsBean> news) {
            this.news = news;
        }

        public static class TopnewsBean {

            private int id;
            private String title;
            private String topimage;
            private String url;
            private String pubdate;
            private boolean comment;
            private String commenturl;
            private String type;
            private String commentlist;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getTopimage() {
                return topimage;
            }

            public void setTopimage(String topimage) {
                this.topimage = topimage;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getPubdate() {
                return pubdate;
            }

            public void setPubdate(String pubdate) {
                this.pubdate = pubdate;
            }

            public boolean isComment() {
                return comment;
            }

            public void setComment(boolean comment) {
                this.comment = comment;
            }

            public String getCommenturl() {
                return commenturl;
            }

            public void setCommenturl(String commenturl) {
                this.commenturl = commenturl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCommentlist() {
                return commentlist;
            }

            public void setCommentlist(String commentlist) {
                this.commentlist = commentlist;
            }
        }

        public static class NewsBean {


            private int id;
            private String title;
            private String url;
            private String listimage;
            private String pubdate;
            private boolean comment;
            private String commenturl;
            private String type;
            private String commentlist;
            private String smallimage;
            private String largeimage;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getListimage() {
                return listimage;
            }

            public void setListimage(String listimage) {
                this.listimage = listimage;
            }

            public String getPubdate() {
                return pubdate;
            }

            public void setPubdate(String pubdate) {
                this.pubdate = pubdate;
            }

            public boolean isComment() {
                return comment;
            }

            public void setComment(boolean comment) {
                this.comment = comment;
            }

            public String getCommenturl() {
                return commenturl;
            }

            public void setCommenturl(String commenturl) {
                this.commenturl = commenturl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCommentlist() {
                return commentlist;
            }

            public void setCommentlist(String commentlist) {
                this.commentlist = commentlist;
            }

            public String getSmallimage() {
                return smallimage;
            }

            public void setSmallimage(String smallimage) {
                this.smallimage = smallimage;
            }

            public String getLargeimage() {
                return largeimage;
            }

            public void setLargeimage(String largeimage) {
                this.largeimage = largeimage;
            }
        }
    }
}
