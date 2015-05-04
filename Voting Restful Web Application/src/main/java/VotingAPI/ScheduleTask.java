package VotingAPI;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.UnknownHostException;
import java.util.ArrayList;

@Component
public class ScheduleTask  {

    //private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    //Moderator_Repository m_repo;
    public static final ModeratorController mc = new ModeratorController();

    @Scheduled(fixedRate = 300000)
    public void checkPollAndMessage() throws UnknownHostException {
        //KafkaProducer kp = new KafkaProducer();
        //kp.sendMessage(email,pollResult);

        //KafkaProducer.sendMessage();
        //String output = mc.hello();
        //System.out.println(output);
        ArrayList<String> result;
        result = mc.getExpiredPolls();
        //System.out.println(result.size());
        /*Iterator it = result.iterator();
        while(it.hasNext())
        {
            //Poll p = (Poll)it.next();

            System.out.println((String)it.next());

        }*/
              //new ModeratorController().getListOfPollExpire();
        //new ModeratorController().getEmailAndSendMessage();


   /* public void reportCurrentTime() {
        System.out.println("The time is now " + dateFormat.format(new Date()));
    }}*/
    }
}