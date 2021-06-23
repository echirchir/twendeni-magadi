package org.etma.main.db;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class MemberRelationship extends RealmObject {

    @PrimaryKey
    @Index
    private long id;

    private String relationshipId;
    private String relationship;
    private String description;
    private String allowRegistration;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAllowRegistration() {
        return allowRegistration;
    }

    public void setAllowRegistration(String allowRegistration) {
        this.allowRegistration = allowRegistration;
    }
}
