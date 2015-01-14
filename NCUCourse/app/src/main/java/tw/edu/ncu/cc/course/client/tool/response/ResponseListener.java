package tw.edu.ncu.cc.course.client.tool.response;

public interface ResponseListener<T> {
    public void onResponse( T responses );
    public void onError( Throwable throwable );
}