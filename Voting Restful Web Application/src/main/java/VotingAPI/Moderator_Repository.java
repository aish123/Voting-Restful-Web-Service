package VotingAPI;

import org.springframework.data.mongodb.repository.MongoRepository;

//@RepositoryRestResource(collectionResourceRel = "example", path = "api/v1/moderators")
public interface Moderator_Repository extends MongoRepository<Moderator, String> {

    //List<Moderator> findByLastName(@Param("name") String name);

     //Poll findById(int id,String id);


}
