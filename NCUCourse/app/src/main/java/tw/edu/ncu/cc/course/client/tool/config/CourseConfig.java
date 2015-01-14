package tw.edu.ncu.cc.course.client.tool.config;

public class CourseConfig {

    private String serverAddress;

    public CourseConfig( String serverAddress ) {
        this.serverAddress = serverAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

}
