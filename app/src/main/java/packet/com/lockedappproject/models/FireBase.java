package packet.com.lockedappproject.models;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FireBase {

    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static DatabaseReference ref = null, userRef = null, houseRef = null, lockRef = null;
    private static User user = null;
    private static ArrayList<Lock> userLocks;
    private static ArrayList<House> userHouses;
    private static HashMap<String, String> nicks;
    private static ArrayList<UpdateHouseData> updateHouse = new ArrayList<>();
    private static ArrayList<UpdateLockData> updateLocks = new ArrayList<>();


    //LISTENERS:
    private static ValueEventListener userListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            user = dataSnapshot.getValue(User.class);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener lockListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Lock lock = dataSnapshot.getValue(Lock.class);
            if (user.lockList.contains(lock.id)) {
                userLocks.add(lock);
                Collections.sort(userLocks);
                for (UpdateLockData u : updateLocks)
                    u.Notify();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Lock lock = dataSnapshot.getValue(Lock.class);

            if (lock.admin.contains(mAuth.getUid()) || lock.notAdmin.contains(mAuth.getUid())) {
                int i;
                for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                    ;
                if (i < userLocks.size()) {
                    userLocks.set(i, lock);
                }
            } else {
                int i;
                for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                    ;
                if (i < userLocks.size())
                    userLocks.remove(i);
            }
            Collections.sort(userLocks);
            for (UpdateLockData u : updateLocks)
                u.Notify();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            Lock lock = dataSnapshot.getValue(Lock.class);
            int i;
            for (i = 0; i < userLocks.size() && !userLocks.get(i).id.equalsIgnoreCase(lock.id); i++)
                ;
            if (i < userLocks.size()) {
                userLocks.remove(i);
                for (UpdateLockData u : updateLocks)
                    u.Notify();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    private static ChildEventListener houseListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            House house = dataSnapshot.getValue(House.class);
            if (user.houseList.contains(house.id)) {
                userHouses.add(house);
                Collections.sort(userHouses);
                for (UpdateHouseData u : updateHouse)
                    u.Notify();
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            House house = dataSnapshot.getValue(House.class);
            if (house.admin.contains(mAuth.getUid())) {
                int i;
                for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                    ;
                if (i < userHouses.size()) {
                    userHouses.set(i, house);
                }
            } else {
                int i;
                for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                    ;
                if (i < userHouses.size())
                    userLocks.remove(i);
            }
            Collections.sort(userHouses);
            for (UpdateHouseData u : updateHouse)
                u.Notify();
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            House house = dataSnapshot.getValue(House.class);
            int i;
            for (i = 0; i < userHouses.size() && !userHouses.get(i).id.equalsIgnoreCase(house.id); i++)
                ;
            if (i < userHouses.size()) {
                userHouses.remove(i);
                for (UpdateHouseData u : updateHouse)
                    u.Notify();
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //GETTERS:
        //get current user
    public static User getUser() {
        return user;
    }

        //get user ID
    public static String getUid() {
        return mAuth.getUid();
    }

        //get all the user houses
    public static ArrayList<House> getHouses() {
        return userHouses;
    }

        //get locks from single house
    public static ArrayList<Lock> getLockFromList(String list) {
        String[] locks = list.split(",");
        ArrayList<Lock> arr = new ArrayList<>();
        for (String s : locks) {
            for (Lock l : userLocks) {
                if (l.id.equalsIgnoreCase(s)) {
                    arr.add(l);
                }
            }
        }
        Collections.sort(arr);
        return arr;
    }

        //get single house
    public static House getOneHouse(String id) {
        for (House h : userHouses)
            if (h.id.equalsIgnoreCase(id))
                return h;
        return null;
    }

        //get lock from list by name/id
    public static Lock getLockByStr(String str){
        for (Lock lock:userLocks)
            if (lock.name.equalsIgnoreCase(str) || (lock.id.equalsIgnoreCase(str)))
                return lock;
        return null;
    }

        //get user email from nick
    public static String getEmailFromNick(String nick) {
        if (nicks.containsKey(nick))
            return nicks.get(nick);
        return null;
    }

    //METHODS:
        //Sign up
    public static void signUp(final String user, String pass, final UpdateUi u) {
        mAuth.signInWithEmailAndPassword(user, pass)
                .addOnCompleteListener((Activity) u, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            currentUser = mAuth.getCurrentUser();
                            if (userRef != null)
                                userRef.removeEventListener(userListener);
                            userRef = db.getReference("users").child(getUid());
                            userRef.addValueEventListener(userListener);
                            download();
                            u.Success();

                        } else {
                            // If sign in fails, display a message to the user.
                            u.Failed(task.getException());
                        }
                    }
                });
    }

        //Download data
    private static void download() {
        userHouses = new ArrayList<>();
        userLocks = new ArrayList<>();
        if (houseRef != null) {
            houseRef.removeEventListener(houseListener);
            lockRef.removeEventListener(lockListener);
        }
        houseRef = db.getReference("house");
        houseRef.addChildEventListener(houseListener);
        lockRef = db.getReference("locks");
        lockRef.addChildEventListener(lockListener);

    }

    //download all the users nicks
    public static void getNicks() {
        nicks = new HashMap<>();
        ref = db.getReference("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User u = dataSnapshot.getValue(User.class);
                nicks.put(u.nick, u.email);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                if (nicks.containsKey(u.nick))
                    nicks.remove(u.nick);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        //checking if nick is taken
    public static boolean checkNick(String s) {
        return nicks.containsKey(s);
    }

        //add listener for houses updates
    public static void addToUpdateHouse(UpdateHouseData adapter) {
        int place = updateHouse.indexOf(adapter);
        if (place > -1)
            updateHouse.set(place, adapter);
        else
            updateHouse.add(adapter);
    }

        //remove listener for houses updates
    public static void removeFromUpdateHouse(UpdateHouseData adapter) {
        if (updateHouse.contains(adapter))
            updateHouse.remove(adapter);
    }

        //add listener for locks updates
    public static void addToUpdateLocks(UpdateLockData adapter) {
        int place = updateLocks.indexOf(adapter);
        if (place > -1)
            updateLocks.set(place, adapter);
        else
            updateLocks.add(adapter);
    }

        //remove listener for locks updates
    public static void removeFromUpdateLock(UpdateLockData adapter) {
        if (updateLocks.contains(adapter))
            updateLocks.remove(adapter);
    }

        //checking if lock exists in the DB
    public static void lookForLock(final String id, final LookForLock callback) {
        lockRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                boolean exists = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equalsIgnoreCase(id)) {
                        Lock lock = snapshot.getValue(Lock.class);
                        //extracting the users name from the DB
                        getUsersNicks(lock.id, lock.admin, callback);
                        exists = true;
                    }
                    i++;
                }
                if (i == dataSnapshot.getChildrenCount() && !exists)
                    callback.lockDoesntExists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        //deleting lock
    public static void deleteLock(final Lock lock, String newAdmin) {
        ArrayList<String> locks = new ArrayList<>(Arrays.asList(user.lockList.split(",")));
        locks.remove(lock.id);
        String s = "";
        for (int i = 0; i < locks.size(); i++) {
            if (i == 0)
                s = locks.get(0);
            else
                s += "," + locks.get(i);
        }
        user.lockList = s;
        ref = db.getReference("users").child(getUid());
        ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                removeLockFromHouse(lock);
            }
        });
    }

        //deleting user from the lock users
    private static void removeUserFromLock(Lock lock) {
        ref = db.getReference("locks").child(lock.id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Lock lock = dataSnapshot.getValue(Lock.class);
                if (lock.admin.contains(mAuth.getUid())) {
                    ArrayList<String> temp = new ArrayList<>(Arrays.asList(lock.admin.split(",")));
                    temp.remove(mAuth.getUid());
                    String s = "";
                    for (int i = 0; i < temp.size(); i++)
                        if (i == 0)
                            s = temp.get(0);
                        else
                            s += "," + temp.get(i);
                    lock.admin = s;
                }
                if (lock.notAdmin.contains(mAuth.getUid())) {
                    ArrayList<String> temp = new ArrayList<>(Arrays.asList(lock.notAdmin.split(",")));
                    temp.remove(mAuth.getUid());
                    String s = "";
                    for (int i = 0; i < temp.size(); i++)
                        if (i == 0)
                            s = temp.get(0);
                        else
                            s += "," + temp.get(i);
                    lock.notAdmin = s;
                }
                if (lock.admin.length() == 0 && lock.notAdmin.length() == 0)
                    ref.removeValue();
                else
                    ref.setValue(lock);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

        //adding new lock
    public static void addNewLock(final String lId, final String lName, final String h, final UpdateUi callback) {

        ref = db.getReference("users").child(FireBase.getUid());
        User user = FireBase.getUser();
        //editing the user
        ArrayList<String> locks = new ArrayList<>(Arrays.asList(user.lockList.split(",")));
        locks.add(lId);
        Collections.sort(locks);
        String s = "";
        int i;
        for (i = 0; i < locks.size(); i++)
            if (i == 0)
                s = locks.get(0);
            else
                s += "," + locks.get(i);
        user.lockList = s;
        //writing the new user data
        ref.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //updating the house
                House house = FireBase.getOneHouse(h);
                house.locks = add_id_to_string(house.locks, lId);
                houseRef.child(h).setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Lock lock = new Lock(lName, "open", lId, FireBase.getUid(), "");
                        lockRef.child(lId).setValue(lock).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                callback.Success();
                            }
                        });
                    }
                });
            }
        });
    }

        //adding new house with new lock
    public static void add_house_and_lock(final String lockId, final String lockName, final String houseName, final String city, final String street, final Context context, final UpdateUi callack) {
        final DatabaseReference newRef = houseRef.push();
        //updating user
        User user = getUser();
        user.houseList = add_id_to_string(user.houseList, newRef.getKey());
        user.lockList = add_id_to_string(user.lockList, lockId);
        userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //creating house
                final House house = new House(houseName, street + ", " + city, lockId, 0, 0, getUid(), newRef.getKey());
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                List<Address> address = null;
                try {
                    address = geocoder.getFromLocationName(street + ", " + city, 1);
                    house.lat = (float) address.get(0).getLatitude();
                    house.lot = (float) address.get(0).getLongitude();
                } catch (Exception e) {
                    house.lat = 0f;
                    house.lot = 0f;
                }
                newRef.setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Creating the new lock
                        Lock lock = new Lock(lockName, "open", lockId, getUid(), "");
                        lockRef.child(lock.id).setValue(lock).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                callack.Success();
                            }
                        });
                    }
                });
            }
        });
    }

        //changing lock status
    public static void changeLockTo(Lock lock, String status) {
        lock.status = status;
        ref = db.getReference("locks").child(lock.id);
        ref.setValue(lock);
    }

    //PRIVATE METHODS:
    private static void getUsersNicks(final String id, String uId, final LookForLock callback) {
        final String arr[] = uId.split(",");
        final String str[] = {""};
        final int[] temp = {1};
        for (String s : arr) {
            db.getReference("users").child(s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    str[0] += user.nick;
                    if (temp[0]++ < arr.length)
                        str[0] += ",";

                    if (temp[0] > arr.length)
                        callback.lockExists(id, str[0]);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private static void removeLockFromHouse(final Lock lock) {
        int i;
        for (i = 0; i < userHouses.size() && !userHouses.get(i).locks.contains(lock.id); i++) ;
        final House house = userHouses.get(i);
        ArrayList<String> locks = new ArrayList<>(Arrays.asList(house.locks.split(",")));
        locks.remove(lock.id);
        String temp = "";
        for (i = 0; i < locks.size(); i++)
            if (i == 0)
                temp = locks.get(0);
            else
                temp += "," + locks.get(i);
        house.locks = temp;
        ref = db.getReference("house").child(house.id);
        ref.setValue(house).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                removeUserFromLock(lock);
            }
        });
    }

    private static String add_id_to_string(String src, String id) {
        src += (src.length() > 0) ? "," + id : id;
        ArrayList<String> arr = new ArrayList<>(Arrays.asList(src.split(",")));
        Collections.sort(arr);
        String s = "";
        for (int i = 0; i < arr.size(); i++)
            if (i == 0)
                s = arr.get(i);
            else
                s += "," + arr.get(i);
        return s;
    }




    //INTERFACES:
    public interface UpdateUi {
        void Success();

        void Failed(Exception e);
    }

    public interface UpdateHouseData {
        void Notify();
    }

    public interface UpdateLockData {
        void Notify();
    }

    public interface LookForLock {
        void lockExists(String id, String str);

        void lockDoesntExists();
    }

}
