package predictions.model.db;

public class Team {

    private String community;
    private String name;
    private String description;
    private String owner;

    public Team(String community, String name, String description, String owner) {
        this.community = community;
        this.name = name;
        this.description = description;
        this.owner = owner;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
