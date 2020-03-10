package com.nowcoder.community.entity;

//封装《分页》的相关信息
public class Page {
    //页面传过来的部分
    //当前页码
    private int current =1;
    //某页面最多显示多少个帖子
    private int limit = 10;

    //服务器传给页面的部分
    //数据总条数
    private int rows;
    //查询路径,用于处理分页的连接
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1&&limit<=100){
            this.limit = limit;
        }

    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0)
        this.rows = rows;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    //给数据库用，通过当前页的页码算出该页起始行
    public int getOffset(){
        //每页起始行=current页码*limit每页多少条数据-这一面的数据
        return (current-1)*limit;
    }
    //用来获取总的页数
    public int getTotal(){
        // rows/limit
        if(rows%limit==0)return rows/limit;
        else {
            return (rows/limit) + 1;
        }
        }

        //获取页码下方网页索引栏的起始页码
        public int getFrom(){
            int from = current-2;
            return from<1?1:from;
        }
        //获取页码下方网页索引栏的结束页码
        public int getTo(){
                int to = current+2;
                int total = getTotal();
                return to>total?total:to;
        }

}

