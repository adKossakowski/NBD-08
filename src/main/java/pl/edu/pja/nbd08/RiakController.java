package pl.edu.pja.nbd08;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.UpdateValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

@Log4j2
@RestController
@RequestMapping("/riak")
@RequiredArgsConstructor
public class RiakController {

    private RiakClient riakClient;

    @PostConstruct
    public void setUp() throws UnknownHostException {
        RiakCluster cluster = setUpCluster();
        riakClient = new RiakClient(cluster);
    }

    private static RiakCluster setUpCluster() throws UnknownHostException {
        RiakNode node = new RiakNode.Builder()
                .withRemoteAddress("localhost")
                .withRemotePort(8087)
                .build();

        RiakCluster cluster = new RiakCluster.Builder(node)
                .build();

        cluster.start();

        return cluster;
    }

    @PostMapping("/firstTask")
    public void firstTask() throws UnknownHostException, ExecutionException, InterruptedException {
        Person personToInsert = Person.builder()
                .name("Anna")
                .surname("Nowak")
                .age(22)
                .height(177.4)
                .build();

        Namespace personBucket = new Namespace("people");
        Location peopleLocation = new Location(personBucket, personToInsert.getName());

        StoreValue storeOp = new StoreValue.Builder(personToInsert)
                .withLocation(peopleLocation)
                .build();
        StoreValue.Response response = riakClient.execute(storeOp);

        FetchValue fetchedPerson = new FetchValue.Builder(peopleLocation)
                .build();
        Person fetchedObject = riakClient.execute(fetchedPerson).getValue(Person.class);
        log.info("Wypisanie pobranego obiektu o kluczu {}: {}", peopleLocation.getKey(), fetchedObject.toString());
    }

    @PutMapping("/secondTask")
    public void secondTask() throws ExecutionException, InterruptedException {
        Namespace personBucket = new Namespace("people");
        Location peopleLocation = new Location(personBucket, "Anna");

        FetchValue fetchedPerson = new FetchValue.Builder(peopleLocation)
                .build();
        Person personToUpdate = riakClient.execute(fetchedPerson).getValue(Person.class);
        personToUpdate.setSurname("Kowalska");
        PersonUpdate updatePerson = new PersonUpdate(personToUpdate);
        UpdateValue updateValue = new UpdateValue.Builder(peopleLocation)
                .withUpdate(updatePerson).build();
        UpdateValue.Response response = riakClient.execute(updateValue);

        fetchedPerson = new FetchValue.Builder(peopleLocation)
                .build();
        Person fetchedObject = riakClient.execute(fetchedPerson).getValue(Person.class);
        log.info("Wypisanie pobranego obiektu o kluczu {}: {}", peopleLocation.getKey(), fetchedObject.toString());
    }
    
    @DeleteMapping("/thirdTask")
    public void thirdTask() throws ExecutionException, InterruptedException {
        Namespace personBucket = new Namespace("people");
        Location peopleLocation = new Location(personBucket, "Anna");

        DeleteValue deleteOp = new DeleteValue.Builder(peopleLocation)
                .build();
        riakClient.execute(deleteOp);
        log.info("Object with key {} deleted", "Anna");

        try {
            FetchValue fetchedPerson = new FetchValue.Builder(peopleLocation)
                    .build();
            Person fetchedObject = riakClient.execute(fetchedPerson).getValue(Person.class);
            log.info("Wypisanie pobranego obiektu o kluczu {}: {}", peopleLocation.getKey(), fetchedObject.toString());
        }catch (NullPointerException e){
            log.error("Person with key {} not found", "Anna");
        }
    }
}
