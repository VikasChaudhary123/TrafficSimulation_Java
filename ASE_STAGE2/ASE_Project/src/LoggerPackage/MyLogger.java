package LoggerPackage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MyLogger {

	private static MyLogger instance;
    private static final String LOG_FILE = "log.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static StringBuilder logBuffer = new StringBuilder();

    private MyLogger() {
    }

    public static MyLogger getInstance() {
        if (instance == null) {
            instance = new MyLogger();
        }
        return instance;
    }

    public synchronized void log(String message) {   
        synchronized(DATE_FORMAT) {
            System.out.println(String.format("[%s] %s\n", DATE_FORMAT.format(new Date()), message));
            logBuffer.append(String.format("[%s] %s\n", DATE_FORMAT.format(new Date()), message));
        }
    }

    public void writeLogsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logBuffer.toString());
            logBuffer = new StringBuilder();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
}