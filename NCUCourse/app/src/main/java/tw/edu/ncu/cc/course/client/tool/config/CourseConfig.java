package tw.edu.ncu.cc.course.client.tool.config;

public class CourseConfig {

    private String serverAddress;
    private String language;

    public CourseConfig( String serverAddress, String language ) {
        this.serverAddress = serverAddress;
        this.language = language;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getLanguage() {
        return language;
    }
}
