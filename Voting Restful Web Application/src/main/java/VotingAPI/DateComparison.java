package VotingAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateComparison {

    public static Boolean checkExpire(String dateInString)
    {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.format(currentDate);
        Boolean result= false;
        //String dateInString = "2014-09-16T13:30:06.419Z";
        //String dateInStringToCompare = "2014-09-16T13:28:06.419Z";

        try {

             Date date = formatter.parse(dateInString);
           // Date date2 = formatter.parse(dateInStringToCompare);
            //System.out.println(date);
            //System.out.println(formatter.format(date));
             result = date.after(currentDate);
            //System.out.println(result);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    }


