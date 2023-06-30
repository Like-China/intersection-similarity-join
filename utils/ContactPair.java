package utils;

public class ContactPair {
    /** A nearest-neighbor. */
    public Data query;
    public Data db;

    /**
     * The distance from the nearest-neighbor to the query Data object
     * parameter.
     */
    public double distance;

    public ContactPair(Data query, Data db, Double dist) {
        this.query = query;
        this.db = db;
        this.distance = dist;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return query.toString() + " " + db.toString();
    }


}
