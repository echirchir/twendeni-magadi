package org.etma.main.db;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class Migration implements RealmMigration {
    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {

        final DynamicRealm realmInstance = realm;
        RealmSchema schema = realm.getSchema();

        RealmObjectSchema churchEvent = schema.get("ChurchEvent");

        if (oldVersion == 0){

            if (churchEvent == null){
                schema.create("ChurchEvent")
                        .addField("id", long.class)
                        .addField("post_id", int.class)
                        .addIndex("id")
                        .addField("title", String.class)
                        .addField("author", String.class)
                        .addField("content", String.class)
                        .addField("date", String.class)
                        .addField("img_url", String.class)
                        .addField("post_url", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.setLong("id", setupUniqueEventId(realmInstance));
                            }
                        }).addPrimaryKey("id");
            }

            oldVersion++;
        }
    }


    private long setupUniqueEventId(final DynamicRealm realm){
        Number num = realm.where("ChurchEvent").max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

}

