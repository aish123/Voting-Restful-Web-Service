package VotingAPI;

import com.fasterxml.jackson.annotation.JsonView;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mongodb.client.model.Filters.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

//import org.bson.Document;
//import static org.springframework.data.mongodb.core.query.Query;
//import static org.springframework.data.mongodb.core.query.Update;

@RestController
public class ModeratorController {

    String strUri = " mongodb://saiaishwarya:Govindha1@ds045521.mongolab.com:45521/cmpe273";
    MongoClientURI uri;
    MongoClient client;
    DB db;
    DBCollection store;
    @Autowired
    MongoOperations m_repo;
    //private Polling_Repository p_repo;

    AtomicInteger count = new AtomicInteger();
    AtomicInteger count_poll = new AtomicInteger(123456789);
    public static List expire_poll = new ArrayList<Poll>();

    //1. creating moderator
    @RequestMapping(name = "api/v1/moderators", method = RequestMethod.POST,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity method0(@Valid @RequestBody Moderator mod, BindingResult result) {

        List errorlist = new ArrayList();
        Moderator mod_new = new Moderator();

        if (mod.getName() != "" || mod.getName() != null) {

            mod_new.setName(mod.getName());
            mod_new.setEmail(mod.getEmail());
            mod_new.setPassword(mod.getPassword());
            mod_new.id = count.incrementAndGet();
            mod_new.setCreated_at();

            m_repo.save(mod_new);
            //moderator.putIfAbsent(mod_new.getid(), mod_new);
        }
        if (mod.getName() == "" || mod.getName() == null) {
            String strName = "Name field can not be empty/null";
            errorlist.add(strName);
        }

        if (result.hasErrors() || mod.getName() == "" || mod.getName() == null) {

            List<FieldError> fieldErrors = result.getFieldErrors();
            String error;
            for (FieldError fielderror : fieldErrors) {
                error = fielderror.getField() + "  " + fielderror.getDefaultMessage();
                errorlist.add(error);
            }
            return new ResponseEntity(errorlist, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<Moderator>(mod_new, HttpStatus.CREATED);
        }
    }
    //2. view moderator resource

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "api/v1/moderators/{id}", method = RequestMethod.GET)
    public ResponseEntity<Moderator> findModerator(@PathVariable("id") int id) {

        Query query = new Query(where("id").is(id));
        Moderator mod = m_repo.findOne(query, Moderator.class);
        HttpHeaders header = new HttpHeaders();
        header.add("Accept", "Application/json");
        return new ResponseEntity<Moderator>(mod, header, HttpStatus.OK);
    }

    //3. update moderator
    @RequestMapping(value = "api/v1/moderators/{id}", method = RequestMethod.PUT,
            consumes = "application/json", produces = "application/json")
    public ResponseEntity<Moderator> updateModerator(@PathVariable("id") int id, @Valid @RequestBody Moderator m, BindingResult result) {

        Query query = new Query(where("id").is(id));
        Moderator mod = m_repo.findOne(query, Moderator.class);
        mod.setEmail(m.getEmail());
        mod.setPassword(m.getPassword());

        if (result.hasErrors()) {
            List errorlist = new ArrayList();
            List<FieldError> fieldErrors = result.getFieldErrors();
            String error;
            for (FieldError fielderror : fieldErrors) {
                /*if(fielderror.getField().compareTo("name")==0)
                {
                    continue;
                }*/
                error = fielderror.getField() + "  " + fielderror.getDefaultMessage();
                errorlist.add(error);

            }
            return new ResponseEntity(errorlist, HttpStatus.BAD_REQUEST);
        } else {
            m_repo.save(mod);
            return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
        }
    }

    //4. create a poll
    @JsonView(View.results.class)
    @RequestMapping(value = "api/v1/moderators/{id}/polls", method = RequestMethod.POST)
    public ResponseEntity<Poll> addNewPoll(@PathVariable("id") int id, @Valid @RequestBody Poll poll, BindingResult result) {

        Poll p = new Poll();
        p.setQuestion(poll.getQuestion());
        p.setChoice(poll.getChoice());
        p.setStarted_at(poll.getstarted_at());
        p.setExpired_at(poll.getExpired_at());
        p.id = Integer.toString(count_poll.incrementAndGet(), 36);
        p.setMid(id);
        //m_repo.createCollection("Polls");
        //p.setMid(id);

        if (result.hasErrors()) {
            List errorlist = new ArrayList();
            List<FieldError> fieldErrors = result.getFieldErrors();
            String error;
            for (FieldError fielderror : fieldErrors) {
                error = fielderror.getField() + " " + fielderror.getDefaultMessage();
                errorlist.add(error);
            }

            return new ResponseEntity(errorlist, HttpStatus.BAD_REQUEST);
        } else {
            Query query = new Query(where("id").is(id));
            Update update = new Update().addToSet("Polls", p);
            //m_repo.updateFirst(query,update,Moderator.class);

            m_repo.save(p);
            return new ResponseEntity<Poll>(p, HttpStatus.CREATED);
        }
    }
    //5. View a poll without result

    @JsonView(View.results.class)
    @RequestMapping(value = "api/v1/polls/{id}", method = RequestMethod.GET)
    public ResponseEntity<Poll> viewPoll(@PathVariable("id") String id) {
        //HttpHeaders header = new HttpHeaders();
        //header.add("Accept","application/json");
        Query query = new Query(where("_id").is(id));
        Poll p = m_repo.findOne(query, Poll.class);
        return new ResponseEntity(p, HttpStatus.OK);
    }

    //6. View a poll with result
    @JsonView(View.viewwithresults.class)
    @RequestMapping(value = "api/v1/moderators/{moderator_id}/polls/{id}", method = RequestMethod.GET)
    public ResponseEntity viewPollWithResults(@PathVariable("moderator_id") int moderator_id, @PathVariable("id") String id) {

        //BasicQuery query = new BasicQuery("{$and:[{_id:'moderator_id'},{Polls._id:id}]}");
        Query query = new Query(where("mid").is(moderator_id).and("_id").is(id));
        Poll p = m_repo.findOne(query, Poll.class);
        return new ResponseEntity(p, HttpStatus.OK);
    }

    //7.List all polls
    @JsonView(View.viewwithresults.class)
    @RequestMapping(value = "api/v1/moderators/{id}/polls", method = RequestMethod.GET)
    public ResponseEntity viewPoll(@PathVariable("id") int id) {

        List all_Polls = new ArrayList();
        Query query = new Query(where("mid").is(id));
        query.fields().exclude("mid");
        all_Polls = m_repo.find(query, Poll.class);
        return new ResponseEntity(all_Polls, HttpStatus.OK);
    }

    //8.Delete a poll
    @RequestMapping(value = "api/v1/moderators/{moderator_id}/polls/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity deletePoll(@PathVariable("id") String id, @PathVariable("moderator_id") int mid) {

        Query query = new Query(where("mid").is(mid).and("_id").is(id));
        m_repo.remove(query, Poll.class);
        return new ResponseEntity("", HttpStatus.NO_CONTENT);
    }
    //9.Vote a poll

    @RequestMapping(value = "api/v1/polls/{id}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity doVote(@RequestParam("choice") int choice, @PathVariable("id") String id) {

        Query query = new Query(where("_id").is(id));
        Poll p = m_repo.findOne(query, Poll.class);

        if (p != null) {

            ArrayList<Integer> results = p.getResults();
            int toIncrement = results.get(choice);
            results.set(choice, ++toIncrement);
            p.setresults(results);
            m_repo.save(p);
            return new ResponseEntity("", HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity("Not valid Poll", HttpStatus.BAD_REQUEST);


    }

    //@RequestMapping(value = "/hello",method =  RequestMethod.GET)
    public List<Poll> getListOfPollExpire() {
        List<Poll> poll_list = new ArrayList<Poll>();
        ArrayList<String> arr2 = new ArrayList<String>();
        poll_list = m_repo.findAll(Poll.class);
        Iterator it = poll_list.iterator();
        while (it.hasNext()) {
            Poll p = (Poll) it.next();
            String expired_at = p.getExpired_at();
            Boolean result = DateComparison.checkExpire(expired_at);
            if (!result) {
                expire_poll.add(p);
            }
        }

        //if(!expire_poll.isEmpty())
        // arr2 = getEmailAndSendMessage();
        // return arr2;
        return expire_poll;
        //,HttpStatus.OK);
        // else
        // return; //new ResponseEntity(null);

    }

    //@RequestMapping(value = "/Hello2", method = RequestMethod.GET)
    public List<Poll> getEmailAndSendMessage() {

        List<Poll> poll_list = new ArrayList<Poll>();
        ArrayList<String> arr2 = new ArrayList<String>();
        poll_list = m_repo.findAll(Poll.class);


        return poll_list;
    }

    public ArrayList<String> getExpiredPolls() throws UnknownHostException{
        ArrayList<String> arr = new ArrayList<String>();
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        DB db = mongoClient.getDB("test");
        DBCollection collection = db.getCollection("poll");
        //DBCollection mcollection = db.getCollection("moderator");
        //MongoDatabase database = client.getDatabase("test");
        DBCollection mcollection = db.getCollection("moderator");

        try {
            DBCursor cursor = collection.find();
            while (cursor.hasNext()) {
                BasicDBObject mObj = (BasicDBObject) cursor.next();
                String string = mObj.getString("expired_at");
                arr.add(string);

                Boolean result = DateComparison.checkExpire(string);
                if (!result) {
                    int mid = mObj.getInt("mid");
                    String Mid = "MID:" + mid;
                    //arr.add(Mid);
                   /* Query query = new Query(where("id").is(mid));
                    Moderator mod = m_repo.findOne(query, Moderator.class);
                    String email = "EMAIL:"+ mod.getEmail();
                    arr.add(email);*/
                    /*String hello = "hello";
                    arr.add(hello);*/
                    //Bson filter = new org.bson.Document("id", mid);
                    //List<org.bson.Document> all = mcollection.find(filter).into(new ArrayList<org.bson.Document>());*/
                    BasicDBObject whereQuery = new BasicDBObject();
                    whereQuery.put("_id", mid);
                    BasicDBObject fields = new BasicDBObject();
                    fields.put("email",4);

                    DBCursor cursor1 = mcollection.find(whereQuery, fields);
                    String email = "";
                    while (cursor1.hasNext()) {

                        //System.out.println(cursor1.next());
                       BasicDBObject objTemp = (BasicDBObject) cursor1.next();
                        email = objTemp.getString("email");

                        //System.out.println(cursor1.next());
                    }
                    //arr.add(email);

                    String str = email + ":010022584:Poll Result [";
                    List<BasicDBObject> resultObj = (List<BasicDBObject>) mObj.get("results");
                    List<BasicDBObject> choiceObj = (List<BasicDBObject>) mObj.get("choice");
                    for (int i = 0; i < choiceObj.size(); i++) {
                        str = str + choiceObj.get(i) + "=" + resultObj.get(i) + ",";
                    }
                    str = str.substring(0, str.length() - 1);
                    str = str + "]";
                    arr.add(str);
                    KafkaProducer kp = new KafkaProducer();
                    kp.sendMessage(str);
                    //}
                }
            }
        }
                /*List<BasicDBObject> pObj = (List<BasicDBObject>) mObj.get("polls");
                for(BasicDBObject obj: pObj){
                    try{
                        String string = obj.getString("expired_at");
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
                        Date date1 = format.parse(string);
                        Date date2 = java.util.Calendar.getInstance().getTime();
                        if (date2.compareTo(date1)>= 0)    {
                            String str = mObj.getString("email") + ":010042721:Poll Result [";
                            List<BasicDBObject> resultObj = (List<BasicDBObject>) obj.get("results");
                            List<BasicDBObject> choiceObj = (List<BasicDBObject>) obj.get("choice");
                            for(int i=0; i<choiceObj.size(); i++ )
                            {
                                str = str + choiceObj.get(i) + "=" + resultObj.get(i) + "," ;
                            }
                            str = str.substring(0,str.length() - 1);
                            str = str + "]";
                            arr.add(str);
                        }

                    }*/ catch (Exception e) {

        }



        /*catch(Exception e){
            System.out.println(e.getStackTrace());
        }*/
        return arr;
    }
}


/*Iterator it = poll_list.iterator();
        while(it.hasNext())
        {
            Poll p = (Poll)it.next();
            String expired_at = p.getExpired_at();
            Boolean result = DateComparison.checkExpire(expired_at);
            if(!result)
            {
                expire_poll.add(p);
            }
        }*

        //List<Poll> pollList = getListOfPollExpire();
        ArrayList<String> arr = new ArrayList<String>();
        Iterator it1 = expire_poll.iterator();

        while (it1.hasNext()) {
            Poll p = (Poll) it1.next();
            Query query = new Query(where("id").is(p.getMid()));
            //arr.add(p.getMid());
            Moderator mod = m_repo.findOne(query, Moderator.class);
            String email = mod.getEmail();
            //arr.add(email);

            ArrayList results = p.getResults();
            ArrayList choice = p.getChoice();
            String pollResult = "PollResult:[" + choice.get(0) + "=" + results.get(0) + "," + choice.get(1) + "=" + results.get(1) + "]";
            System.out.println(pollResult);
            String msg = email + ":010022584:" + pollResult;
            arr.add(msg);
        }
            //KafkaProducer kp = new KafkaProducer();
            //kp.sendMessage(email,pollResult);
            //ScheduleTask.checkPollAndMessage(email,pollResult);
            //KafkaProducer.sendMessage(email, pollResult);
            return arr;

        }

    public String hello()
    {
         return "Hello";
    }
    public void makeConnection() {


        try {
            uri = new MongoClientURI(strUri);
            client = new MongoClient(uri);
            db = client.getDB("polling");
            store = db.getCollection("example");
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}*/