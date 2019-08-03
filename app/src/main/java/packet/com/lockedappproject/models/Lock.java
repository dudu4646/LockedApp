package packet.com.lockedappproject.models;

import java.io.Serializable;

public class Lock implements Comparable<Lock>, Serializable {
    public String name,status,id,admin,notAdmin;

    public Lock(String name, String status,String id,String admin,String notAdmin) {
        this.name = name;
        this.status = status;
        this.id=id;
        this.admin=admin;
        this.notAdmin = notAdmin;
    }

    public Lock() {
    }

    @Override
    public int compareTo(Lock o) {
        return this.name.compareToIgnoreCase(o.name);
    }
}