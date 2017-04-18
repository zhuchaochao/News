# News
  ## 说明：
```
这是学习尚硅谷的学习视频，敲出来的练手作品，所以使用的后台数据信息也是尚硅谷提供的。
```
  ##演示说明：
* 请求数据练习使用了okhttp,xutil,volley。
* 加载图片和显示图片，练习使用了xutil，volley，glide，picaso，imageloader。
* 新闻页面的RefreshListView是自定义的，顶部tab栏使用的是[ViewPagerIndicator](https://github.com/JakeWharton/ViewPagerIndicator)。
* 专题页面的RefreshListView使用的是第三方的库,顶部tab栏使用的是系统自带的TabLayout。
* 图组页面单击显示图片并通过手势可以缩放图片，使用的是第三方[PhotoView](https://github.com/chrisbanes/PhotoView)库。
* 在新闻和专题页面上的轮播图是用viewpage实现的，而父层也是使用viewpage实现的，轮播图部分的事件会被父层拦截，就会出bug,所以要自定义父层viewpage。主要代码如下：
```java
 @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
* 轮播图又会与slidingMenu有Bug，是向左滑动轮播图时，本来是要滑动轮播图，却滑开了slidingMenu，所以自定轮播图viewpage。主要代码如下：
```java
@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX=ev.getX();
                startY=ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = endX - startX;
                float distanceY = endY - startY;
                if(Math.abs(distanceX)>Math.abs(distanceY)){
                    //水平方向
                    //当滑到ViewPager第一个页面的时候，并且是从左往右滑
                    if(getCurrentItem()==0&&distanceX>0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else if (getCurrentItem()==(getAdapter().getCount()-1)&&distanceX<0){
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else{
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else{
                    //垂直方向
                    //要求父视图不拦截触摸事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return super.dispatchTouchEvent(ev);
    }
```
  ## 演示 


![](https://github.com/zhuchaochao/Images/raw/master/News/news.gif)
![](https://github.com/zhuchaochao/Images/raw/master/News/topic.gif)
![](https://github.com/zhuchaochao/Images/raw/master/News/photos.gif)