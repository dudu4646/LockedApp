package packet.com.lockedappproject.models;

public class House implements Comparable<House>{
    public String name,address,admin,id;
    public float lot,lat;
    public String locks;

    public House(String name, String address, String locks, float lot, float lat,String admin,String id) {
        this.name = name;
        this.address = address;
        this.lot = lot;
        this.lat = lat;
        this.locks=locks;
        this.admin=admin;
        this.id=id;
    }

    public House() {
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(House o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}
