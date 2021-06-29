package pl.edu.pja.nbd08;

import com.basho.riak.client.api.commands.kv.UpdateValue;

public class PersonUpdate extends UpdateValue.Update<Person> {

    private final Person update;

    public PersonUpdate(Person update){
        this.update = update;
    }

    @Override
    public Person apply(Person person) {
        if(person == null) {
            person = new Person();
        }

        person.setName(update.getName());
        person.setSurname(update.getSurname());
        person.setAge(update.getAge());
        person.setHeight(update.getHeight());

        return person;
    }
}
