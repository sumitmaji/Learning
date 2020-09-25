import com.sum.udemy.modal.Course;
import com.sum.udemy.modal.CourseDetails;
import com.sum.udemy.modal.SubscribedCourses;
import com.sum.udemy.util.OperationSupport;
import com.sum.udemy.util.SystemPropertyUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main<T> {
    private static String SUBSCRIBED_COURSES = "https://"+ SystemPropertyUtil.getUrl()+"/api-2.0/users/me/subscribed-courses/?page=2&page_size=1400";
    private static String COURSE = "https://"+SystemPropertyUtil.getUrl()+"/api-2.0/courses/%d/subscriber-curriculum-items/" +
            "?page_size=1400&fields[lecture]=title,object_index,is_published,sort_order,created,asset,supplementary_assets," +
            "is_free&fields[quiz]=title,object_index,is_published,sort_order,type&fields[practice]=title,object_index," +
            "is_published,sort_order&fields[chapter]=title,object_index,is_published,sort_order&fields[asset]=title," +
            "filename,asset_type,status,time_estimation,is_external&caching_intent=True";
    private static String VIDEO = "https://"+SystemPropertyUtil.getUrl()+"/api-2.0/users/me/subscribed-courses/2043700/lectures/" +
            "12872856/?fields[asset]=stream_urls";
    private static String ARTICLE = "https://"+SystemPropertyUtil.getUrl()+"/api-2.0/assets/18810460/?fields[asset]=body";
    private static String DOCUMENT = "https://"+SystemPropertyUtil.getUrl()+"/api-2.0/users/me/subscribed-courses/2043700/lectures/" +
            "12749775/supplementary-assets/15501723/?fields[asset]=download_urls";
    private static String EXTERNAL_LINK = "https://"+SystemPropertyUtil.getUrl()+"/api-2.0/users/me/subscribed-courses/2043700/lectures/" +
            "15515450/supplementary-assets/19219304/?fields[asset]=external_url";
    private static Set<String> completed = new HashSet<>();

    static {
        completed.add("Spring Framework Master Class - Java Spring the Modern Way");
    }

    public static void main(String[] args) throws Exception {

        OperationSupport<SubscribedCourses> subsCoursesMain = new OperationSupport<>();
        SubscribedCourses subsCourses = subsCoursesMain.getData(SUBSCRIBED_COURSES, SubscribedCourses.class);

        for (CourseDetails details : subsCourses.getCourseDetails()) {
            if (!completed.contains(details.getTitle()))
                continue;
            OperationSupport<Course> courseMain = new OperationSupport<>();
            Course course = courseMain.getData(String.format(COURSE, details.getId()), Course.class);
            course.setTitle(details.getTitle());
            course.setId(details.getId());
            File file = new File("/home/sumit/udemy");
            course.save(file);
        }

        System.exit(0);
    }
}
