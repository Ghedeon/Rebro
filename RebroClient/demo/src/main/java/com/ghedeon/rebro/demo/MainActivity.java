package com.ghedeon.rebro.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ghedeon.rebro.demo.model.Company;
import com.ghedeon.rebro.demo.model.Domain;
import com.ghedeon.rebro.demo.model.Person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        final Button addPersonBtn = (Button) findViewById(R.id.addPersonBtn);
        // Push on update feature
        addPersonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                realm.beginTransaction();
                final Person person = realm.createObject(Person.class);
                person.setName(((TextView) findViewById(R.id.name)).getText().toString());
                person.setAge(Integer.parseInt(((TextView) findViewById(R.id.age)).getText().toString()));
                realm.commitTransaction();
            }
        });
        if (realm.isEmpty()) {
            createDb();
        }
    }

    private void createDb() {
        Log.d(TAG, "Creating realm DB");

        final String[] names = {"Barbara", "Ronald", "Stephanie", "Tina", "Judy", "Willie", "Heather", "Raymond", "Willie", "Mark"};
        final String[] companyNames = {"Quinu", "Thoughtstorm", "Shuffletag", "Einti", "Zoombeat", "Meetz", "Skiba", "Roomm", "Brightbean", "Photojam"};
        final String[] domainNames = {"bandcamp.com", "dailymail.co.uk", "homestead.com", "nature.com", "wired.com", "marriott.com", "cdc.gov", "blogger.com", "e-recht24.de", "instagram.com"};
        final Random random = new Random();
        realm.beginTransaction();

        for (int i = 0; i < 10; i++) {
            Person person = realm.createObject(Person.class);
            Domain domain = realm.createObject(Domain.class);
            Company company = realm.createObject(Company.class);

            person.setName(names[i]);
            person.setAge(random.nextInt(99) + 1);

            domain.setUrl(domainNames[i]);

            company.setName(companyNames[i]);
        }

        final RealmResults<Person> persons = realm.where(Person.class).findAll();
        final ArrayList<Person> shuffledPersons = new ArrayList<>(persons);
        Collections.shuffle(shuffledPersons);

        final RealmResults<Domain> domains = realm.where(Domain.class).findAll();
        final ArrayList<Domain> shuffledDomains = new ArrayList<>(domains);
        Collections.shuffle(shuffledDomains);

        final RealmResults<Company> findAll = realm.where(Company.class).findAll();
        for (int i = 0; i < findAll.size(); i++) {
            final Company company = findAll.get(i);
            company.setOwner(shuffledPersons.get(i));
            company.setDomain(shuffledDomains.get(i));
        }

        realm.commitTransaction();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
