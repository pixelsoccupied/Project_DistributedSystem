package log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    static Logger logger = Logger.getLogger("log");
    static FileHandler handler;


    static  public void generateLogFileServer(String msg, String clientType, String clientID) throws SecurityException, IOException {


        handler = new FileHandler("/Users/Np/Documents/IntellijProjects/Project_DistributedSystem/src/log/Log Files/"+clientType+"/"
                + clientID +".log",true);

        logger.addHandler(handler);
        SimpleFormatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);
        logger.log(Level.INFO, msg);
        handler.close();
    }

    static  public void generateLogFileClient(String msg, String clientType, String clientID) throws SecurityException, IOException {


        String folderServer = clientID.substring(0,2);

        handler = new FileHandler("/Users/Np/Documents/IntellijProjects/Corba_Banking/src/Log/Log Files/"+clientType+"/"+ folderServer+"/"
                + clientID +".log",true);

        logger.addHandler(handler);
        SimpleFormatter formatter = new SimpleFormatter();
        handler.setFormatter(formatter);
        logger.log(Level.INFO, msg);
        handler.close();
    }

}
